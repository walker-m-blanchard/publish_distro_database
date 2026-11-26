/**
 * This class handles all database operations related to report generation.
 *
 * It executes SQL queries to compute and retrieve report data such as
 * revenue, expenses, and payment summaries. The results are returned
 * as structured records to be used by the ReportsMenu class.
 *
 * @author Morgan Simon
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 * This class handles the construction and execution of proper SQL statements for the reports operations.
 */
public class ReportsService {
    private final Connection connection;

    /**
     * Initializes the ReportsService with a database connection.
     *
     * @param connection the database connection used to execute queries
     */
    public ReportsService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves the number of copies and total price of publications
     * purchased per distributor.
     *
     * Orders are grouped by distributor and publication (ISBN or IssueID),
     * and aggregated to compute total copies and total price for each group.
     *
     * @return list of records containing distributor ID, name, publication ID,
     *         total copies, and total price
     * @throws SQLException if a database error occurs while executing the query
     */

    public List<CopiesAndTotalPricePerDistributorRecord> getCopiesAndTotalPricePerDistributor() throws SQLException {
        String sql =
                "SELECT d.DistID, d.Name, COALESCE(o.ISBN, o.IssueID) AS ISBN_IssueID, " +
                "       SUM(o.NumCopy) AS TotalCopies, " +
                "       SUM(o.Price) AS TotalPrice " +
                "FROM Distributors d " +
                "JOIN Orders o ON d.DistID = o.DistID " +
                "GROUP BY d.DistID, d.Name, COALESCE(o.ISBN, o.IssueID) ";

        List<CopiesAndTotalPricePerDistributorRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new CopiesAndTotalPricePerDistributorRecord(
                        rs.getInt("DistID"),
                        rs.getString("Name"),
                        rs.getString("ISBN_IssueID"),
                        rs.getInt("TotalCopies"),
                        rs.getDouble("TotalPrice")
                ));
            }
        }

        return results;
    }

    /**
     * Retrieves the total number of copies and total price of publications
     * per week within a user-specified date range.
     *
     * Orders are filtered by the provided start and end dates, then grouped
     * by publication (ISBN or IssueID) to compute total copies and total price.
     *
     * @param startDate the beginning of the date range
     * @param endDate the end of the date range
     * @return list of records containing publication ID, total copies, and total price
     * @throws SQLException if a database error occurs while executing the query
     */
    public List<CopiesAndTotalPricePerWeekRecord> getCopiesAndTotalPricePerWeek(Date startDate, Date endDate)
            throws SQLException {
        String sql =
                "SELECT COALESCE(o.ISBN, o.IssueID) AS ISBN_IssueID, " +
                "       SUM(o.NumCopy) AS TotalCopies, " +
                "       SUM(o.Price) AS TotalPrice " +
                "FROM Orders o " +
                "WHERE o.PlaceDate BETWEEN ? AND ? " +
                "GROUP BY COALESCE(o.ISBN, o.IssueID)";

        List<CopiesAndTotalPricePerWeekRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new CopiesAndTotalPricePerWeekRecord(
                            rs.getString("ISBN_IssueID"),
                            rs.getInt("TotalCopies"),
                            rs.getDouble("TotalPrice")
                    ));
                }
            }
        }

        return results;
    }

    /**
     * Retrieves the total number of copies and total price of publications
     * for a specific month and year.
     *
     * Orders are filtered by the given year and month, then grouped by
     * publication (ISBN or IssueID) to compute total copies and total price.
     *
     * @param year the year of the report
     * @param month the month of the report (1–12)
     * @return list of records containing publication ID, year, month,
     *         total copies, and total price
     * @throws SQLException if a database error occurs while executing the query
     */
    public List<CopiesAndTotalPricePerMonthRecord> getCopiesAndTotalPricePerMonth(Integer year, Integer month)
            throws SQLException {
        String sql =
                "SELECT COALESCE(o.ISBN, o.IssueID) AS ISBN_IssueID, " +
                "       YEAR(o.PlaceDate) AS Year, " +
                "       MONTH(o.PlaceDate) AS Month, " +
                "       SUM(o.NumCopy) AS TotalCopies, " +
                "       SUM(o.Price) AS TotalPrice " +
                "FROM Orders o " +
                "WHERE YEAR(o.PlaceDate) = ? AND MONTH(o.PlaceDate) = ? " +
                "GROUP BY YEAR(o.PlaceDate), MONTH(o.PlaceDate), COALESCE(o.ISBN, o.IssueID)";

        List<CopiesAndTotalPricePerMonthRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, year);
            stmt.setObject(2, month);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new CopiesAndTotalPricePerMonthRecord(
                            rs.getString("ISBN_IssueID"),
                            rs.getInt("Year"),
                            rs.getInt("Month"),
                            rs.getInt("TotalCopies"),
                            rs.getDouble("TotalPrice")
                    ));
                }
            }
        }

        return results;
    }

    /**
     * Calculates the total revenue of the publishing house.
     *
     * Assumption: Revenue is defined as total order price minus shipping costs, since shipping is an expense borne by the
     * publishing house.
     *
     * Revenue is computed as the sum of order prices minus the sum of
     * shipping costs across all orders.
     *
     * @return total revenue value
     * @throws SQLException if a database error occurs while executing the query
     */
    public double getTotalRevenue() throws SQLException {
        String sql =
                "SELECT SUM(Price) - SUM(ShippingCost) AS TotalRevenue " +
                "FROM Orders";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("TotalRevenue");
            }
        }

        return 0.0;
    }

    /**
     * Calculates the total expenses of the publishing house.
     *
     * Expenses include both shipping costs from orders and salary payments
     * to employees. Salary payments are identified as payments that are not
     * associated with a specific publication unit (PublicationUnitID in EmployeePayments is NULL).
     *
     * @return a record containing total shipping costs, total salaries, and total expenses
     * @throws SQLException if a database error occurs while executing the query
     */
    public TotalExpensesRecord getTotalExpenses() throws SQLException {
        String sql =
                "SELECT " +
                "    (SELECT COALESCE(SUM(o.ShippingCost), 0) " +
                "     FROM Orders o) AS TotalShippingCost, " +
                "    (SELECT COALESCE(SUM(p.Amount), 0) " +
                "     FROM Payments p " +
                "     JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID " +
                "     WHERE ep.PublicationUnitID IS NULL) AS TotalSalaries, " +
                "    (SELECT COALESCE(SUM(o.ShippingCost), 0) FROM Orders o) + " +
                "    (SELECT COALESCE(SUM(p.Amount), 0) " +
                "     FROM Payments p " +
                "     JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID " +
                "     WHERE ep.PublicationUnitID IS NULL) AS TotalExpenses";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new TotalExpensesRecord(
                        rs.getDouble("TotalShippingCost"),
                        rs.getDouble("TotalSalaries"),
                        rs.getDouble("TotalExpenses")
                );
            }
        }

        return new TotalExpensesRecord(0.0, 0.0, 0.0);
    }

    /**
     * Retrieves the total number of distributors currently in the system.
     *
     * @return total number of distributors
     * @throws SQLException if a database error occurs while executing the query
     */
    public int getTotalCurrentNumberOfDistributors() throws SQLException {
        String sql =
                "SELECT COUNT(*) AS TotalDistributors " +
                "FROM Distributors";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TotalDistributors");
            }
        }

        return 0;
    }

    /**
     * Retrieves the total revenue generated per city.
     *
     * Revenue is calculated as the sum of order prices minus shipping costs,
     * grouped by the city of each distributor.
     *
     * @return list of records containing city and total revenue
     * @throws SQLException if a database error occurs while executing the query
     */
    public List<TotalRevenuePerCityRecord> getTotalRevenuePerCity() throws SQLException {
        String sql =
                "SELECT d.City, " +
                "       SUM(o.Price) - SUM(o.ShippingCost) AS TotalRevenue " +
                "FROM Distributors d " +
                "JOIN Orders o ON d.DistID = o.DistID " +
                "GROUP BY d.City";

        List<TotalRevenuePerCityRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new TotalRevenuePerCityRecord(
                        rs.getString("City"),
                        rs.getDouble("TotalRevenue")
                ));
            }
        }

        return results;
    }

    /**
     * Retrieves the total revenue generated per distributor.
     *
     * Revenue is calculated as the sum of order prices minus shipping costs,
     * grouped by each distributor.
     *
     * @return list of records containing distributor ID, name, and total revenue
     * @throws SQLException if a database error occurs while executing the query
     */
    public List<TotalRevenuePerDistributorRecord> getTotalRevenuePerDistributor() throws SQLException {
        String sql =
                "SELECT d.DistID, d.Name, " +
                "       SUM(o.Price) - SUM(o.ShippingCost) AS TotalRevenue " +
                "FROM Distributors d " +
                "JOIN Orders o ON d.DistID = o.DistID " +
                "GROUP BY d.DistID, d.Name";

        List<TotalRevenuePerDistributorRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new TotalRevenuePerDistributorRecord(
                        rs.getInt("DistID"),
                        rs.getString("Name"),
                        rs.getDouble("TotalRevenue")
                ));
            }
        }

        return results;
    }

    /**
     * Retrieves the total payments made to Editors and Authors per month for a given year.
     *
     * Payments are calculated from records in the EmployeePayments table by joining
     * with Payments. The results are grouped by month and aggregated to compute the
     * total payment amount for each month.
     *
     * @param year the year for which monthly payments are calculated
     * @return list of records containing year, month, and total payments
     * @throws SQLException if a database error occurs while executing the query
     */
    public List<TotalPaymentsPerMonthRecord> getTotalPaymentsPerMonth(Integer year) throws SQLException {
        String sql =
                "SELECT YEAR(ep.IssueDate) AS Year, " +
                "       MONTH(ep.IssueDate) AS Month, " +
                "       SUM(p.Amount) AS TotalPayments " +
                "FROM Payments p " +
                "JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID " +
                "WHERE YEAR(ep.IssueDate) = ? " +
                "GROUP BY YEAR(ep.IssueDate), MONTH(ep.IssueDate) " +
                "ORDER BY Year, Month";

        List<TotalPaymentsPerMonthRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new TotalPaymentsPerMonthRecord(
                            rs.getInt("Year"),
                            rs.getInt("Month"),
                            rs.getDouble("TotalPayments")
                    ));
                }
            }
        }

        return results;
    }

    /**
     * Retrieves the total payments grouped by work type.
     *
     * Payments are classified into work types based on their association with
     * publication units. Payments with a NULL PublicationUnitID are treated as
     * editorial work (staff salaries). Payments linked to publication units are
     * categorized as chapter authorship or article authorship based on the
     * PublicationUnitType.
     *
     * @return list of records containing work type and total payments
     * @throws SQLException if a database error occurs while executing the query
     */
    public List<TotalPaymentsPerWorkTypeRecord> getTotalPaymentsPerWorkType() throws SQLException {
        String sql =
                "SELECT " +
                "    CASE " +
                "        WHEN ep.PublicationUnitID IS NULL THEN 'Editorial Work' " +
                "        WHEN pu.PublicationUnitType = 'Chapter' THEN 'Chapter Authorship' " +
                "        WHEN pu.PublicationUnitType = 'Article' THEN 'Article Authorship' " +
                "        ELSE 'Other' " +
                "    END AS WorkType, " +
                "    SUM(p.Amount) AS TotalPayments " +
                "FROM Payments p " +
                "JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID " +
                "LEFT JOIN PublicationUnits pu ON ep.PublicationUnitID = pu.PublicationUnitID " +
                "GROUP BY WorkType " +
                "ORDER BY WorkType";

        List<TotalPaymentsPerWorkTypeRecord> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new TotalPaymentsPerWorkTypeRecord(
                        rs.getString("WorkType"),
                        rs.getDouble("TotalPayments")
                ));
            }
        }

        return results;
    }


    // ===================================================================================
    // REPORT DATA CLASSES
    // ===================================================================================
    // Operation 1:  Number and Total Price of Copies per Distributor
    // - distributor ID
    // - distributor name
    // - publication ID
    // - total copies
    // - total price
    public static class CopiesAndTotalPricePerDistributorRecord {
        private final Integer distId;
        private final String name;
        private final String isbnIssueId;
        private final Integer totalCopies;
        private final Double totalPrice;

        public CopiesAndTotalPricePerDistributorRecord(Integer distId, String name, String isbnIssueId,
                                                    Integer totalCopies, Double totalPrice) {
            this.distId = distId;
            this.name = name;
            this.isbnIssueId = isbnIssueId;
            this.totalCopies = totalCopies;
            this.totalPrice = totalPrice;
        }

        public Integer getDistId() { return distId; }
        public String getName() { return name; }
        public String getIsbnIssueId() { return isbnIssueId; }
        public Integer getTotalCopies() { return totalCopies; }
        public Double getTotalPrice() { return totalPrice; }
    }

    // Operation 2: Number and Total Price of Copies per Week
    // - publication ID
    // - total copies
    // - total price
    public static class CopiesAndTotalPricePerWeekRecord {
        private final String isbnIssueId;
        private final Integer totalCopies;
        private final Double totalPrice;

        public CopiesAndTotalPricePerWeekRecord(String isbnIssueId, Integer totalCopies, Double totalPrice) {
            this.isbnIssueId = isbnIssueId;
            this.totalCopies = totalCopies;
            this.totalPrice = totalPrice;
        }

        public String getIsbnIssueId() { return isbnIssueId; }
        public Integer getTotalCopies() { return totalCopies; }
        public Double getTotalPrice() { return totalPrice; }
    }

    // Operation 3: Number and Total Price of Copies per Month
    // - publication ID
    // - year
    // - month
    // - total copies
    // - total price
    public static class CopiesAndTotalPricePerMonthRecord {
        private final String isbnIssueId;
        private final Integer year;
        private final Integer month;
        private final Integer totalCopies;
        private final Double totalPrice;

        public CopiesAndTotalPricePerMonthRecord(String isbnIssueId, Integer year, Integer month,
                                                Integer totalCopies, Double totalPrice) {
            this.isbnIssueId = isbnIssueId;
            this.year = year;
            this.month = month;
            this.totalCopies = totalCopies;
            this.totalPrice = totalPrice;
        }

        public String getIsbnIssueId() { return isbnIssueId; }
        public Integer getYear() { return year; }
        public Integer getMonth() { return month; }
        public Integer getTotalCopies() { return totalCopies; }
        public Double getTotalPrice() { return totalPrice; }
    }

    // Operation 5: Total Expenses (Shipping Costs and Salaries)
    // - total shipping cost
    // - total salaries
    // - total expenses
    public static class TotalExpensesRecord {
        private final Double totalShippingCost;
        private final Double totalSalaries;
        private final Double totalExpenses;

        public TotalExpensesRecord(Double totalShippingCost, Double totalSalaries, Double totalExpenses) {
            this.totalShippingCost = totalShippingCost;
            this.totalSalaries = totalSalaries;
            this.totalExpenses = totalExpenses;
        }

        public Double getTotalShippingCost() { return totalShippingCost; }
        public Double getTotalSalaries() { return totalSalaries; }
        public Double getTotalExpenses() { return totalExpenses; }
    }

    // Operation 7: Total Revenue per City
    // - city
    // - total revenue
    public static class TotalRevenuePerCityRecord {
        private final String city;
        private final Double totalRevenue;

        public TotalRevenuePerCityRecord(String city, Double totalRevenue) {
            this.city = city;
            this.totalRevenue = totalRevenue;
        }

        public String getCity() { return city; }
        public Double getTotalRevenue() { return totalRevenue; }
    }

    // Operation 8: Total Revenue per Distributor
    // - distributor ID
    // - distributor name
    // - total revenue
    public static class TotalRevenuePerDistributorRecord {
        private final Integer distId;
        private final String name;
        private final Double totalRevenue;

        public TotalRevenuePerDistributorRecord(Integer distId, String name, Double totalRevenue) {
            this.distId = distId;
            this.name = name;
            this.totalRevenue = totalRevenue;
        }

        public Integer getDistId() { return distId; }
        public String getName() { return name; }
        public Double getTotalRevenue() { return totalRevenue; }
    }

    // Operation 9: Total Payments to Editors and Authors per Month
    // - year
    // - month
    // - total payments
    public static class TotalPaymentsPerMonthRecord {
        private final Integer year;
        private final Integer month;
        private final Double totalPayments;

        public TotalPaymentsPerMonthRecord(Integer year, Integer month, Double totalPayments) {
            this.year = year;
            this.month = month;
            this.totalPayments = totalPayments;
        }

        public Integer getYear() { return year; }
        public Integer getMonth() { return month; }
        public Double getTotalPayments() { return totalPayments; }
    }

    // Operation 10: Total Payments per Work Type
    // - work type
    // - total payments
    public static class TotalPaymentsPerWorkTypeRecord {
        private final String workType;
        private final Double totalPayments;

        public TotalPaymentsPerWorkTypeRecord(String workType, Double totalPayments) {
            this.workType = workType;
            this.totalPayments = totalPayments;
        }

        public String getWorkType() { return workType; }
        public Double getTotalPayments() { return totalPayments; }
    }
}