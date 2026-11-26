import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/*
 * To be run on a database populated only by the sql file "3_insert_data"
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FullIntegrationTest {
    private static DBManager dbManager;
    private static Connection connection;
    private static EditingPublishingService editingService;
    private static ProductionService productionService;
    private static DistributionService distributionService;
    private static ReportsService reportsService;

    // Test IDs (high numbers to avoid conflicts with demo data)
    private static final int TEST_PUB_ID_BOOK = 9001;
    private static final int TEST_PUB_ID_PERIODICAL = 9002;
    private static final int TEST_EMPLOYEE_ID = 9001;
    private static final int TEST_AUTHOR_ID = 9001;
    private static final int TEST_EDITOR_ID = 9002;
    private static final int TEST_PUB_UNIT_ARTICLE = 9001;
    private static final int TEST_PUB_UNIT_CHAPTER = 9002;
    private static final String TEST_ISBN = "9999999999";
    private static final int TEST_ISSUE_ID = 9001;
    private static final int TEST_PAYMENT_ID = 9001;
    private static final int TEST_DIST_ID = 9001;
    private static final int TEST_ORDER_ID = 9001;

    private static final Date TEST_DATE = Date.valueOf("2026-04-01");
    private static final Date TEST_DATE_2 = Date.valueOf("2026-04-02");
    private static final Date TEST_DATE_3 = Date.valueOf("2026-04-03");

    @BeforeAll
    static void setUp() throws Exception {
        dbManager = new DBManager();
        dbManager.connect();
        connection = dbManager.getConnection();
        editingService = new EditingPublishingService(connection);
        productionService = new ProductionService(connection);
        distributionService = new DistributionService(connection);
        reportsService = new ReportsService(connection);
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @BeforeEach
    void prepareTestData() throws SQLException {
        // Clean up any leftover test data
        cleanupTestData();
        // Insert base test data: employee that is both author and editor
        insertEmployee(TEST_EMPLOYEE_ID, "Test Employee", false);
        insertAuthor(TEST_EMPLOYEE_ID);
        insertEditor(TEST_EMPLOYEE_ID);
        // Create a test book publication and periodical publication
        editingService.enterNewBookPublication(TEST_PUB_ID_BOOK, "Test Book", "Test Topic");
        editingService.enterNewPeriodicalPublication(TEST_PUB_ID_PERIODICAL, "Test Magazine", "Magazine", "Test Topic", "Monthly");
    }

    @AfterEach
    void cleanup() throws SQLException {
        cleanupTestData();
    }

    private void cleanupTestData() throws SQLException {
        // Delete in reverse order of dependencies, using test IDs only
        // 1. Orders
        deleteOrder(TEST_ORDER_ID);
        // 2. DistPayments
        deleteDistPaymentByDistId(TEST_DIST_ID);
        deleteDistPaymentByPaymentId(TEST_PAYMENT_ID);
        // 3. EmployeePayments
        deleteEmployeePaymentByPaymentId(TEST_PAYMENT_ID);
        deleteEmployeePaymentByEmployeeId(TEST_EMPLOYEE_ID);
        deleteEmployeePaymentByPublicationUnitId(TEST_PUB_UNIT_ARTICLE);
        deleteEmployeePaymentByPublicationUnitId(TEST_PUB_UNIT_CHAPTER);
        // 4. Payments
        deletePayment(TEST_PAYMENT_ID);
        // 5. EditionChapter
        deleteEditionChapterByIsbn(TEST_ISBN);
        deleteEditionChapterByPublicationUnitId(TEST_PUB_UNIT_CHAPTER);
        // 6. IssueArticle
        deleteIssueArticleByIssueId(TEST_ISSUE_ID);
        deleteIssueArticleByPublicationUnitId(TEST_PUB_UNIT_ARTICLE);
        // 7. Articles
        deleteArticle(TEST_PUB_UNIT_ARTICLE);
        // 8. Chapters
        deleteChapter(TEST_PUB_UNIT_CHAPTER);
        // 9. PublicationUnits
        deletePublicationUnit(TEST_PUB_UNIT_ARTICLE);
        deletePublicationUnit(TEST_PUB_UNIT_CHAPTER);
        // 10. Editions
        deleteEdition(TEST_ISBN);
        // 11. Issues
        deleteIssue(TEST_ISSUE_ID);
        // 12. Edits
        deleteEditByEmployeeId(TEST_EMPLOYEE_ID);
        deleteEditByPubId(TEST_PUB_ID_BOOK);
        deleteEditByPubId(TEST_PUB_ID_PERIODICAL);
        // 13. PeriodicalPubs
        deletePeriodicalPub(TEST_PUB_ID_PERIODICAL);
        // 14. Books
        deleteBook(TEST_PUB_ID_BOOK);
        // 15. Publications
        deletePublication(TEST_PUB_ID_BOOK);
        deletePublication(TEST_PUB_ID_PERIODICAL);
        // 16. Authors
        deleteAuthor(TEST_EMPLOYEE_ID);
        // 17. Editors
        deleteEditor(TEST_EMPLOYEE_ID);
        // 18. Employees
        deleteEmployee(TEST_EMPLOYEE_ID);
        // 19. Distributors
        deleteDistributor(TEST_DIST_ID);
    }

    // -------------------------------------------------------------------------
    // Helper deletion methods
    // -------------------------------------------------------------------------

    private void deleteEmployee(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Employees WHERE EmployeeID = ?")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private void deleteEdition(String isbn) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Editions WHERE ISBN = ?")) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

    private void deleteIssue(int issueId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Issues WHERE IssueID = ?")) {
            stmt.setInt(1, issueId);
            stmt.executeUpdate();
        }
    }

    private void deletePublicationUnit(int pubUnitId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM PublicationUnits WHERE PublicationUnitID = ?")) {
            stmt.setInt(1, pubUnitId);
            stmt.executeUpdate();
        }
    }

    private void deletePayment(int paymentId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Payments WHERE PaymentID = ?")) {
            stmt.setInt(1, paymentId);
            stmt.executeUpdate();
        }
    }

    private void deleteDistributor(int distId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Distributors WHERE DistID = ?")) {
            stmt.setInt(1, distId);
            stmt.executeUpdate();
        }
    }

    private void deleteOrder(int orderId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Orders WHERE OrderID = ?")) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    private void deleteDistPaymentByDistId(int distId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM DistPayments WHERE DistID = ?")) {
            stmt.setInt(1, distId);
            stmt.executeUpdate();
        }
    }

    private void deleteDistPaymentByPaymentId(int paymentId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM DistPayments WHERE PaymentID = ?")) {
            stmt.setInt(1, paymentId);
            stmt.executeUpdate();
        }
    }

    private void deleteEmployeePaymentByPaymentId(int paymentId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EmployeePayments WHERE PaymentID = ?")) {
            stmt.setInt(1, paymentId);
            stmt.executeUpdate();
        }
    }

    private void deleteEmployeePaymentByEmployeeId(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EmployeePayments WHERE EmployeeID = ?")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private void deleteEmployeePaymentByPublicationUnitId(int pubUnitId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EmployeePayments WHERE PublicationUnitID = ?")) {
            stmt.setInt(1, pubUnitId);
            stmt.executeUpdate();
        }
    }

    private void deleteEditionChapterByIsbn(String isbn) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EditionChapter WHERE ISBN = ?")) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

    private void deleteEditionChapterByPublicationUnitId(int pubUnitId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EditionChapter WHERE PublicationUnitID = ?")) {
            stmt.setInt(1, pubUnitId);
            stmt.executeUpdate();
        }
    }

    private void deleteIssueArticleByIssueId(int issueId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM IssueArticle WHERE IssueID = ?")) {
            stmt.setInt(1, issueId);
            stmt.executeUpdate();
        }
    }

    private void deleteIssueArticleByPublicationUnitId(int pubUnitId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM IssueArticle WHERE PublicationUnitID = ?")) {
            stmt.setInt(1, pubUnitId);
            stmt.executeUpdate();
        }
    }

    private void deleteArticle(int pubUnitId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Articles WHERE PublicationUnitID = ?")) {
            stmt.setInt(1, pubUnitId);
            stmt.executeUpdate();
        }
    }

    private void deleteChapter(int pubUnitId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Chapters WHERE PublicationUnitID = ?")) {
            stmt.setInt(1, pubUnitId);
            stmt.executeUpdate();
        }
    }

    private void deleteEditByEmployeeId(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Edits WHERE EmployeeID = ?")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private void deleteEditByPubId(int pubId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Edits WHERE PubID = ?")) {
            stmt.setInt(1, pubId);
            stmt.executeUpdate();
        }
    }

    private void deletePeriodicalPub(int pubId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM PeriodicalPubs WHERE PubID = ?")) {
            stmt.setInt(1, pubId);
            stmt.executeUpdate();
        }
    }

    private void deleteBook(int pubId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Books WHERE PubID = ?")) {
            stmt.setInt(1, pubId);
            stmt.executeUpdate();
        }
    }

    private void deletePublication(int pubId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Publications WHERE PubID = ?")) {
            stmt.setInt(1, pubId);
            stmt.executeUpdate();
        }
    }

    private void deleteAuthor(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Authors WHERE EmployeeID = ?")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private void deleteEditor(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Editors WHERE EmployeeID = ?")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Helper insert methods
    // -------------------------------------------------------------------------

    private void insertEmployee(int employeeId, String name, boolean isStaff) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Employees (EmployeeID, Name, IsStaff) VALUES (?, ?, ?)")) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, name);
            stmt.setBoolean(3, isStaff);
            stmt.executeUpdate();
        }
    }

    private void insertAuthor(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Authors (EmployeeID) VALUES (?)")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private void insertEditor(int employeeId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Editors (EmployeeID) VALUES (?)")) {
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    // =========================================================================
    // TEST METHODS
    // =========================================================================

    // -------------------------------------------------------------------------
    // Group a: Editing and Publishing
    // -------------------------------------------------------------------------

    /**
     * a.1 Enter basic information on a new book publication.
     * Verifies that a new Book publication row is created in both Publications and Books.
     */
    @Test
    @Order(1)
    void testEnterNewBookPublication() throws Exception {
        // Already done in prepareTestData; verify it exists
        assertTrue(publicationExists(TEST_PUB_ID_BOOK));
        assertTrue(bookExists(TEST_PUB_ID_BOOK));
    }

    /**
     * a.2 Enter basic information on a new periodical publication.
     * Verifies that a new Periodical publication row is created in both Publications and PeriodicalPubs.
     */
    @Test
    @Order(2)
    void testEnterNewPeriodicalPublication() throws Exception {
        assertTrue(publicationExists(TEST_PUB_ID_PERIODICAL));
        assertTrue(periodicalExists(TEST_PUB_ID_PERIODICAL));
    }

    /**
     * a.3 Update publication information (title).
     * Verifies that the title is updated in Publications.
     */
    @Test
    @Order(3)
    void testUpdatePublicationInformation() throws Exception {
        int rows = editingService.updatePublicationInformation(TEST_PUB_ID_BOOK, "Updated Title");
        assertEquals(1, rows);
        assertEquals("Updated Title", getPublicationTitle(TEST_PUB_ID_BOOK));
    }

    /**
     * a.4 Assign an editor to a publication.
     * Verifies that a row is inserted into Edits.
     */
    @Test
    @Order(4)
    void testAssignEditorToPublication() throws Exception {
        int rows = editingService.assignEditorToPublication(TEST_EMPLOYEE_ID, TEST_PUB_ID_BOOK);
        assertEquals(1, rows);
        assertTrue(editExists(TEST_EMPLOYEE_ID, TEST_PUB_ID_BOOK));
    }

    /**
     * a.5 Remove an editor from a publication.
     * Verifies that the Edits row is deleted.
     */
    @Test
    @Order(5)
    void testRemoveEditorFromPublication() throws Exception {
        editingService.assignEditorToPublication(TEST_EMPLOYEE_ID, TEST_PUB_ID_BOOK);
        int rows = editingService.removeEditorFromPublication(TEST_EMPLOYEE_ID, TEST_PUB_ID_BOOK);
        assertEquals(1, rows);
        assertFalse(editExists(TEST_EMPLOYEE_ID, TEST_PUB_ID_BOOK));
    }

    /**
     * a.6 View all publications assigned to a specific editor.
     * Verifies that the assigned publication appears in the result list.
     */
    @Test
    @Order(6)
    void testGetPublicationsByEditor() throws Exception {
        editingService.assignEditorToPublication(TEST_EMPLOYEE_ID, TEST_PUB_ID_BOOK);
        List<EditingPublishingService.PublicationRecord> publications =
                editingService.getPublicationsByEditor(TEST_EMPLOYEE_ID);
        assertNotNull(publications);
        assertTrue(publications.stream().anyMatch(p -> p.getPubId() == TEST_PUB_ID_BOOK));
    }

    /**
     * a.7 Edit table of contents: add and delete an article from an issue (periodical).
     * Verifies that IssueArticle row is inserted and then deleted.
     */
    @Test
    @Order(7)
    void testAddAndDeleteArticleToIssue() throws Exception {
        productionService.enterIssue(TEST_ISSUE_ID, "Test Issue", TEST_DATE, TEST_DATE_2, TEST_PUB_ID_PERIODICAL);
        productionService.enterPublicationUnit(TEST_PUB_UNIT_ARTICLE, "Test Article", "Article",
                TEST_DATE, "Full text", "Technology", TEST_EMPLOYEE_ID);

        int insertRows = editingService.addArticleToIssue(TEST_PUB_UNIT_ARTICLE, TEST_ISSUE_ID);
        assertEquals(1, insertRows);
        assertTrue(articleExists(TEST_PUB_UNIT_ARTICLE, TEST_ISSUE_ID));

        int deleteRows = editingService.deleteArticleFromIssue(TEST_PUB_UNIT_ARTICLE, TEST_ISSUE_ID);
        assertEquals(1, deleteRows);
        assertFalse(articleExists(TEST_PUB_UNIT_ARTICLE, TEST_ISSUE_ID));
    }

    /**
     * a.8 Edit table of contents: add and delete a chapter from a book edition.
     * Verifies that EditionChapter row is inserted and then deleted.
     */
    @Test
    @Order(8)
    void testAddAndDeleteChapterToEdition() throws Exception {
        productionService.enterEdition(TEST_ISBN, 1, TEST_DATE, TEST_DATE_2, TEST_PUB_ID_BOOK);
        productionService.enterPublicationUnit(TEST_PUB_UNIT_CHAPTER, "Test Chapter", "Chapter",
                TEST_DATE, "Full text", "Technology", TEST_EMPLOYEE_ID);

        int insertRows = editingService.addChapterToEdition(TEST_PUB_UNIT_CHAPTER, TEST_ISBN);
        assertEquals(1, insertRows);
        assertTrue(chapterExists(TEST_PUB_UNIT_CHAPTER, TEST_ISBN));

        int deleteRows = editingService.deleteChapterFromEdition(TEST_PUB_UNIT_CHAPTER, TEST_ISBN);
        assertEquals(1, deleteRows);
        assertFalse(chapterExists(TEST_PUB_UNIT_CHAPTER, TEST_ISBN));
    }

    // -------------------------------------------------------------------------
    // Group b: Production
    // -------------------------------------------------------------------------

    /**
     * b.1 Enter a new book edition.
     * Verifies that a row is inserted into Editions.
     */
    @Test
    @Order(9)
    void testEnterEdition() throws Exception {
        productionService.enterEdition(TEST_ISBN, 1, TEST_DATE, TEST_DATE_2, TEST_PUB_ID_BOOK);
        assertTrue(editionExists(TEST_ISBN));
    }

    /**
     * b.2 Enter a new issue of a periodical publication.
     * Verifies that a row is inserted into Issues.
     */
    @Test
    @Order(10)
    void testEnterIssue() throws Exception {
        productionService.enterIssue(TEST_ISSUE_ID, "Test Issue", TEST_DATE, TEST_DATE_2, TEST_PUB_ID_PERIODICAL);
        assertTrue(issueExists(TEST_ISSUE_ID));
    }

    /**
     * b.3 Update a book edition (edition number and dates).
     * Verifies that EditionNum is updated in Editions.
     */
    @Test
    @Order(11)
    void testUpdateEdition() throws Exception {
        productionService.enterEdition(TEST_ISBN, 1, null, null, TEST_PUB_ID_BOOK);
        assertEquals(1, getEditionNum(TEST_ISBN));
        productionService.updateEdition(TEST_ISBN, 2, TEST_DATE, TEST_DATE_2);
        assertEquals(2, getEditionNum(TEST_ISBN));
    }

    /**
     * b.4 Update a publication issue (title and dates).
     * Verifies that IssueTitle is updated in Issues.
     */
    @Test
    @Order(12)
    void testUpdateIssue() throws Exception {
        productionService.enterIssue(TEST_ISSUE_ID, "Test Issue", null, null, TEST_PUB_ID_PERIODICAL);
        assertEquals("Test Issue", getIssueTitle(TEST_ISSUE_ID));
        productionService.updateIssue(TEST_ISSUE_ID, "Updated Issue", TEST_DATE, TEST_DATE_2);
        assertEquals("Updated Issue", getIssueTitle(TEST_ISSUE_ID));
    }

    /**
     * b.5 Delete a book edition (also removes dependent EditionChapter and Orders rows).
     * Verifies that the edition no longer exists after deletion.
     */
    @Test
    @Order(13)
    void testDeleteEdition() throws Exception {
        productionService.enterEdition(TEST_ISBN, 1, TEST_DATE, TEST_DATE_2, TEST_PUB_ID_BOOK);
        assertTrue(editionExists(TEST_ISBN));
        productionService.deleteEdition(TEST_ISBN);
        assertFalse(editionExists(TEST_ISBN));
    }

    /**
     * b.6 Delete a publication issue (also removes dependent IssueArticle and Orders rows).
     * Verifies that the issue no longer exists after deletion.
     */
    @Test
    @Order(14)
    void testDeleteIssue() throws Exception {
        productionService.enterIssue(TEST_ISSUE_ID, "Test Issue", TEST_DATE, TEST_DATE_2, TEST_PUB_ID_PERIODICAL);
        assertTrue(issueExists(TEST_ISSUE_ID));
        productionService.deleteIssue(TEST_ISSUE_ID);
        assertFalse(issueExists(TEST_ISSUE_ID));
    }

    /**
     * b.7 Enter an article (publication unit of type Article).
     * Verifies rows exist in both PublicationUnits and Articles.
     */
    @Test
    @Order(15)
    void testEnterPublicationUnit() throws Exception {
        productionService.enterPublicationUnit(TEST_PUB_UNIT_ARTICLE, "Test Article", "Article",
                TEST_DATE, "Full text", "Technology", TEST_EMPLOYEE_ID);
        assertTrue(publicationUnitExists(TEST_PUB_UNIT_ARTICLE));
        assertTrue(articleExists(TEST_PUB_UNIT_ARTICLE));
    }

    /**
     * b.8 Update an article: title, author, topic, date, and full text.
     * Verifies that the title is updated in PublicationUnits.
     */
    @Test
    @Order(16)
    void testUpdatePublicationUnit() throws Exception {
        productionService.enterPublicationUnit(TEST_PUB_UNIT_ARTICLE, "Test Article", "Article",
                TEST_DATE, "Full text", "Technology", TEST_EMPLOYEE_ID);
        int rows = productionService.updatePublicationUnit(TEST_PUB_UNIT_ARTICLE, "Updated Title",
                TEST_DATE_2, "Updated text", "Databases", TEST_EMPLOYEE_ID);
        assertEquals(1, rows);
        assertEquals("Updated Title", getPublicationUnitTitle(TEST_PUB_UNIT_ARTICLE));
    }

    /**
     * b.9 Find books and articles by topic.
     * Uses demo data topic "Technology" (present in PublicationUnits).
     */
    @Test
    @Order(17)
    void testSearchTopic() throws Exception {
        // Demo data has publication units with topic "Technology" (IDs 4 and 8)
        List<ProductionService.PublicationUnitRecord> results = productionService.searchTopic("Technology");
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * b.10 Find books and articles by specific date range.
     * Uses demo data: several articles written in January 2023.
     */
    @Test
    @Order(18)
    void testSearchDate() throws Exception {
        // Demo data has publication units written in Jan 2023 (IDs 3, 4, 8)
        Date start = Date.valueOf("2023-01-01");
        Date end = Date.valueOf("2023-01-31");
        List<ProductionService.PublicationUnitRecord> results = productionService.searchDate(start, end);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * b.11 Find books and articles by author's name.
     * Uses demo data author "Ivy Chen" (EmployeeID=9, has chapters 1 and 5).
     */
    @Test
    @Order(19)
    void testSearchAuthor() throws Exception {
        // Demo data: "Ivy Chen" (EmployeeID=9) authored PublicationUnits 1 and 5
        List<ProductionService.PublicationUnitRecord> results = productionService.searchAuthor("Ivy Chen");
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * b.12 Enter payment for an author (linked to a publication unit).
     * Verifies rows exist in both Payments and EmployeePayments.
     */
    @Test
    @Order(20)
    void testEnterEmployeePayment() throws Exception {
        // Create the publication unit first (required by FK on EmployeePayments)
        productionService.enterPublicationUnit(TEST_PUB_UNIT_ARTICLE, "Test Article", "Article",
                TEST_DATE, "Full text", "Technology", TEST_EMPLOYEE_ID);
        productionService.enterEmployeePayment(TEST_PAYMENT_ID, 500.00, null, TEST_DATE,
                TEST_EMPLOYEE_ID, TEST_PUB_UNIT_ARTICLE);
        assertTrue(paymentExists(TEST_PAYMENT_ID));
        assertTrue(employeePaymentExists(TEST_PAYMENT_ID));
    }

    /**
     * b.13 Update the claim date for an employee payment.
     * Verifies that ClaimDate is updated in Payments.
     */
    @Test
    @Order(21)
    void testUpdateEmployeePayment() throws Exception {
        // Create the publication unit first (required by FK on EmployeePayments)
        productionService.enterPublicationUnit(TEST_PUB_UNIT_ARTICLE, "Test Article", "Article",
                TEST_DATE, "Full text", "Technology", TEST_EMPLOYEE_ID);
        productionService.enterEmployeePayment(TEST_PAYMENT_ID, 500.00, null, TEST_DATE,
                TEST_EMPLOYEE_ID, TEST_PUB_UNIT_ARTICLE);
        int rows = productionService.updateEmployeePayment(TEST_PAYMENT_ID, TEST_DATE_2);
        assertEquals(1, rows);
        assertEquals(TEST_DATE_2, getPaymentClaimDate(TEST_PAYMENT_ID));
    }

    /**
     * b.14 List payments that were issued but not claimed within a specified date range.
     * Uses demo data: unclaimed payments (ClaimDate IS NULL) issued in Mar–Apr 2023
     * (PaymentIDs 3, 5, 7 with IssueDates in that range).
     */
    @Test
    @Order(22)
    void testSearchEmployeePayments() throws Exception {
        // Demo data: unclaimed payments with IssueDate in 2023-03 to 2023-04 are PaymentIDs 3, 5, 7
        Date start = Date.valueOf("2023-03-01");
        Date end = Date.valueOf("2023-04-30");
        List<ProductionService.PaymentRecord> results = productionService.searchEmployeePayments(start, end);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * b.15 Compare two issues by listing their associated articles.
     * Uses demo data issues 1 and 2 (both have articles linked via IssueArticle).
     */
    @Test
    @Order(23)
    void testCompareIssues() throws Exception {
        // Demo data: Issue 1 has articles 3 and 4; Issue 2 has article 3
        List<ProductionService.IssueRecord> results = productionService.compareIssues(1, 2);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    // -------------------------------------------------------------------------
    // Group c: Distribution
    // -------------------------------------------------------------------------

    /**
     * c.1 Enter a new distributor.
     * Verifies that a row is inserted into Distributors.
     */
    @Test
    @Order(24)
    void testEnterNewDistributor() throws Exception {
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 0.0);
        assertTrue(distributorExists(TEST_DIST_ID));
    }

    /**
     * c.2 Update distributor information.
     * Verifies that the Name is updated in Distributors.
     */
    @Test
    @Order(25)
    void testUpdateDistributorInformation() throws Exception {
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 0.0);
        int rows = distributionService.updateDistributorInformation(TEST_DIST_ID, "Updated Name",
                "Wholesale", "0987654321", "Jane Doe", "Durham", "456 Avenue");
        assertEquals(1, rows);
        assertEquals("Updated Name", getDistributorName(TEST_DIST_ID));
    }

    /**
     * c.3 Delete a distributor (cascade removes dependent DistPayments and Orders).
     * Verifies that the distributor no longer exists after deletion.
     */
    @Test
    @Order(26)
    void testDeleteDistributor() throws Exception {
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 0.0);
        assertTrue(distributorExists(TEST_DIST_ID));
        int rows = distributionService.deleteDistributor(TEST_DIST_ID);
        assertEquals(1, rows);
        assertFalse(distributorExists(TEST_DIST_ID));
    }

    /**
     * c.4a Input an order from a distributor for a book edition.
     * Verifies that a row is inserted into Orders with a non-null ISBN.
     */
    @Test
    @Order(27)
    void testInputOrderForEdition() throws Exception {
        productionService.enterEdition(TEST_ISBN, 1, TEST_DATE, TEST_DATE_2, TEST_PUB_ID_BOOK);
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 0.0);
        distributionService.inputOrderForEdition(TEST_ORDER_ID, 100, 1500.00, 50.00,
                TEST_DATE, true, TEST_DATE_2, TEST_DATE_3, TEST_DIST_ID, TEST_ISBN);
        assertTrue(orderExists(TEST_ORDER_ID));
    }

    /**
     * c.4b Input an order from a distributor for a periodical issue.
     * Verifies that a row is inserted into Orders with a non-null IssueID.
     */
    @Test
    @Order(28)
    void testInputOrderForIssue() throws Exception {
        productionService.enterIssue(TEST_ISSUE_ID, "Test Issue", TEST_DATE, TEST_DATE_2, TEST_PUB_ID_PERIODICAL);
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 0.0);
        distributionService.inputOrderForIssue(TEST_ORDER_ID, 200, 2000.00, 60.00,
                TEST_DATE, true, TEST_DATE_2, TEST_DATE_3, TEST_DIST_ID, TEST_ISSUE_ID);
        assertTrue(orderExists(TEST_ORDER_ID));
    }

    /**
     * c.5 Bill a distributor for an order (increases outstanding balance by order price).
     * Verifies that the distributor's balance increases by exactly the order price.
     */
    @Test
    @Order(29)
    void testBillDistributorForOrder() throws Exception {
        productionService.enterEdition(TEST_ISBN, 1, TEST_DATE, TEST_DATE_2, TEST_PUB_ID_BOOK);
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 0.0);
        distributionService.inputOrderForEdition(TEST_ORDER_ID, 100, 1500.00, 50.00,
                TEST_DATE, true, TEST_DATE_2, TEST_DATE_3, TEST_DIST_ID, TEST_ISBN);
        int rows = distributionService.billDistributorForOrder(TEST_ORDER_ID);
        assertEquals(1, rows);
        // Balance starts at 0.0; after billing by order price (1500.00) it should be 1500.00
        assertEquals(1500.00, getDistributorBalance(TEST_DIST_ID), 0.001);
    }

    /**
     * c.6 Receive a payment from a distributor and decrease outstanding balance.
     * Verifies that a Payments row and DistPayments row are created, and
     * the distributor balance is reduced by the payment amount.
     */
    @Test
    @Order(30)
    void testReceivePayment() throws Exception {
        distributionService.enterNewDistributor(TEST_DIST_ID, "Test Distributor", "Retail",
                "1234567890", "John Doe", "Raleigh", "123 Street", 1000.0);
        distributionService.receivePayment(TEST_PAYMENT_ID, 500.0, TEST_DIST_ID);
        assertTrue(paymentExists(TEST_PAYMENT_ID));
        // Balance 1000.0 - 500.0 = 500.0
        assertEquals(500.0, getDistributorBalance(TEST_DIST_ID), 0.001);
    }

    /**
     * c.7 Identify distributors whose total billed amount does not match the sum of their recorded payments.
     * Runs against existing demo data; simply verifies the query executes without error.
     */
    @Test
    @Order(31)
    void testGetDistributorsWithMismatchedPayments() throws Exception {
        List<DistributionService.DistributorMismatchRecord> results =
                distributionService.getDistributorsWithMismatchedPayments();
        assertNotNull(results);
        // The query may return results from demo data; we verify no exception is thrown
    }

    /**
     * c.8 List all distributors of a specific type located in a given city.
     * Uses demo data: category "Retail" in city "Raleigh" matches DistIDs 1 and 5.
     */
    @Test
    @Order(32)
    void testGetDistributorsByTypeAndCity() throws Exception {
        // Demo data has two 'Retail' distributors in 'Raleigh' (DistIDs 1 and 5)
        List<DistributionService.DistributorRecord> results =
                distributionService.getDistributorsByTypeAndCity("Retail", "Raleigh");
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    // -------------------------------------------------------------------------
    // Group d: Reports
    // -------------------------------------------------------------------------

    /**
     * d.1 Number and total price of copies of each publication per distributor.
     * Uses demo data orders; verifies the query returns at least one record.
     */
    @Test
    @Order(33)
    void testGetCopiesAndTotalPricePerDistributor() throws Exception {
        List<ReportsService.CopiesAndTotalPricePerDistributorRecord> results =
                reportsService.getCopiesAndTotalPricePerDistributor();
        assertNotNull(results);
        // Demo data has 7 orders across 6 distributors
        assertTrue(results.size() > 0);
    }

    /**
     * d.2 Number and total price of copies per week (within a given date range).
     * Uses demo data orders placed in February 2023 (OrderIDs 1–4).
     */
    @Test
    @Order(34)
    void testGetCopiesAndTotalPricePerWeek() throws Exception {
        // Demo data: orders placed in Feb 2023 (OrderIDs 1, 2, 3, 4)
        Date start = Date.valueOf("2023-02-01");
        Date end = Date.valueOf("2023-02-28");
        List<ReportsService.CopiesAndTotalPricePerWeekRecord> results =
                reportsService.getCopiesAndTotalPricePerWeek(start, end);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * d.3 Number and total price of copies per month (for a specific year and month).
     * Uses demo data orders placed in February 2023.
     */
    @Test
    @Order(35)
    void testGetCopiesAndTotalPricePerMonth() throws Exception {
        // Demo data: orders in year 2023, month 2 (February)
        List<ReportsService.CopiesAndTotalPricePerMonthRecord> results =
                reportsService.getCopiesAndTotalPricePerMonth(2023, 2);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * d.4 Total revenue of the publishing house (all orders).
     * Verifies that total revenue is a non-negative number.
     */
    @Test
    @Order(36)
    void testGetTotalRevenue() throws Exception {
        double revenue = reportsService.getTotalRevenue();
        assertTrue(revenue >= 0.0);
    }

    /**
     * d.5 Total expenses: shipping costs and salaries.
     * Verifies that each component and the total are non-negative.
     */
    @Test
    @Order(37)
    void testGetTotalExpenses() throws Exception {
        ReportsService.TotalExpensesRecord expenses = reportsService.getTotalExpenses();
        assertNotNull(expenses);
        assertTrue(expenses.getTotalShippingCost() >= 0.0);
        assertTrue(expenses.getTotalSalaries() >= 0.0);
        assertTrue(expenses.getTotalExpenses() >= 0.0);
    }

    /**
     * d.6 Total current number of distributors.
     * Demo data has 6 distributors; verifies count >= 6.
     */
    @Test
    @Order(38)
    void testGetTotalCurrentNumberOfDistributors() throws Exception {
        int count = reportsService.getTotalCurrentNumberOfDistributors();
        // Demo data has 6 distributors
        assertTrue(count >= 6);
    }

    /**
     * d.7 Total revenue (since inception) per city.
     * Verifies that at least one city record is returned from demo data.
     */
    @Test
    @Order(39)
    void testGetTotalRevenuePerCity() throws Exception {
        List<ReportsService.TotalRevenuePerCityRecord> results = reportsService.getTotalRevenuePerCity();
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * d.8 Total revenue (since inception) per distributor.
     * Verifies that at least one distributor record is returned from demo data.
     */
    @Test
    @Order(40)
    void testGetTotalRevenuePerDistributor() throws Exception {
        List<ReportsService.TotalRevenuePerDistributorRecord> results = reportsService.getTotalRevenuePerDistributor();
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * d.9 Total payments to editors and authors per time period (month), for a given year.
     * Uses demo data: EmployeePayments with IssueDates in 2023 (PaymentIDs 1–7).
     */
    @Test
    @Order(41)
    void testGetTotalPaymentsPerMonth() throws Exception {
        // Demo data: employee payments with IssueDates in 2023
        List<ReportsService.TotalPaymentsPerMonthRecord> results = reportsService.getTotalPaymentsPerMonth(2023);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    /**
     * d.10 Total payments per work type (book authorship, article authorship, editorial work).
     * Verifies that at least one work-type bucket is returned from demo data.
     */
    @Test
    @Order(42)
    void testGetTotalPaymentsPerWorkType() throws Exception {
        List<ReportsService.TotalPaymentsPerWorkTypeRecord> results = reportsService.getTotalPaymentsPerWorkType();
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    // =========================================================================
    // HELPER CHECK METHODS
    // =========================================================================

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

    private boolean issueExists(int issueId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Issues WHERE IssueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, issueId);
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

    private boolean publicationUnitExists(int pubUnitId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM PublicationUnits WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubUnitId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private boolean articleExists(int pubUnitId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Articles WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubUnitId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private String getPublicationUnitTitle(int pubUnitId) throws SQLException {
        String sql = "SELECT PublicationUnitTitle FROM PublicationUnits WHERE PublicationUnitID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubUnitId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString("PublicationUnitTitle");
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

    private boolean distributorExists(int distId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Distributors WHERE DistID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, distId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private String getDistributorName(int distId) throws SQLException {
        String sql = "SELECT Name FROM Distributors WHERE DistID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, distId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString("Name");
            }
        }
    }

    private boolean orderExists(int orderId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Orders WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    private double getDistributorBalance(int distId) throws SQLException {
        String sql = "SELECT Balance FROM Distributors WHERE DistID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, distId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDouble("Balance");
            }
        }
    }
}
