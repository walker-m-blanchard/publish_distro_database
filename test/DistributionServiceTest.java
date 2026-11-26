import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * To be run on a database populated only by the sql file "demo_date"
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DistributionServiceTest {

    private static DBManager dbManager;
    private static Connection connection;
    private static DistributionService service;

    // Test IDs that won't conflict with seed data (seed data uses 1–6 for distributors,
    // 1–7 for orders, 8–13 for payments)
    private static final int TEST_DIST_ID_1 = 9001;
    private static final int TEST_DIST_ID_2 = 9002;
    private static final int TEST_ORDER_ID_1 = 9001;
    private static final int TEST_ORDER_ID_2 = 9002;
    private static final int TEST_PAYMENT_ID_1 = 9001;

    // Existing seed data used as FK targets
    private static final String EXISTING_ISBN = "9781458300002";
    private static final int EXISTING_ISSUE_ID = 1;

    @BeforeAll
    static void setUp() throws Exception {
        dbManager = new DBManager();
        dbManager.connect();
        connection = dbManager.getConnection();
        service = new DistributionService(connection);
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
    void testEnterNewDistributor() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Test Books Inc.", "Retail",
                "555-111-2222", "John Doe", "Raleigh, NC", "100 Test St", 0.0);

        assertTrue(distributorExists(TEST_DIST_ID_1));
        assertEquals("Test Books Inc.", getDistributorName(TEST_DIST_ID_1));
    }

    @Test
    @Order(2)
    void testUpdateDistributorInformation() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Old Name", "Retail",
                "555-000-0000", "Old Rep", "OldCity", "1 Old St", 0.0);

        int rows = service.updateDistributorInformation(TEST_DIST_ID_1, "New Name", "Wholesale",
                "555-999-8888", "New Rep", "NewCity", "2 New Ave");

        assertEquals(1, rows);
        assertEquals("New Name", getDistributorName(TEST_DIST_ID_1));

        //Confirming existing attribute values won't change if null value passed in update
        service.updateDistributorInformation(TEST_DIST_ID_1, null, "Bookstore",
                "666-999-8888", "Newer Rep", "NewerCity", "3 New Ave");
        assertEquals("New Name", getDistributorName(TEST_DIST_ID_1));
    }

    @Test
    @Order(3)
    void testDeleteDistributor() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Delete Me", "Academic",
                "555-333-4444", "Jane Smith", "Durham", "5 Delete Rd", 100.0);
        assertTrue(distributorExists(TEST_DIST_ID_1));

        int rows = service.deleteDistributor(TEST_DIST_ID_1);

        assertEquals(1, rows);
        assertFalse(distributorExists(TEST_DIST_ID_1));
    }

    @Test
    @Order(4)
    void testInputOrderForEdition() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Order Test Dist", "Retail",
                "555-777-8888", "Rep One", "Charlotte", "10 Order Blvd", 0.0);

        service.inputOrderForEdition(TEST_ORDER_ID_1, 50, 750.00, 80.00,
                Date.valueOf("2024-01-10"), false,
                Date.valueOf("2024-01-20"), Date.valueOf("2024-01-25"),
                TEST_DIST_ID_1, EXISTING_ISBN);

        assertTrue(orderExists(TEST_ORDER_ID_1));
        assertEquals(EXISTING_ISBN, getOrderIsbn(TEST_ORDER_ID_1));
    }

    @Test
    @Order(5)
    void testInputOrderForIssue() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Issue Order Dist", "Wholesale",
                "555-222-3333", "Rep Two", "Greensboro", "20 Issue Rd", 0.0);

        service.inputOrderForIssue(TEST_ORDER_ID_2, 30, 400.00, 50.00,
                Date.valueOf("2024-02-01"), false,
                Date.valueOf("2024-02-10"), Date.valueOf("2024-02-15"),
                TEST_DIST_ID_1, EXISTING_ISSUE_ID);

        assertTrue(orderExists(TEST_ORDER_ID_2));
        assertEquals(EXISTING_ISSUE_ID, getOrderIssueId(TEST_ORDER_ID_2));
    }

    @Test
    @Order(6)
    void testBillDistributorForOrder() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Bill Test Dist", "Retail",
                "555-444-5555", "Bill Rep", "Wilmington", "30 Bill St", 0.0);
        service.inputOrderForEdition(TEST_ORDER_ID_1, 100, 1000.00, 150.00,
                Date.valueOf("2024-03-01"), false,
                Date.valueOf("2024-03-10"), Date.valueOf("2024-03-15"),
                TEST_DIST_ID_1, EXISTING_ISBN);

        double balanceBefore = getDistributorBalance(TEST_DIST_ID_1);
        int rows = service.billDistributorForOrder(TEST_ORDER_ID_1);

        assertEquals(1, rows);
        double balanceAfter = getDistributorBalance(TEST_DIST_ID_1);
        assertEquals(balanceBefore + 1000.00, balanceAfter, 0.001);
    }

    @Test
    @Order(7)
    void testReceivePayment() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Payment Test Dist", "Academic",
                "555-666-7777", "Pay Rep", "Raleigh, NC", "40 Pay Ave", 500.0);

        service.receivePayment(TEST_PAYMENT_ID_1, 200.0, TEST_DIST_ID_1);

        assertTrue(paymentExists(TEST_PAYMENT_ID_1));
        assertTrue(distPaymentExists(TEST_PAYMENT_ID_1, TEST_DIST_ID_1));
        assertEquals(300.0, getDistributorBalance(TEST_DIST_ID_1), 0.001);
    }

    @Test
    @Order(8)
    void testGetDistributorsWithMismatchedPayments() throws Exception {
        // Insert a distributor with a balance that doesn't match orders - payments
        // Balance = 9999 (intentionally wrong), orders cost = 0, payments = 0 => mismatch
        service.enterNewDistributor(TEST_DIST_ID_1, "Mismatch Dist", "Retail",
                "555-888-9999", "Mismatch Rep", "Chapel Hill", "50 Mismatch Ln", 9999.0);

        List<DistributionService.DistributorMismatchRecord> mismatched =
                service.getDistributorsWithMismatchedPayments();

        assertNotNull(mismatched);
        assertTrue(mismatched.stream().anyMatch(r -> r.getDistId() == TEST_DIST_ID_1));
    }

    @Test
    @Order(9)
    void testGetDistributorsByTypeAndCity() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "City Test Dist 1", "Retail",
                "555-100-2000", "Rep A", "Raleigh, NC", "60 City St", 0.0);
        service.enterNewDistributor(TEST_DIST_ID_2, "City Test Dist 2", "Retail",
                "555-200-3000", "Rep B", "Raleigh, NC", "61 City St", 0.0);

        List<DistributionService.DistributorRecord> results =
                service.getDistributorsByTypeAndCity("Retail", "Raleigh, NC");

        assertNotNull(results);
        // Should include both test distributors plus any seed Retail/Raleigh distributors
        assertTrue(results.stream().anyMatch(r -> r.getDistId() == TEST_DIST_ID_1));
        assertTrue(results.stream().anyMatch(r -> r.getDistId() == TEST_DIST_ID_2));
        // All returned records must be Retail in Raleigh
        assertTrue(results.stream().allMatch(r -> r.getCategory().equals("Retail") && r.getCity().equals("Raleigh, NC")));
    }

    @Test
    @Order(10)
    void testDeleteDistributorCascadesOrders() throws Exception {
        service.enterNewDistributor(TEST_DIST_ID_1, "Cascade Dist", "Wholesale",
                "555-300-4000", "Cascade Rep", "Fayetteville", "70 Cascade Rd", 0.0);
        service.inputOrderForEdition(TEST_ORDER_ID_1, 10, 100.00, 20.00,
                Date.valueOf("2024-04-01"), false,
                Date.valueOf("2024-04-10"), Date.valueOf("2024-04-15"),
                TEST_DIST_ID_1, EXISTING_ISBN);
        assertTrue(orderExists(TEST_ORDER_ID_1));

        service.deleteDistributor(TEST_DIST_ID_1);

        assertFalse(distributorExists(TEST_DIST_ID_1));
        assertFalse(orderExists(TEST_ORDER_ID_1));
    }

    // -------------------------------------------------------------------------
    // Helper – existence checks
    // -------------------------------------------------------------------------

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

    private String getOrderIsbn(int orderId) throws SQLException {
        String sql = "SELECT ISBN FROM Orders WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString("ISBN");
            }
        }
    }

    private int getOrderIssueId(int orderId) throws SQLException {
        String sql = "SELECT IssueID FROM Orders WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("IssueID");
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

    private boolean distPaymentExists(int paymentId, int distId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM DistPayments WHERE PaymentID = ? AND DistID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.setInt(2, distId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helper – cleanup
    // -------------------------------------------------------------------------

    private void cleanupTestData() throws SQLException {
        // Remove DistPayments for test distributors
        for (int distId : new int[]{TEST_DIST_ID_1, TEST_DIST_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM DistPayments WHERE DistID = ?")) {
                stmt.setInt(1, distId);
                stmt.executeUpdate();
            }
        }

        // Remove test payments
        for (int pid : new int[]{TEST_PAYMENT_ID_1}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Payments WHERE PaymentID = ?")) {
                stmt.setInt(1, pid);
                stmt.executeUpdate();
            }
        }

        // Remove test orders
        for (int oid : new int[]{TEST_ORDER_ID_1, TEST_ORDER_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Orders WHERE OrderID = ?")) {
                stmt.setInt(1, oid);
                stmt.executeUpdate();
            }
        }

        // Remove test distributors
        for (int distId : new int[]{TEST_DIST_ID_1, TEST_DIST_ID_2}) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Distributors WHERE DistID = ?")) {
                stmt.setInt(1, distId);
                stmt.executeUpdate();
            }
        }
    }
}
