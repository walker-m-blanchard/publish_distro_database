import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access service for all Editing and Publishing operations against the GutenbergDb schema.
 *
 * Responsibilities:
 * 1. Create new book publications and periodical publications (magazines, journals)
 * 2. Update publication metadata such as title
 * 3. Assign and remove editors from publications via the Edits join table
 * 4. Retrieve all publications associated with a given editor
 * 5. Manage the table of contents for periodical issues (add/delete articles)
 * 6. Manage the table of contents for book editions (add/delete chapters)
 */
public class EditingPublishingService {
    private final Connection connection;

    /**
     * Constructs an EditingPublishingService with the given database connection.
     *
     * @param connection the active database connection used for all SQL operations.
     */
    public EditingPublishingService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new book publication into the Publications and Books tables in a single transaction.
     * Rolls back automatically if either insert fails.
     *
     * @param pubId    the unique identifier for the publication.
     * @param pubTitle the title of the book.
     * @param topic    the topic or subject area of the book.
     * @throws SQLException if any database operation fails.
     */
    public void enterNewBookPublication(Integer pubId, String pubTitle, String topic) throws SQLException {
        String insertPublicationSql =
                "INSERT INTO Publications (PubID, PubTitle, PubType, Topic) VALUES (?, ?, ?, ?)";
        String insertBookSql =
                "INSERT INTO Books (PubID) VALUES (?)";

        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            //Attempt to insert the publication
            try (PreparedStatement pubStmt = connection.prepareStatement(insertPublicationSql)) {
                pubStmt.setObject(1, pubId);
                pubStmt.setString(2, pubTitle);
                pubStmt.setString(3, "Book");
                pubStmt.setString(4, topic);
                pubStmt.executeUpdate();
            }

            //Attempt to insert the book
            try (PreparedStatement bookStmt = connection.prepareStatement(insertBookSql)) {
                bookStmt.setObject(1, pubId);
                bookStmt.executeUpdate();
            }
            //Commit transaction
            connection.commit();
        } catch (SQLException e) {
            //Transaction failed, rolling back
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Inserts a new periodical publication (magazine or journal) into the Publications and PeriodicalPubs
     * tables in a single transaction. Rolls back automatically if either insert fails.
     *
     * @param pubId       the unique identifier for the publication.
     * @param pubTitle    the title of the periodical.
     * @param pubType     the type of the periodical (e.g. Magazine, Journal).
     * @param topic       the topic or subject area of the periodical.
     * @param periodicity the publication frequency (e.g. Weekly, Monthly).
     * @throws SQLException if any database operation fails.
     */
    public void enterNewPeriodicalPublication(Integer pubId, String pubTitle, String pubType,
                                              String topic, String periodicity) throws SQLException {
        String insertPublicationSql =
                "INSERT INTO Publications (PubID, PubTitle, PubType, Topic) VALUES (?, ?, ?, ?)";
        String insertPeriodicalSql =
                "INSERT INTO PeriodicalPubs (PubID, Periodicity) VALUES (?, ?)";

        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            //Attempt to insert the publication
            try (PreparedStatement pubStmt = connection.prepareStatement(insertPublicationSql)) {
                pubStmt.setObject(1, pubId);
                pubStmt.setString(2, pubTitle);
                pubStmt.setString(3, pubType);
                pubStmt.setString(4, topic);
                pubStmt.executeUpdate();
            }

            //Attempt to insert the periodical
            try (PreparedStatement periodicalStmt = connection.prepareStatement(insertPeriodicalSql)) {
                periodicalStmt.setObject(1, pubId);
                periodicalStmt.setString(2, periodicity);
                periodicalStmt.executeUpdate();
            }
            //Commit transaction
            connection.commit();
        } catch (SQLException e) {
            //Transaction failed, rolling back
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Updates the title of an existing publication.
     *
     * @param pubId    the ID of the publication to update.
     * @param newTitle the new title to set.
     * @return the number of rows affected (1 if updated, 0 if not found).
     * @throws SQLException if the database update fails.
     */
    public int updatePublicationInformation(Integer pubId, String newTitle) throws SQLException {
        String sql = "UPDATE Publications SET PubTitle = ? WHERE PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newTitle);
            stmt.setObject(2, pubId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Assigns an editor to a publication by inserting a record into the Edits join table.
     *
     * @param employeeId the employee ID of the editor to assign.
     * @param pubId      the ID of the publication to assign the editor to.
     * @return the number of rows inserted (1 on success, 0 on failure).
     * @throws SQLException if the database insert fails.
     */
    public int assignEditorToPublication(Integer employeeId, Integer pubId) throws SQLException {
        String sql = "INSERT INTO Edits (EmployeeID, PubID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, employeeId);
            stmt.setObject(2, pubId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Removes an editor from a publication by deleting the corresponding record from the Edits join table.
     *
     * @param employeeId the employee ID of the editor to remove.
     * @param pubId      the ID of the publication to remove the editor from.
     * @return the number of rows deleted (1 if removed, 0 if no matching assignment was found).
     * @throws SQLException if the database delete fails.
     */
    public int removeEditorFromPublication(Integer employeeId, Integer pubId) throws SQLException {
        String sql = "DELETE FROM Edits WHERE EmployeeID = ? AND PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, employeeId);
            stmt.setObject(2, pubId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Returns all publications assigned to the specified editor, ordered by publication ID.
     *
     * @param employeeId the employee ID of the editor whose publications to retrieve.
     * @return a list of PublicationRecord objects representing the assigned publications.
     * @throws SQLException if the database query fails.
     */
    public List<PublicationRecord> getPublicationsByEditor(Integer employeeId) throws SQLException {
        String sql =
                "SELECT p.PubID, p.PubTitle, p.PubType, p.Topic " +
                        "FROM Publications p " +
                        "JOIN Edits e ON p.PubID = e.PubID " +
                        "WHERE e.EmployeeID = ? " +
                        "ORDER BY p.PubID";

        List<PublicationRecord> publications = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    publications.add(new PublicationRecord(
                            rs.getInt("PubID"),
                            rs.getString("PubTitle"),
                            rs.getString("PubType"),
                            rs.getString("Topic")
                    ));
                }
            }
        }

        return publications;
    }

    /**
     * Adds an article to a periodical issue by inserting a record into the IssueArticle join table.
     *
     * @param publicationUnitId the ID of the article (publication unit) to add.
     * @param issueId           the ID of the issue to add the article to.
     * @return the number of rows inserted (1 on success, 0 on failure).
     * @throws SQLException if the database insert fails.
     */
    public int addArticleToIssue(Integer publicationUnitId, Integer issueId) throws SQLException {
        String sql = "INSERT INTO IssueArticle (PublicationUnitID, IssueID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, publicationUnitId);
            stmt.setObject(2, issueId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Removes an article from a periodical issue by deleting the corresponding record from the IssueArticle join table.
     *
     * @param publicationUnitId the ID of the article to remove.
     * @param issueId           the ID of the issue to remove the article from.
     * @return the number of rows deleted (1 if removed, 0 if no matching relationship was found).
     * @throws SQLException if the database delete fails.
     */
    public int deleteArticleFromIssue(Integer publicationUnitId, Integer issueId) throws SQLException {
        String sql = "DELETE FROM IssueArticle WHERE PublicationUnitID = ? AND IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, publicationUnitId);
            stmt.setObject(2, issueId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Adds a chapter to a book edition by inserting a record into the EditionChapter join table.
     *
     * @param publicationUnitId the ID of the chapter (publication unit) to add.
     * @param isbn              the ISBN of the book edition to add the chapter to.
     * @return the number of rows inserted (1 on success, 0 on failure).
     * @throws SQLException if the database insert fails.
     */
    public int addChapterToEdition(Integer publicationUnitId, String isbn) throws SQLException {
        String sql = "INSERT INTO EditionChapter (PublicationUnitID, ISBN) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, publicationUnitId);
            stmt.setString(2, isbn);
            return stmt.executeUpdate();
        }
    }

    /**
     * Removes a chapter from a book edition by deleting the corresponding record from the EditionChapter join table.
     *
     * @param publicationUnitId the ID of the chapter to remove.
     * @param isbn              the ISBN of the book edition to remove the chapter from.
     * @return the number of rows deleted (1 if removed, 0 if no matching relationship was found).
     * @throws SQLException if the database delete fails.
     */
    public int deleteChapterFromEdition(Integer publicationUnitId, String isbn) throws SQLException {
        String sql = "DELETE FROM EditionChapter WHERE PublicationUnitID = ? AND ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, publicationUnitId);
            stmt.setString(2, isbn);
            return stmt.executeUpdate();
        }
    }

    /**
     * Record representing a Publication.
     */
    public static class PublicationRecord {
        private final Integer pubId;
        private final String pubTitle;
        private final String pubType;
        private final String topic;

        /**
         * Constructs a PublicationRecord with all publication fields.
         *
         * @param pubId    the publication's unique ID.
         * @param pubTitle the title of the publication.
         * @param pubType  the type of the publication (Book, Magazine, or Journal).
         * @param topic    the topic or subject area of the publication.
         */
        public PublicationRecord(Integer pubId, String pubTitle, String pubType, String topic) {
            this.pubId = pubId;
            this.pubTitle = pubTitle;
            this.pubType = pubType;
            this.topic = topic;
        }

        /**
         * Returns the publication's unique ID.
         *
         * @return the publication ID.
         */
        public Integer getPubId() {
            return pubId;
        }

        /**
         * Returns the title of the publication.
         *
         * @return the publication title.
         */
        public String getPubTitle() {
            return pubTitle;
        }

        /**
         * Returns the type of the publication.
         *
         * @return the publication type string.
         */
        public String getPubType() {
            return pubType;
        }

        /**
         * Returns the topic or subject area of the publication.
         *
         * @return the topic string.
         */
        public String getTopic() {
            return topic;
        }
    }
}
