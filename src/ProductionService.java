import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * The class handles the construction and execution of proper SQL statements for the production operations.
 * *
 * This class ensures database integrity for multi-statement transactions using manual commits and rollbacks.
 */
public class ProductionService {
    private final Connection connection;

    /**
     * Creates a new instance with provided the database connection.
     * @param connection the active database connection
     */
    public ProductionService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new record into the Editions table.
     * @param isbn          the unique ISBN of the edition (primary key)
     * @param editionNum    the edition number
     * @param writtenDate   the date the edition was written (can be null)
     * @param publishedDate the date the edition was published (can be null)
     * @param pubID         the ID of the associated publication
     * @throws SQLException if a database access error occurs
     */
    public void enterEdition(String isbn, Integer editionNum, Date writtenDate, Date publishedDate, Integer pubID)
            throws SQLException {
        String sql = "INSERT INTO Editions VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.setObject(2, editionNum);
            stmt.setDate(3, writtenDate);
            stmt.setDate(4, publishedDate);
            stmt.setObject(5, pubID);
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts a new record into the Issues table.
     * @param issueID       the unique ID of the issue (primary key)
     * @param issueTitle    the title of the issue (can be null)
     * @param producedDate  the date the issue was produced (can be null)
     * @param publishedDate the date the issue was published (can be null)
     * @param pubID         the ID of the associated publication
     * @throws SQLException if a database access error occurs
     */
    public void enterIssue(Integer issueID, String issueTitle, Date producedDate, Date publishedDate, Integer pubID)
            throws SQLException {
        String sql = "INSERT INTO Issues VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, issueID);
            stmt.setString(2, issueTitle);
            stmt.setDate(3, producedDate);
            stmt.setDate(4, publishedDate);
            stmt.setObject(5, pubID);
            stmt.executeUpdate();
        }
    }

    /**
     * Dynamically updates specified attributes of the edition with the given ISBN.
     * @param isbn          the ISBN of the edition to update
     * @param editionNum    the new edition number (optional)
     * @param writtenDate   the new written date (optional)
     * @param publishedDate the new published date (optional)
     * @return              the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int updateEdition(String isbn, Integer editionNum, Date writtenDate, Date publishedDate)
            throws SQLException {
        Map<String, Object> updateFields = new HashMap<>();
        if (editionNum != null) {
            updateFields.put("EditionNum", editionNum);
        }
        if (writtenDate != null) {
            updateFields.put("WrittenDate", writtenDate);
        }
        if (publishedDate != null) {
            updateFields.put("PublishedDate", publishedDate);
        }

        StringBuilder sql = new StringBuilder("UPDATE Editions SET ");
        List<Object> parameters = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, Object> item : updateFields.entrySet()) {
            sql.append(item.getKey()).append(" = ?");
            parameters.add(item.getValue());

            if (++count < updateFields.size()) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ").append("ISBN").append(" = ?");
        parameters.add(isbn);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 1; i <= parameters.size(); i++) {
                stmt.setObject(i, parameters.get(i - 1));
            }
            return stmt.executeUpdate();
        }
    }

    /**
     * Dynamically updates specified attributes for the issue with the given Issue ID.
     * @param issueID       the ID of the issue to update
     * @param issueTitle    the new issue title (optional)
     * @param producedDate  the new produced date (optional)
     * @param publishedDate the new published date (optional)
     * @return              the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int updateIssue(Integer issueID, String issueTitle, Date producedDate, Date publishedDate)
            throws SQLException {
        Map<String, Object> updateFields = new HashMap<>();
        if (issueTitle != null) {
            updateFields.put("IssueTitle", issueTitle);
        }
        if (producedDate != null) {
            updateFields.put("ProducedDate", producedDate);
        }
        if (publishedDate != null) {
            updateFields.put("PublishedDate", publishedDate);
        }

        StringBuilder sql = new StringBuilder("UPDATE Issues SET ");
        List<Object> parameters = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, Object> item : updateFields.entrySet()) {
            sql.append(item.getKey()).append(" = ?");
            parameters.add(item.getValue());

            if (++count < updateFields.size()) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ").append("IssueID").append(" = ?");
        parameters.add(issueID);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 1; i <= parameters.size(); i++) {
                stmt.setObject(i, parameters.get(i - 1));
            }
            return stmt.executeUpdate();
        }
    }

    /**
     * Deletes the edition and its records in the EditionChapter and Orders tables.
     * @param isbn          the ISBN of the edition to delete
     * @return              the number of rows affected in the Editions table
     * @throws SQLException if a database access error occurs; triggers a rollback
     */
    public int deleteEdition(String isbn) throws SQLException {
        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            //Attempt to remove the edition/chapter association referencing this edition
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM EditionChapter WHERE ISBN = ?")) {
                stmt.setString(1, isbn);
                stmt.executeUpdate();
            }

