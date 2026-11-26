import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * To be run on a database populated only by the sql file "demo_data"
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EditingPublishingServiceTest {

    private static DBManager dbManager;
    private static Connection connection;
    private static EditingPublishingService service;

    @BeforeAll
    static void setUp() throws Exception {
        dbManager = new DBManager();
        dbManager.connect();
        connection = dbManager.getConnection();
        service = new EditingPublishingService(connection);
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @AfterEach
    void cleanup() throws SQLException {
        deletePublicationCompletely(9001);
        deletePublicationCompletely(9002);

        deleteArticle(8, 2);
        deleteChapter(2, "978-1-119-54902-3");
    }

    @Test
    @Order(1)
    void testEnterNewBookPublication() throws Exception {
        service.enterNewBookPublication(9001, "JUnit Test Book", "Testing");

        assertTrue(publicationExists(9001));
        assertTrue(bookExists(9001));
    }

    @Test
    @Order(2)
    void testEnterNewPeriodicalPublication() throws Exception {
        service.enterNewPeriodicalPublication(9002, "JUnit Test Magazine", "Magazine", "Technology", "Weekly");

        assertTrue(publicationExists(9002));
        assertTrue(periodicalExists(9002));
    }

    @Test
    @Order(3)
    void testUpdatePublicationInformation() throws Exception {
        service.enterNewBookPublication(9001, "Old Title", "Testing");

        int rows = service.updatePublicationInformation(9001, "New Title");
        assertEquals(1, rows);
        assertEquals("New Title", getPublicationTitle(9001));
    }

    @Test
    @Order(4)
    void testAssignEditorToPublication() throws Exception {
        service.enterNewBookPublication(9001, "Editor Assignment Book", "Testing");

        int rows = service.assignEditorToPublication(1, 9001);
        assertEquals(1, rows);
        assertTrue(editExists(1, 9001));
    }

    @Test
    @Order(5)
    void testRemoveEditorFromPublication() throws Exception {
        service.enterNewBookPublication(9001, "Editor Removal Book", "Testing");
        service.assignEditorToPublication(1, 9001);

        int rows = service.removeEditorFromPublication(1, 9001);
        assertEquals(1, rows);
        assertFalse(editExists(1, 9001));
    }

    @Test
    @Order(6)
    void testGetPublicationsByEditor() throws Exception {
        service.enterNewBookPublication(9001, "Count Book", "Testing");
        service.assignEditorToPublication(1, 9001);

        List<EditingPublishingService.PublicationRecord> publications =
                service.getPublicationsByEditor(1);

        assertNotNull(publications);
        assertTrue(publications.stream().anyMatch(p -> p.getPubId() == 9001));
    }

    @Test
    @Order(7)
    void testAddAndDeleteArticleToIssue() throws Exception {
        int insertRows = service.addArticleToIssue(8, 2);
        assertEquals(1, insertRows);
        assertTrue(articleExists(8, 2));

        int deleteRows = service.deleteArticleFromIssue(8, 2);
        assertEquals(1, deleteRows);
        assertFalse(articleExists(8, 2));
    }

    @Test
    @Order(8)
    void testAddAndDeleteChapterToEdition() throws Exception {
        int insertRows = service.addChapterToEdition(1, "9781458300002");
        assertEquals(1, insertRows);
        assertTrue(chapterExists(1, "9781458300002"));

        int deleteRows = service.deleteChapterFromEdition(1, "9781458300002");
        assertEquals(1, deleteRows);
        assertFalse(chapterExists(1, "9781458300002"));
    }

    private boolean publicationExists(int pubId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Publications WHERE PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean bookExists(int pubId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Books WHERE PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean periodicalExists(int pubId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM PeriodicalPubs WHERE PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private String getPublicationTitle(int pubId) throws SQLException {
        String sql = "SELECT PubTitle FROM Publications WHERE PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString("PubTitle");
            }
        }
    }

    private boolean editExists(int employeeId, int pubId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Edits WHERE EmployeeID = ? AND PubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, pubId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean articleExists(int publicationUnitId, int issueId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM IssueArticle WHERE PublicationUnitID = ? AND IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitId);
            stmt.setInt(2, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean chapterExists(int publicationUnitId, String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM EditionChapter WHERE PublicationUnitID = ? AND ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitId);
            stmt.setString(2, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }


    private void deleteEdit(int employeeId, int pubId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Edits WHERE EmployeeID = ? AND PubID = ?")) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, pubId);
            stmt.executeUpdate();
        }
    }

    private void deleteArticle(int publicationUnitId, int issueId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM IssueArticle WHERE PublicationUnitID = ? AND IssueID = ?")) {
            stmt.setInt(1, publicationUnitId);
            stmt.setInt(2, issueId);
            stmt.executeUpdate();
        }
    }

    private void deleteChapter(int publicationUnitId, String isbn) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EditionChapter WHERE PublicationUnitID = ? AND ISBN = ?")) {
            stmt.setInt(1, publicationUnitId);
            stmt.setString(2, isbn);
            stmt.executeUpdate();
        }
    }

    private void deletePublicationCompletely(int pubId) throws SQLException {
        try (PreparedStatement stmt1 = connection.prepareStatement(
                "DELETE FROM Edits WHERE PubID = ?");
             PreparedStatement stmt2 = connection.prepareStatement(
                     "DELETE FROM PeriodicalPubs WHERE PubID = ?");
             PreparedStatement stmt3 = connection.prepareStatement(
                     "DELETE FROM Books WHERE PubID = ?");
             PreparedStatement stmt4 = connection.prepareStatement(
                     "DELETE FROM Publications WHERE PubID = ?")) {

            stmt1.setInt(1, pubId);
            stmt1.executeUpdate();

            stmt2.setInt(1, pubId);
            stmt2.executeUpdate();

            stmt3.setInt(1, pubId);
            stmt3.executeUpdate();

            stmt4.setInt(1, pubId);
            stmt4.executeUpdate();
        }
    }

    @BeforeEach
    void prepareCleanState() throws SQLException {
        deletePublicationCompletely(9001);
        deletePublicationCompletely(9002);

        deleteArticle(8, 2);
        deleteChapter(2, "9781458300002");
    }
}