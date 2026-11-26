/**
 * Unit tests for ReportsService.
 *
 * This class verifies the correctness of report queries by inserting
 * test data and checking the results returned by each method.
 *
 * @author Morgan Simon
 */

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * To be run on a database populated with no data
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportsServiceTest {

    private static DBManager dbManager;
    private static Connection connection;
    private static ReportsService service;

    private static final int TEST_DIST_ID_1 = 9001;
    private static final int TEST_ORDER_ID_1 = 9001;
    private static final int TEST_PUB_ID_1 = 9001;
    private static final String TEST_ISBN_1 = "9999999999991";
    private static final int TEST_PAYMENT_ID_1 = 9001;
    private static final int TEST_EMPLOYEE_ID_1 = 9001;

    @BeforeAll
    static void setUp() throws Exception {
        dbManager = new DBManager();
        dbManager.connect();
        connection = dbManager.getConnection();
        service = new ReportsService(connection);
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

    @Test
    @Order(1)
    void testGetCopiesAndTotalPricePerDistributor() throws Exception {
        insertPublication(
                TEST_PUB_ID_1,
                "Test Publication",
                "Book",
                "Technology"
        );

        insertBook(TEST_PUB_ID_1);

        insertEdition(
                TEST_ISBN_1,
                1,
                Date.valueOf("2023-12-01"),
                Date.valueOf("2024-01-01"),
                TEST_PUB_ID_1
        );

        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        insertOrderForEdition(
                TEST_ORDER_ID_1,
                25,
                500.00,
                50.00,
                Date.valueOf("2024-01-10"),
                false,
                Date.valueOf("2024-01-15"),
                Date.valueOf("2024-01-20"),
                TEST_DIST_ID_1,
                TEST_ISBN_1
        );

        List<ReportsService.CopiesAndTotalPricePerDistributorRecord> results =
                service.getCopiesAndTotalPricePerDistributor();

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(r ->
                r.getDistId() == TEST_DIST_ID_1 &&
                        r.getName().equals("Test Distributor") &&
                        r.getIsbnIssueId().equals(TEST_ISBN_1) &&
                        r.getTotalCopies() == 25 &&
                        Math.abs(r.getTotalPrice() - 500.00) < 0.001
        ));
    }

    @Test
    @Order(2)
    void testGetCopiesAndTotalPricePerWeek() throws Exception {
        insertPublication(
                TEST_PUB_ID_1,
                "Test Publication",
                "Book",
                "Technology"
        );

        insertBook(TEST_PUB_ID_1);

        insertEdition(
                TEST_ISBN_1,
                1,
                Date.valueOf("2023-12-01"),
                Date.valueOf("2024-01-01"),
                TEST_PUB_ID_1
        );

        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        insertOrderForEdition(
                TEST_ORDER_ID_1,
                25,
                500.00,
                50.00,
                Date.valueOf("2024-01-10"),
                false,
                Date.valueOf("2024-01-15"),
                Date.valueOf("2024-01-20"),
                TEST_DIST_ID_1,
                TEST_ISBN_1
        );

        List<ReportsService.CopiesAndTotalPricePerWeekRecord> results =
                service.getCopiesAndTotalPricePerWeek(
                        Date.valueOf("2024-01-01"),
                        Date.valueOf("2024-01-31")
                );

        assertNotNull(results);

        assertTrue(results.stream().anyMatch(r ->
                r.getIsbnIssueId().equals(TEST_ISBN_1) &&
                        r.getTotalCopies() == 25 &&
                        Math.abs(r.getTotalPrice() - 500.00) < 0.001
        ));
    }

    @Test
    @Order(3)
    void testGetCopiesAndTotalPricePerMonth() throws Exception {
        insertPublication(
                TEST_PUB_ID_1,
                "Test Publication",
                "Book",
                "Technology"
        );

        insertBook(TEST_PUB_ID_1);

        insertEdition(
                TEST_ISBN_1,
                1,
                Date.valueOf("2023-12-01"),
                Date.valueOf("2024-01-01"),
                TEST_PUB_ID_1
        );

        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        insertOrderForEdition(
                TEST_ORDER_ID_1,
                25,
                500.00,
                50.00,
                Date.valueOf("2024-01-10"),
                false,
                Date.valueOf("2024-01-15"),
                Date.valueOf("2024-01-20"),
                TEST_DIST_ID_1,
                TEST_ISBN_1
        );

        List<ReportsService.CopiesAndTotalPricePerMonthRecord> results =
                service.getCopiesAndTotalPricePerMonth(2024, 1);

        assertNotNull(results);

        assertTrue(results.stream().anyMatch(r ->
                r.getIsbnIssueId().equals(TEST_ISBN_1) &&
                        r.getYear() == 2024 &&
                        r.getMonth() == 1 &&
                        r.getTotalCopies() == 25 &&
                        Math.abs(r.getTotalPrice() - 500.00) < 0.001
        ));
    }

    @Test
    @Order(4)
    void testGetTotalRevenue() throws Exception {
        insertPublication(
                TEST_PUB_ID_1,
                "Test Publication",
                "Book",
                "Technology"
        );

        insertBook(TEST_PUB_ID_1);

        insertEdition(
                TEST_ISBN_1,
                1,
                Date.valueOf("2023-12-01"),
                Date.valueOf("2024-01-01"),
                TEST_PUB_ID_1
        );

        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        insertOrderForEdition(
                TEST_ORDER_ID_1,
                25,
                500.00,
                50.00,
                Date.valueOf("2024-01-10"),
                false,
                Date.valueOf("2024-01-15"),
                Date.valueOf("2024-01-20"),
                TEST_DIST_ID_1,
                TEST_ISBN_1
        );

        double revenue = service.getTotalRevenue();

        assertEquals(450.00, revenue, 0.001);
    }

    @Test
    @Order(5)
    void testGetTotalExpenses() throws Exception {
        ReportsService.TotalExpensesRecord result = service.getTotalExpenses();

        assertNotNull(result);
        assertTrue(result.getTotalShippingCost() >= 0);
        assertTrue(result.getTotalSalaries() >= 0);
        assertTrue(result.getTotalExpenses() >= 0);
        assertEquals(
                result.getTotalShippingCost() + result.getTotalSalaries(),
                result.getTotalExpenses(),
                0.001
        );
    }

    @Test
    @Order(6)
    void testGetTotalCurrentNumberOfDistributors() throws Exception {
        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        int count = service.getTotalCurrentNumberOfDistributors();

        assertTrue(count >= 1);
    }

    @Test
    @Order(7)
    void testGetTotalRevenuePerCity() throws Exception {
        insertPublication(
                TEST_PUB_ID_1,
                "Test Publication",
                "Book",
                "Technology"
        );

        insertBook(TEST_PUB_ID_1);

        insertEdition(
                TEST_ISBN_1,
                1,
                Date.valueOf("2023-12-01"),
                Date.valueOf("2024-01-01"),
                TEST_PUB_ID_1
        );

        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        insertOrderForEdition(
                TEST_ORDER_ID_1,
                25,
                500.00,
                50.00,
                Date.valueOf("2024-01-10"),
                false,
                Date.valueOf("2024-01-15"),
                Date.valueOf("2024-01-20"),
                TEST_DIST_ID_1,
                TEST_ISBN_1
        );

        List<ReportsService.TotalRevenuePerCityRecord> results =
                service.getTotalRevenuePerCity();

        assertNotNull(results);

        assertTrue(results.stream().anyMatch(r ->
                r.getCity().equals("Raleigh, NC") &&
                        Math.abs(r.getTotalRevenue() - 450.00) < 0.001
        ));
    }

    @Test
    @Order(8)
    void testGetTotalRevenuePerDistributor() throws Exception {
        insertPublication(
                TEST_PUB_ID_1,
                "Test Publication",
                "Book",
                "Technology"
        );

        insertBook(TEST_PUB_ID_1);

        insertEdition(
                TEST_ISBN_1,
                1,
                Date.valueOf("2023-12-01"),
                Date.valueOf("2024-01-01"),
                TEST_PUB_ID_1
        );

        insertDistributor(
                TEST_DIST_ID_1,
                "Test Distributor",
                "Retail",
                "555-111-2222",
                "John Doe",
                "Raleigh, NC",
                "100 Test St",
                0.0
        );

        insertOrderForEdition(
                TEST_ORDER_ID_1,
                25,
                500.00,
                50.00,
                Date.valueOf("2024-01-10"),
                false,
                Date.valueOf("2024-01-15"),
                Date.valueOf("2024-01-20"),
                TEST_DIST_ID_1,
                TEST_ISBN_1
        );

        List<ReportsService.TotalRevenuePerDistributorRecord> results =
                service.getTotalRevenuePerDistributor();

        System.out.println("Results size: " + results.size());
        for (ReportsService.TotalRevenuePerDistributorRecord r : results) {
            System.out.println(
                    "DistID=" + r.getDistId() +
                            ", TotalRevenue=" + r.getTotalRevenue()
            );
        }

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(r ->
                r.getDistId() == TEST_DIST_ID_1 &&
                        Math.abs(r.getTotalRevenue() - 450.00) < 0.001
        ));
    }

    @Test
    @Order(9)
    void testGetTotalPaymentsPerMonth() throws Exception {
        insertEmployee(
                TEST_EMPLOYEE_ID_1,
                "Test Employee",
                true
        );

        insertPayment(
                TEST_PAYMENT_ID_1,
                300.00,
                Date.valueOf("2026-03-20")
        );

        insertEmployeePayment(
                TEST_PAYMENT_ID_1,
                Date.valueOf("2026-03-15"),
                TEST_EMPLOYEE_ID_1,
                null
        );

        List<ReportsService.TotalPaymentsPerMonthRecord> results =
                service.getTotalPaymentsPerMonth(2026);

        assertNotNull(results);

        assertTrue(results.stream().anyMatch(r ->
                r.getYear() == 2026 &&
                        r.getMonth() == 3 &&
                        Math.abs(r.getTotalPayments() - 300.00) < 0.001
        ));
    }

    @Test
    @Order(10)
    void testGetTotalPaymentsPerWorkType() throws Exception {
        insertEmployee(
                TEST_EMPLOYEE_ID_1,
                "Test Employee",
                true
        );

        insertPayment(
                TEST_PAYMENT_ID_1,
                300.00,
                Date.valueOf("2026-03-20")
        );

        insertEmployeePayment(
                TEST_PAYMENT_ID_1,
                Date.valueOf("2026-03-15"),
                TEST_EMPLOYEE_ID_1,
                null
        );

        List<ReportsService.TotalPaymentsPerWorkTypeRecord> results =
                service.getTotalPaymentsPerWorkType();

        assertNotNull(results);

        assertTrue(results.stream().anyMatch(r ->
                r.getWorkType().equals("Editorial Work") &&
                        Math.abs(r.getTotalPayments() - 300.00) < 0.001
        ));
    }

    // Helper Functions
    private void insertDistributor(int distId, String name, String category,
                                   String phone, String representative, String city,
                                   String street, double balance) throws SQLException {
        String sql = """
        INSERT INTO Distributors
        (DistID, Name, Category, Phone, Representative, City, Street, Balance)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, distId);
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setString(4, phone);
            stmt.setString(5, representative);
            stmt.setString(6, city);
            stmt.setString(7, street);
            stmt.setDouble(8, balance);
            stmt.executeUpdate();
        }
    }

    private void insertPublication(int pubId, String pubTitle, String pubType, String topic) throws SQLException {
        String sql = """
        INSERT INTO Publications
        (PubID, PubTitle, PubType, Topic)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubId);
            stmt.setString(2, pubTitle);
            stmt.setString(3, pubType);
            stmt.setString(4, topic);
            stmt.executeUpdate();
        }
    }

    private void insertBook(int pubId) throws SQLException {
        String sql = """
        INSERT INTO Books (PubID)
        VALUES (?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pubId);
            stmt.executeUpdate();
        }
    }

    private void insertEdition(String isbn, int editionNum, Date writtenDate,
                               Date publishedDate, int pubId) throws SQLException {
        String sql = """
        INSERT INTO Editions
        (ISBN, EditionNum, WrittenDate, PublishedDate, PubID)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.setInt(2, editionNum);
            stmt.setDate(3, writtenDate);
            stmt.setDate(4, publishedDate);
            stmt.setInt(5, pubId);
            stmt.executeUpdate();
        }
    }

    private void insertOrderForEdition(int orderId, int numCopy, double price, double shippingCost,
                                       Date placeDate, boolean isPrinted, Date plannedPrintDate,
                                       Date deliveryDate, int distId, String isbn) throws SQLException {
        String sql = """
        INSERT INTO Orders
        (OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, numCopy);
            stmt.setDouble(3, price);
            stmt.setDouble(4, shippingCost);
            stmt.setDate(5, placeDate);
            stmt.setBoolean(6, isPrinted);
            stmt.setDate(7, plannedPrintDate);
            stmt.setDate(8, deliveryDate);
            stmt.setInt(9, distId);
            stmt.setString(10, isbn);
            stmt.executeUpdate();
        }
    }

    private void insertEmployee(int employeeId, String name, boolean isStaff) throws SQLException {
        String sql = """
        INSERT INTO Employees
        (EmployeeID, Name, IsStaff)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, name);
            stmt.setBoolean(3, isStaff);
            stmt.executeUpdate();
        }
    }

    private void insertPayment(int paymentId, double amount, Date claimDate) throws SQLException {
        String sql = """
        INSERT INTO Payments
        (PaymentID, Amount, ClaimDate)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.setDouble(2, amount);
            stmt.setDate(3, claimDate);
            stmt.executeUpdate();
        }
    }

    private void insertEmployeePayment(int paymentId, Date issueDate, int employeeId, Integer publicationUnitId)
            throws SQLException {
        String sql = """
        INSERT INTO EmployeePayments
        (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.setDate(2, issueDate);
            stmt.setInt(3, employeeId);

            if (publicationUnitId == null) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, publicationUnitId);
            }

            stmt.executeUpdate();
        }
    }
    // Cleanup
    private void cleanupTestData() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM EmployeePayments WHERE PaymentID = ?")) {
            stmt.setInt(1, TEST_PAYMENT_ID_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Payments WHERE PaymentID = ?")) {
            stmt.setInt(1, TEST_PAYMENT_ID_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Employees WHERE EmployeeID = ?")) {
            stmt.setInt(1, TEST_EMPLOYEE_ID_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Orders WHERE OrderID = ?")) {
            stmt.setInt(1, TEST_ORDER_ID_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Editions WHERE ISBN = ?")) {
            stmt.setString(1, TEST_ISBN_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Books WHERE PubID = ?")) {
            stmt.setInt(1, TEST_PUB_ID_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Publications WHERE PubID = ?")) {
            stmt.setInt(1, TEST_PUB_ID_1);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Distributors WHERE DistID = ?")) {
            stmt.setInt(1, TEST_DIST_ID_1);
            stmt.executeUpdate();
        }
    }
}