            //Attempt to remove the order associations referencing this edition
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Orders WHERE ISBN = ?")) {
                stmt.setString(1, isbn);
                stmt.executeUpdate();
            }

            //Values greater than 0 indicate successful location and removal
            int rows;
            //Attempt to remove the edition
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Editions WHERE ISBN = ?")) {
                stmt.setString(1, isbn);
                rows = stmt.executeUpdate();
            }
            //Commit transaction
            connection.commit();
            return rows;
        } catch (SQLException e) {
            //Transaction failed, rolling back
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Deletes the issue and its records in the IssueArticle and Orders tables.
     * @param issueID       the ID of the issue to delete
     * @return              the number of rows affected in the Issues table
     * @throws SQLException if a database access error occurs; triggers a rollback
     */
    public int deleteIssue(Integer issueID) throws SQLException {
        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            //Attempt to remove the issue/article association referencing this issue
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM IssueArticle WHERE IssueID = ?")) {
                stmt.setObject(1, issueID);
                stmt.executeUpdate();
            }

            //Attempt to remove the order associations referencing this issue
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Orders WHERE IssueID = ?")) {
                stmt.setObject(1, issueID);
                stmt.executeUpdate();
            }

            //Values greater than 0 indicate successful location and removal
            int rows;
            //Attempt to remove the issue
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Issues WHERE IssueID = ?")) {
                stmt.setObject(1, issueID);
                rows = stmt.executeUpdate();
            }
            //Commit transaction
            connection.commit();
            return rows;
        } catch (SQLException e) {
            //Transaction failed, rolling back
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Inserts a new record into the PublicationUnit table and inserts it into
     * either the Articles or Chapters table as appropriate.
     * @param publicationUnitID    the unique ID for the publication unit (primary key)
     * @param publicationUnitTitle the title of the publication unit (can be null)
     * @param publicationUnitType  the type of publication unit ("Article" or "Chapter")
     * @param writtenDate          the date the publication unit was written (can be null)
     * @param fullTextContent      the text which composes the publication unit (can be null)
     * @param topic                the publication unit topic (can be null)
     * @param employeeID           the ID of the author
     * @throws SQLException        if a database access error occurs or the type is invalid; triggers rollback
     */
    public void enterPublicationUnit(Integer publicationUnitID, String publicationUnitTitle, String publicationUnitType,
                                     Date writtenDate, String fullTextContent, String topic, Integer employeeID)
            throws SQLException {
        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            //Attempt to insert the publication unit
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO PublicationUnits VALUES(?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setObject(1, publicationUnitID);
                stmt.setString(2, publicationUnitTitle);
                stmt.setString(3, publicationUnitType);
                stmt.setDate(4, writtenDate);
                stmt.setString(5, fullTextContent);
                stmt.setString(6, topic);
                stmt.setObject(7, employeeID);
                stmt.executeUpdate();
            }

            //Attempt to insert either as a chapter or article
            if (publicationUnitType.equalsIgnoreCase("Article")) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO Articles VALUES(?)")) {
                    stmt.setObject(1, publicationUnitID);
                    stmt.executeUpdate();
                }
            } else if (publicationUnitType.equalsIgnoreCase("Chapter")) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO Chapters VALUES(?)")) {
                    stmt.setObject(1, publicationUnitID);
                    stmt.executeUpdate();
                }
            } else {
                throw new SQLException("Publication Unit Type must be Article or Chapter");
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
     * Dynamically updates specified attributes for the publication unit with the given Publication Unit ID.
     * @return              the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int updatePublicationUnit(Integer publicationUnitID, String publicationUnitTitle, Date writtenDate,
                                      String fullTextContent, String topic, Integer employeeID) throws SQLException {
        Map<String, Object> updateFields = new HashMap<>();
        if (publicationUnitTitle != null) {
            updateFields.put("PublicationUnitTitle", publicationUnitTitle);
        }
        if (writtenDate != null) {
            updateFields.put("WrittenDate", writtenDate);
        }
        if (fullTextContent != null) {
            updateFields.put("FullTextContent", fullTextContent);
        }
        if (topic != null) {
            updateFields.put("Topic", topic);
        }
        if (employeeID != null) {
            updateFields.put("EmployeeID", employeeID);
        }

        StringBuilder sql = new StringBuilder("UPDATE PublicationUnits SET ");
        List<Object> parameters = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, Object> item : updateFields.entrySet()) {
            sql.append(item.getKey()).append(" = ?");
            parameters.add(item.getValue());

            if (++count < updateFields.size()) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ").append("PublicationUnitID").append(" = ?");
        parameters.add(publicationUnitID);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 1; i <= parameters.size(); i++) {
                stmt.setObject(i, parameters.get(i - 1));
            }
            return stmt.executeUpdate();
        }
    }

    /**
     * Searches for publication units which match the given topic.
     * @param topic         the topic to search for
     * @return              a list of matching PublicationUnitRecords
     * @throws SQLException if a database access error occurs
     */
    public List<PublicationUnitRecord> searchTopic(String topic) throws SQLException {
        String sql = "SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType, pu.Topic, " +
                "pu.WrittenDate, e.Name AS AuthorName FROM PublicationUnits pu " +
                "JOIN Employees e ON pu.EmployeeID = e.EmployeeID " +
                "WHERE pu.Topic = ?;";
        List<PublicationUnitRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, topic);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new PublicationUnitRecord(
                            rs.getInt("PublicationUnitID"),
                            rs.getString("PublicationUnitTitle"),
                            rs.getString("PublicationUnitType"),
                            rs.getString("Topic"),
                            rs.getDate("WrittenDate"),
                            rs.getString("AuthorName")
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Searches for publication units written between the given dates.
     * @param startDate     the start of the date range
     * @param endDate       the end of the date range
     * @return              a list of matching PublicationUnitRecords
     * @throws SQLException if a database access error occurs
     */
    public List<PublicationUnitRecord> searchDate(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType, pu.Topic, " +
                "pu.WrittenDate, e.Name AS AuthorName FROM PublicationUnits pu " +
                "JOIN Employees e ON pu.EmployeeID = e.EmployeeID " +
                "WHERE pu.WrittenDate BETWEEN ? AND ?";
        List<PublicationUnitRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new PublicationUnitRecord(
                            rs.getInt("PublicationUnitID"),
                            rs.getString("PublicationUnitTitle"),
                            rs.getString("PublicationUnitType"),
                            rs.getString("Topic"),
                            rs.getDate("WrittenDate"),
                            rs.getString("AuthorName")
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Searches for publication units which match the given author.
     * @param authorName    the name of the author
     * @return              a list of matching PublicationUnitRecords
     * @throws SQLException if a database access error occurs
     */
    public List<PublicationUnitRecord> searchAuthor(String authorName) throws SQLException {
        String sql = "SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType, pu.Topic, " +
                "pu.WrittenDate, e.Name AS AuthorName FROM PublicationUnits pu " +
                "JOIN Employees e ON pu.EmployeeID = e.EmployeeID " +
                "WHERE e.name = ?";
        List<PublicationUnitRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, authorName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new PublicationUnitRecord(
                            rs.getInt("PublicationUnitID"),
                            rs.getString("PublicationUnitTitle"),
                            rs.getString("PublicationUnitType"),
                            rs.getString("Topic"),
                            rs.getDate("WrittenDate"),
                            rs.getString("AuthorName")
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Inserts a new record into the Payments table and inserts it into the EmployeePayment table as well.
     * @param paymentID         the unique ID of the payment (primary key)
     * @param amount            the amount being paid
     * @param claimDate         the date the payment was claimed (can be null)
     * @param issueDate         the date the payment was issued
     * @param employeeID        the ID of the employee being paid
     * @param publicationUnitID the ID of the deliverable being paid for (can be null)
     * @throws SQLException     if a database access error occurs; triggers rollback
     */
    public void enterEmployeePayment(Integer paymentID, Double amount, Date claimDate, Date issueDate, Integer employeeID,
                                     Integer publicationUnitID) throws SQLException {
        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            //Attempt to insert the payment
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Payments VALUES (?, ?, ?)")) {
                stmt.setObject(1, paymentID);
                stmt.setObject(2, amount);
                stmt.setDate(3, claimDate);
                stmt.executeUpdate();
            }

            //Attempt to insert the employee payment
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO EmployeePayments VALUES (?, ?, ?, ?)")) {
                stmt.setObject(1, paymentID);
                stmt.setDate(2, issueDate);
                stmt.setObject(3, employeeID);
                stmt.setObject(4, publicationUnitID);
                stmt.executeUpdate();
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
     * Updates the claim date for an existing unclaimed payment.
     * @param paymentID     unique ID of the payment
     * @param claimDate     the date the payment was claimed
     * @return              the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int updateEmployeePayment(Integer paymentID, Date claimDate) throws SQLException {
        String sql = "UPDATE Payments SET ClaimDate = ? WHERE PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, claimDate);
            stmt.setObject(2, paymentID);
            return stmt.executeUpdate();
        }
    }

    /**
     * Searches for employee payments which have not been claimed within the given date range.
     * @param startDate     the start of the issue date range
     * @param endDate       the end of the issue date range
     * @return              a list of PaymentRecords which have not been claimed
     * @throws SQLException if a database access error occurs
     */
    public List<PaymentRecord> searchEmployeePayments(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT p.PaymentID, p.Amount, ep.IssueDate, ep.EmployeeID, ep.PublicationUnitID FROM Payments p " +
                "JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID " +
                "WHERE p.ClaimDate IS NULL AND ep.IssueDate BETWEEN ? AND ?";
        List<PaymentRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new PaymentRecord(
                            rs.getInt("PaymentID"),
                            rs.getDouble("Amount"),
                            rs.getDate("IssueDate"),
                            rs.getInt("EmployeeID"),
                            rs.getInt("PublicationUnitID")
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Retrieves the data on articles which are present in either of two issues.
     * @param issue1        the ID of the first issue
     * @param issue2        the ID of the second issue
     * @return              a list of IssueRecords for the specified issues
     * @throws SQLException if a database access error occurs
     */
    public List<IssueRecord> compareIssues(Integer issue1, Integer issue2) throws SQLException {
        String sql = "SELECT i.IssueID, i.IssueTitle, pu.PublicationUnitID, pu.PublicationUnitTitle, pu.Topic, " +
                "pu.WrittenDate, e.Name AS AuthorName FROM Issues i " +
                "JOIN IssueArticle a ON i.IssueID = a.IssueID " +
                "JOIN PublicationUnits pu ON a.PublicationUnitID = pu.PublicationUnitID " +
                "JOIN Employees e ON pu.EmployeeID = e.EmployeeID WHERE i . IssueID IN (? , ?) " +
                "ORDER BY i.IssueID, pu.PublicationUnitID;";
        List<IssueRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, issue1);
            stmt.setObject(2, issue2);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new IssueRecord(
                            rs.getInt("IssueID"),
                            rs.getString("IssueTitle"),
                            rs.getInt("PublicationUnitID"),
                            rs.getString("PublicationUnitTitle"),
                            rs.getString("Topic"),
                            rs.getDate("WrittenDate"),
                            rs.getString("AuthorName")
                    ));
                }
            }
        }
        return results;
    }

    // -------------------------------------------------------------------------
    // Record classes
    // -------------------------------------------------------------------------

    /**
     * Record representing a Publication Unit.
     */
    public static class PublicationUnitRecord {
        private final Integer publicationUnitID;
        private final String publicationUnitTitle;
        private final String publicationUnitType;
        private final String topic;
        private final Date writtenDate;
        private final String authorName;

        public PublicationUnitRecord(Integer publicationUnitID, String publicationUnitTitle, String publicationUnitType,
                                     String topic, Date writtenDate, String authorName) {
            this.publicationUnitID = publicationUnitID;
            this.publicationUnitTitle = publicationUnitTitle;
            this.publicationUnitType = publicationUnitType;
            this.topic = topic;
            this.writtenDate = writtenDate;
            this.authorName = authorName;
        }

        /** @return the ID of the publication unit */
        public Integer getPublicationUnitID() { return publicationUnitID; }
        /** @return the title of the publication unit */
        public String getPublicationUnitTitle() { return publicationUnitTitle; }
        /** @return the publication unit type ("Chapter" or "Article") */
        public String getPublicationUnitType() { return publicationUnitType; }
        /** @return the topic of the publication unit */
        public String getTopic() { return topic; }
        /** @return the date the publication unit was written */
        public Date getWrittenDate() { return writtenDate; }
        /** @return the author of the publication unit */
        public String getAuthorName() { return authorName; }
    }

    /**
     * Record representing an employee payment.
     */
    public static class PaymentRecord {
        private final Integer paymentID;
        private final Double amount;
        private final Date issueDate;
        private final Integer employeeID;
        private final Integer publicationUnitID;

        public PaymentRecord(Integer paymentID, Double amount, Date issueDate, Integer employeeID, Integer publicationUnitID) {
            this.paymentID = paymentID;
            this.amount = amount;
            this.issueDate = issueDate;
            this.employeeID = employeeID;
            this.publicationUnitID = publicationUnitID;
        }

        /** @return the ID of the payment */
        public Integer getPaymentID() { return paymentID; }
        /** @return the amount being paid */
        public Double getAmount() { return amount; }
        /** @return the date the payment was issued */
        public Date getIssueDate() { return issueDate; }
        /** @return the ID of the employee being paid */
        public Integer getEmployeeID() { return employeeID; }
        /** @return the ID of the deliverable being paid for */
        public Integer getPublicationUnitID() { return publicationUnitID; }
    }

    /**
     * Record representing an article within a specific issue.
     */
    public static class IssueRecord {
        private final Integer issueID;
        private final String issueTitle;
        private final Integer publicationUnitID;
        private final String publicationUnitTitle;
        private final String topic;
        private final Date writtenDate;
        private final String authorName;

        public IssueRecord(Integer issueID, String issueTitle, Integer publicationUnitID, String publicationUnitTitle,
                           String topic, Date writtenDate, String authorName) {
            this.issueID = issueID;
            this.issueTitle = issueTitle;
            this.publicationUnitID = publicationUnitID;
            this.publicationUnitTitle = publicationUnitTitle;
            this.topic = topic;
            this.writtenDate = writtenDate;
            this.authorName = authorName;
        }

        /** @return the ID of the issue. */
        public Integer getIssueID() {return  issueID; }
        /** @return the title of the issue. */
        public String getIssueTitle() {return issueTitle; }
        /** @return the ID of the publication unit */
        public Integer getPublicationUnitID() { return publicationUnitID; }
        /** @return the title of the publication unit */
        public String getPublicationUnitTitle() { return publicationUnitTitle; }
        /** @return the topic of the publication unit */
        public String getTopic() { return topic; }
        /** @return the date the publication unit was written */
        public Date getWrittenDate() { return writtenDate; }
        /** @return the author of the publication unit */
        public String getAuthorName() { return authorName; }
    }
}
