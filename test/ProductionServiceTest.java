import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * To be run on a database populated with no data
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductionServiceTest {
    private static DBManager dbManager;
    private static Connection connection;
    private static ProductionService pService;
    private static EditingPublishingService eService;

    private static final String TEST_ISBN_1 = "9001";
    private static final String TEST_ISBN_2 = "9002";
    private static final int TEST_ISSUE_ID_1 = 9001;
    private static final int TEST_ISSUE_ID_2 = 9002;
    private static final int TEST_PAYMENT_ID_1 = 9001;
    private static final int TEST_PAYMENT_ID_2 = 9002;
    private static final int TEST_PAYMENT_ID_3 = 9003;
    private static final int TEST_EMPLOYEE_ID_1 = 9001;
    private static final int TEST_EMPLOYEE_ID_2 = 9002;
    private static final int TEST_ARTICLE_ID_1 = 9001;
    private static final int TEST_ARTICLE_ID_2 = 9003;
    private static final int TEST_CHAPTER_ID_1 = 9002;
    private static final int TEST_CHAPTER_ID_2 = 9004;
    private static final int TEST_BOOK_ID_1 = 9001;
    private static final int TEST_PERIODICAL_ID_1 = 9002;
    private static final Date TEST_DATE_1 = Date.valueOf("2026-04-01");
    private static final Date TEST_DATE_2 = Date.valueOf("2026-04-02");
    private static final Date TEST_DATE_3 = Date.valueOf("2026-04-03");
    private static final Date TEST_DATE_5 = Date.valueOf("2026-04-05");
    private static final Date TEST_DATE_6 = Date.valueOf("2026-04-06");
    private static final Date TEST_DATE_7 = Date.valueOf("2026-04-07");
    private static final Date TEST_DATE_8 = Date.valueOf("2026-04-08");
    private static final Date TEST_DATE_9 = Date.valueOf("2026-04-09");
    private static final String TEST_NAME = "test name";

    @BeforeAll
    static void setUp() throws Exception {
        dbManager = new DBManager();
        dbManager.connect();
        connection = dbManager.getConnection();
        pService = new ProductionService(connection);
        eService = new EditingPublishingService(connection);
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @BeforeEach
    void prepareCleanState() throws SQLException {
        cleanupTestData();
        eService.enterNewBookPublication(TEST_BOOK_ID_1, "Test book title", null);
        eService.enterNewPeriodicalPublication(TEST_PERIODICAL_ID_1, "Test periodical title", "Journal", null, "Monthly");

        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Employees VALUES (9001, ?, 0)")) {
                stmt.setString(1, TEST_NAME);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Authors VALUES (9001)")) {
                stmt.executeUpdate();
            }
        } catch (SQLException e){
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @AfterEach
    void cleanup() throws SQLException {
        cleanupTestData();
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    void enterEdition() throws Exception {
        assertFalse(editionExists(TEST_ISBN_1));
        pService.enterEdition(TEST_ISBN_1, 10, TEST_DATE_2, TEST_DATE_3, TEST_BOOK_ID_1);
        assertTrue(editionExists(TEST_ISBN_1));
    }

    @Test
    @Order(2)
    void enterIssue() throws Exception {
        assertFalse(issueExists(TEST_ISSUE_ID_1));
        pService.enterIssue(TEST_ISSUE_ID_1, "Test issue title", TEST_DATE_2, TEST_DATE_3, TEST_PERIODICAL_ID_1);
        assertTrue(issueExists(TEST_ISSUE_ID_1));
    }

    @Test
    @Order(3)
    void updateEdition() throws Exception {
        pService.enterEdition(TEST_ISBN_1, 10, null, null, TEST_BOOK_ID_1);
        assertEquals(10, getEditionNum(TEST_ISBN_1));
        pService.updateEdition(TEST_ISBN_1, 9, TEST_DATE_2, TEST_DATE_3);
        assertEquals(9, getEditionNum(TEST_ISBN_1));

        //Confirming existing attribute values won't change if null value passed in update
        assertEquals(TEST_DATE_2, getEditionWrittenDate(TEST_ISBN_1));
        assertEquals(TEST_DATE_3, getEditionPublishedDate(TEST_ISBN_1));
        pService.updateEdition(TEST_ISBN_1, null, TEST_DATE_1, null);
        assertEquals(TEST_DATE_1, getEditionWrittenDate(TEST_ISBN_1));
        assertEquals(TEST_DATE_3, getEditionPublishedDate(TEST_ISBN_1));
    }

    @Test
    @Order(4)
    void updateIssue() throws Exception {
        pService.enterIssue(TEST_ISSUE_ID_1, "Test issue title", null, null, TEST_PERIODICAL_ID_1);
        assertEquals("Test issue title", getIssueTitle(TEST_ISSUE_ID_1));
        pService.updateIssue(TEST_ISSUE_ID_1, "Changed issue title", TEST_DATE_2, TEST_DATE_3);
        assertEquals("Changed issue title", getIssueTitle(TEST_ISSUE_ID_1));

        //Confirming existing attribute values won't change if null value passed in update
        assertEquals(TEST_DATE_2, getIssueProducedDate(TEST_ISSUE_ID_1));
        assertEquals(TEST_DATE_3, getIssuePublishedDate(TEST_ISSUE_ID_1));
        pService.updateIssue(TEST_ISSUE_ID_1, null, TEST_DATE_1, null);
        assertEquals(TEST_DATE_1, getIssueProducedDate(TEST_ISSUE_ID_1));
        assertEquals(TEST_DATE_3, getIssuePublishedDate(TEST_ISSUE_ID_1));
    }

    @Test
    @Order(5)
    void deleteEdition() throws Exception {
        pService.enterEdition(TEST_ISBN_1, 10, TEST_DATE_2, TEST_DATE_3, TEST_BOOK_ID_1);
        assertTrue(editionExists(TEST_ISBN_1));
        pService.deleteEdition(TEST_ISBN_1);
        assertFalse(editionExists(TEST_ISBN_1));
    }

    @Test
    @Order(6)
    void deleteIssue() throws Exception {
        pService.enterIssue(TEST_ISSUE_ID_1, "Test issue title", TEST_DATE_2, TEST_DATE_3, TEST_PERIODICAL_ID_1);
        assertTrue(issueExists(TEST_ISSUE_ID_1));
        pService.deleteIssue(TEST_ISSUE_ID_1);
        assertFalse(issueExists(TEST_ISSUE_ID_1));
    }

    @Test
    @Order(7)
    void enterPublicationUnit() throws Exception {
        assertFalse(publicationUnitExists(TEST_ARTICLE_ID_1));
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        assertTrue(publicationUnitExists(TEST_ARTICLE_ID_1));
        assertTrue(articleExists(TEST_ARTICLE_ID_1));

        assertFalse(publicationUnitExists(TEST_CHAPTER_ID_1));
        pService.enterPublicationUnit(TEST_CHAPTER_ID_1, "Test chapter title", "Chapter", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        assertTrue(publicationUnitExists(TEST_CHAPTER_ID_1));
        assertTrue(chapterExists(TEST_CHAPTER_ID_1));
    }

    @Test
    @Order(8)
    void updatePublicationUnit() throws Exception {
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article", null, null, null, TEST_EMPLOYEE_ID_1);
        assertEquals("Test article title", getPublicationUnitTitle(TEST_ARTICLE_ID_1));
        //Confirming existing attribute values won't change if null value passed in update
        assertEquals(TEST_EMPLOYEE_ID_1, getEmployeeID(TEST_ARTICLE_ID_1));
        pService.updatePublicationUnit(TEST_ARTICLE_ID_1, "Changed article title", TEST_DATE_2, null, "test topic", null);
        assertEquals("Changed article title", getPublicationUnitTitle(TEST_ARTICLE_ID_1));
        assertEquals(TEST_EMPLOYEE_ID_1, getEmployeeID(TEST_ARTICLE_ID_1));
    }

    @Test
    @Order(9)
    void searchTopic() throws Exception {
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article",
                TEST_DATE_3, null, "Fantasy", TEST_EMPLOYEE_ID_1);
        pService.enterPublicationUnit(TEST_CHAPTER_ID_1, "Test chapter title", "Chapter",
                TEST_DATE_8, null, "Fantasy", TEST_EMPLOYEE_ID_1);

        List<ProductionService.PublicationUnitRecord> results =
                pService.searchTopic("Fantasy");

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertTrue(results.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_1));
        assertTrue(results.stream().allMatch(r -> r.getTopic().equals("Fantasy")));
    }

    @Test
    @Order(10)
    void searchDate() throws Exception {
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article",
                TEST_DATE_3, null, "Fantasy", TEST_EMPLOYEE_ID_1);
        pService.enterPublicationUnit(TEST_CHAPTER_ID_1, "Test chapter title", "Chapter",
                TEST_DATE_7, null, "Fantasy", TEST_EMPLOYEE_ID_1);

        List<ProductionService.PublicationUnitRecord> results1 =
                pService.searchDate(TEST_DATE_2, TEST_DATE_6);

        assertNotNull(results1);
        assertTrue(results1.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertFalse(results1.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_1));
        assertTrue(results1.stream().allMatch(r -> r.getWrittenDate().after(TEST_DATE_1)));
        assertTrue(results1.stream().allMatch(r -> r.getWrittenDate().before(TEST_DATE_7)));

        List<ProductionService.PublicationUnitRecord> results2 =
                pService.searchDate(TEST_DATE_2, TEST_DATE_8);

        assertNotNull(results2);
        assertTrue(results2.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertTrue(results2.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_1));
        assertTrue(results2.stream().allMatch(r -> r.getWrittenDate().after(TEST_DATE_1)));
        assertTrue(results2.stream().allMatch(r -> r.getWrittenDate().before(TEST_DATE_9)));
    }

    @Test
    @Order(11)
    void searchAuthor() throws Exception {
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article",
                TEST_DATE_3, null, "Fantasy", TEST_EMPLOYEE_ID_1);
        pService.enterPublicationUnit(TEST_CHAPTER_ID_1, "Test chapter title", "Chapter",
                TEST_DATE_7, null, "Fantasy", TEST_EMPLOYEE_ID_1);

        List<ProductionService.PublicationUnitRecord> results =
                pService.searchAuthor(TEST_NAME);

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertTrue(results.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_1));
        assertTrue(results.stream().allMatch(r -> r.getAuthorName().equals(TEST_NAME)));
    }

    @Test@Order(12)
    void enterEmployeePayment() throws Exception {
        assertFalse(paymentExists(TEST_PAYMENT_ID_1));
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        pService.enterEmployeePayment(TEST_PAYMENT_ID_1, 100.00, null, TEST_DATE_2, TEST_EMPLOYEE_ID_1, TEST_ARTICLE_ID_1);
        assertTrue(paymentExists(TEST_PAYMENT_ID_1));
        assertTrue(employeePaymentExists(TEST_PAYMENT_ID_1));
    }

    @Test
    @Order(13)
    void updateEmployeePayment() throws Exception {
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        pService.enterEmployeePayment(TEST_PAYMENT_ID_1, 100.00, null, TEST_DATE_2, TEST_EMPLOYEE_ID_1, TEST_ARTICLE_ID_1);
        assertNull(getPaymentClaimDate(TEST_PAYMENT_ID_1));
        pService.updateEmployeePayment(TEST_PAYMENT_ID_1, TEST_DATE_3);
        assertEquals(TEST_DATE_3, getPaymentClaimDate(TEST_PAYMENT_ID_1));
    }

    @Test
    @Order(14)
    void searchEmployeePayments() throws Exception {
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title", "Article", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        pService.enterPublicationUnit(TEST_CHAPTER_ID_1, "Test chapter title 1", "Chapter", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        pService.enterPublicationUnit(TEST_CHAPTER_ID_2, "Test chapter title 2", "Chapter", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        pService.enterEmployeePayment(TEST_PAYMENT_ID_1, 100.00, null, TEST_DATE_3, TEST_EMPLOYEE_ID_1, TEST_ARTICLE_ID_1);
        pService.enterEmployeePayment(TEST_PAYMENT_ID_2, 200.00, null, TEST_DATE_7, TEST_EMPLOYEE_ID_1, TEST_CHAPTER_ID_1);
        pService.enterEmployeePayment(TEST_PAYMENT_ID_3, 300.00, TEST_DATE_7, TEST_DATE_5, TEST_EMPLOYEE_ID_1, TEST_CHAPTER_ID_1);



        List<ProductionService.PaymentRecord> results1 =
                pService.searchEmployeePayments(TEST_DATE_2, TEST_DATE_6);

        assertNotNull(results1);
        assertTrue(results1.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertFalse(results1.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_1));
        assertFalse(results1.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_2));
        assertTrue(results1.stream().allMatch(r -> r.getIssueDate().after(TEST_DATE_1)));
        assertTrue(results1.stream().allMatch(r -> r.getIssueDate().before(TEST_DATE_7)));

        List<ProductionService.PaymentRecord> results2 =
                pService.searchEmployeePayments(TEST_DATE_2, TEST_DATE_8);

        assertNotNull(results2);
        assertTrue(results2.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertTrue(results2.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_1));
        assertFalse(results1.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_CHAPTER_ID_2));
        assertTrue(results2.stream().allMatch(r -> r.getIssueDate().after(TEST_DATE_1)));
        assertTrue(results2.stream().allMatch(r -> r.getIssueDate().before(TEST_DATE_9)));
    }

    @Test
    @Order(15)
    void compareIssues() throws Exception {
        pService.enterIssue(TEST_ISSUE_ID_1, "Test issue title 1", TEST_DATE_2, TEST_DATE_3, TEST_PERIODICAL_ID_1);
        pService.enterIssue(TEST_ISSUE_ID_2, "Test issue title 2", TEST_DATE_1, TEST_DATE_6, TEST_PERIODICAL_ID_1);
        pService.enterPublicationUnit(TEST_ARTICLE_ID_1, "Test article title 1", "Article", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        pService.enterPublicationUnit(TEST_ARTICLE_ID_2, "Test article title 2", "Article", TEST_DATE_2, null, "test topic", TEST_EMPLOYEE_ID_1);
        eService.addArticleToIssue(TEST_ARTICLE_ID_1, TEST_ISSUE_ID_1);
        eService.addArticleToIssue(TEST_ARTICLE_ID_2, TEST_ISSUE_ID_2);

        List<ProductionService.IssueRecord> results =
                pService.compareIssues(TEST_ISSUE_ID_1, TEST_ISSUE_ID_2);

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(r -> r.getIssueID() == TEST_ISSUE_ID_1));
        assertTrue(results.stream().anyMatch(r -> r.getIssueID() == TEST_ISSUE_ID_2));
        assertTrue(results.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_1));
        assertTrue(results.stream().anyMatch(r -> r.getPublicationUnitID() == TEST_ARTICLE_ID_2));
    }

    // -------------------------------------------------------------------------
    // Helper – existence checks
    // -------------------------------------------------------------------------

    private boolean editionExists(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Editions WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean issueExists(int issueID) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Issues WHERE IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, issueID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private int getEditionNum(String isbn) throws SQLException {
        String sql = "SELECT EditionNum FROM Editions WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("EditionNum");
            }
        }
    }

    private Date getEditionWrittenDate(String isbn) throws SQLException {
        String sql = "SELECT WrittenDate FROM Editions WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDate("WrittenDate");
            }
        }
    }

    private Date getEditionPublishedDate(String isbn) throws SQLException {
        String sql = "SELECT PublishedDate FROM Editions WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDate("PublishedDate");
            }
        }
    }

    private Date getIssueProducedDate(int issueID) throws SQLException {
        String sql = "SELECT ProducedDate FROM Issues WHERE IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, issueID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDate("ProducedDate");
            }
        }
    }

    private Date getIssuePublishedDate(int issueID) throws SQLException {
        String sql = "SELECT PublishedDate FROM Issues WHERE IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, issueID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDate("PublishedDate");
            }
        }
    }

    private String getIssueTitle(int issueId) throws SQLException {
        String sql = "SELECT IssueTitle FROM Issues WHERE IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString("IssueTitle");
            }
        }
    }

    private boolean publicationUnitExists(int publicationUnitID) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM PublicationUnits WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean articleExists(int publicationUnitID) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Articles WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean chapterExists(int publicationUnitID) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Chapters WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private String getPublicationUnitTitle(int publicationUnitId) throws SQLException {
        String sql = "SELECT PublicationUnitTitle FROM PublicationUnits WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString("PublicationUnitTitle");
            }
        }
    }

    private int getEmployeeID(int publicationUnitID) throws SQLException {
        String sql = "SELECT EmployeeID FROM PublicationUnits WHERE EmployeeID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publicationUnitID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("EmployeeID");
            }
        }
    }

    private boolean paymentExists(int paymentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Payments WHERE PaymentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean employeePaymentExists(int paymentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM EmployeePayments WHERE PaymentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private Date getPaymentClaimDate(int paymentId) throws SQLException {
        String sql = "SELECT ClaimDate FROM Payments WHERE PaymentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDate("ClaimDate");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helper – cleanup
    // -------------------------------------------------------------------------

    private void cleanupTestData() throws SQLException {
        // Remove test employeePayments
        for (int employeeid : new int[]{TEST_EMPLOYEE_ID_1, TEST_EMPLOYEE_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM EmployeePayments WHERE EmployeeID = ?")) {
                stmt.setInt(1, employeeid);
                stmt.executeUpdate();
            }
        }

        // Remove test payments
        for (int paymentid : new int[]{TEST_PAYMENT_ID_1, TEST_PAYMENT_ID_2, TEST_PAYMENT_ID_3}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Payments WHERE PaymentID = ?")) {
                stmt.setInt(1, paymentid);
                stmt.executeUpdate();
            }
        }

        // Remove test issue/article association
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM IssueArticle WHERE IssueID = ? AND PublicationUnitID = ?")) {
            stmt.setInt(1, TEST_ISSUE_ID_1);
            stmt.setInt(2, TEST_ARTICLE_ID_1);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM IssueArticle WHERE IssueID = ? AND PublicationUnitID = ?")) {
            stmt.setInt(1, TEST_ISSUE_ID_2);
            stmt.setInt(2, TEST_ARTICLE_ID_2);
            stmt.executeUpdate();
        }

        // Remove test edition/chapter association
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EditionChapter WHERE ISBN = ? AND PublicationUnitID = ?")) {
            stmt.setString(1, TEST_ISBN_1);
            stmt.setInt(2, TEST_CHAPTER_ID_1);
            stmt.executeUpdate();
        }

        // Remove test articles
        for (int articleid : new int[]{TEST_ARTICLE_ID_1, TEST_ARTICLE_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Articles WHERE PublicationUnitID = ?")) {
                stmt.setInt(1, articleid);
                stmt.executeUpdate();
            }
        }

        // Remove test chapters
        for (int chapterid : new int[]{TEST_CHAPTER_ID_1, TEST_CHAPTER_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Chapters WHERE PublicationUnitID = ?")) {
                stmt.setInt(1, chapterid);
                stmt.executeUpdate();
            }
        }

        // Remove test publication units
        for (int pubunitid : new int[]{TEST_ARTICLE_ID_1, TEST_ARTICLE_ID_2, TEST_CHAPTER_ID_1, TEST_CHAPTER_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM PublicationUnits WHERE PublicationUnitID = ?")) {
                stmt.setInt(1, pubunitid);
                stmt.executeUpdate();
            }
        }

        // Remove test issues
        for (int issueid : new int[]{TEST_ISSUE_ID_1, TEST_ISSUE_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Issues WHERE IssueID = ?")) {
                stmt.setInt(1, issueid);
                stmt.executeUpdate();
            }
        }

        // Remove test editions
        for (String isbn : new String[]{TEST_ISBN_1, TEST_ISBN_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Editions WHERE ISBN = ?")) {
                stmt.setString(1, isbn);
                stmt.executeUpdate();
            }
        }

        // Remove test periodical
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM PeriodicalPubs WHERE PubID = ?")) {
            stmt.setInt(1, TEST_PERIODICAL_ID_1);
            stmt.executeUpdate();
        }

        // Remove test book
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Books WHERE PubID = ?")) {
            stmt.setInt(1, TEST_BOOK_ID_1);
            stmt.executeUpdate();
        }

        // Remove test publications
        for (int pubid : new int[]{TEST_BOOK_ID_1, TEST_PERIODICAL_ID_1}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Publications WHERE PubID = ?")) {
                stmt.setInt(1, pubid);
                stmt.executeUpdate();
            }
        }

        // Remove test authors
        for (int employeeid : new int[]{TEST_EMPLOYEE_ID_1, TEST_EMPLOYEE_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Authors WHERE EmployeeID = ?")) {
                stmt.setInt(1, employeeid);
                stmt.executeUpdate();
            }
        }

        // Remove test employees
        for (int employeeid : new int[]{TEST_EMPLOYEE_ID_1, TEST_EMPLOYEE_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Employees WHERE EmployeeID = ?")) {
                stmt.setInt(1, employeeid);
                stmt.executeUpdate();
            }
        }
    }
}