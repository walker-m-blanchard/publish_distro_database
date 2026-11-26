import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data-access service for all Distribution operations against the GutenbergDb schema.
 *
 * Responsibilities:
 * 1. Insert, update, and delete distributor records (with cascading cleanup of orders and payments)
 * 2. Create orders for book editions and periodical issues
 * 3. Bill a distributor for an order by incrementing their account balance
 * 4. Record incoming distributor payments and decrement the corresponding balance
 * 5. Identify distributors whose stored balance does not match billed-minus-paid totals
 * 6. Retrieve distributors filtered by category and city
 */
public class DistributionService {
    private final Connection connection;

    /**
     * Constructs a DistributionService with the given database connection.
     *
     * @param connection the active database connection used for all SQL operations.
     */
    public DistributionService(Connection connection) {
        this.connection = connection;
    }

    // Operation 1: Enter new distributor
    /**
     * Inserts a new distributor record into the Distributors table.
     *
     * @param distId         the unique identifier for the distributor.
     * @param name           the name of the distributor.
     * @param category       the category of the distributor (Retail, Academic, or Wholesale).
     * @param phone          the phone number of the distributor.
     * @param representative the name of the contact person at the distributor.
     * @param city           the city where the distributor is located.
     * @param street         the street address of the distributor.
     * @param balance        the initial account balance owed to GutenbergPubs.
     * @throws SQLException if the database insert fails.
     */
    public void enterNewDistributor(Integer distId, String name, String category, String phone,
                                    String representative, String city, String street,
                                    Double balance) throws SQLException {
        String sql = "INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, distId);
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setString(4, phone);
            stmt.setString(5, representative);
            stmt.setString(6, city);
            stmt.setString(7, street);
            stmt.setObject(8, balance);
            stmt.executeUpdate();
        }
    }

    // Operation 2: Update distributor information
    /**
     * Updates the contact and address information of an existing distributor.
     *
     * @param distId         the ID of the distributor to update.
     * @param name           the new name of the distributor.
     * @param category       the new category of the distributor.
     * @param phone          the new phone number.
     * @param representative the new contact person name.
     * @param city           the new city.
     * @param street         the new street address.
     * @return the number of rows affected (1 if updated, 0 if not found).
     * @throws SQLException if the database update fails.
     */
    public int updateDistributorInformation(Integer distId, String name, String category, String phone,
                                            String representative, String city, String street) throws SQLException {
        Map<String, Object> updateFields = new HashMap<>();

        if (name != null) {
            updateFields.put("Name", name);
        }
        if (category != null) {
            updateFields.put("Category", category);
        }
        if (phone != null) {
            updateFields.put("Phone", phone);
        }
        if (representative != null) {
            updateFields.put("Representative", representative);
        }
        if (city != null) {
            updateFields.put("City", city);
        }
        if (street != null) {
            updateFields.put("Street", street);
        }


        StringBuilder sql = new StringBuilder("UPDATE Distributors SET ");
        List<Object> parameters = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, Object> item : updateFields.entrySet()) {
            sql.append(item.getKey()).append(" = ?");
            parameters.add(item.getValue());

            if (++count < updateFields.size()) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ").append("DistID").append(" = ?");
        parameters.add(distId);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 1; i <= parameters.size(); i++) {
                stmt.setObject(i, parameters.get(i - 1));
            }
            return stmt.executeUpdate();
        }
    }

    // Operation 3: Delete a distributor (with cascading cleanup)
    /**
     * Deletes a distributor and all associated orders and payment records in a single transaction.
     * Rolls back automatically if any step fails.
     *
     * @param distId the ID of the distributor to delete.
     * @return the number of rows deleted from the Distributors table (1 if deleted, 0 if not found).
     * @throws SQLException if any database operation fails.
     */
    public int deleteDistributor(Integer distId) throws SQLException {
        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            // Attempt to remove DistPayments referencing this distributor
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM DistPayments WHERE DistID = ?")) {
                stmt.setObject(1, distId);
                stmt.executeUpdate();
            }

            // Attempt to remove Orders referencing this distributor
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Orders WHERE DistID = ?")) {
                stmt.setObject(1, distId);
                stmt.executeUpdate();
            }

            //Values greater than 0 indicate successful location and removal
            int rows;
            // Attempt to delete the distributor
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM Distributors WHERE DistID = ?")) {
                stmt.setObject(1, distId);
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

    // Operation 4: Input order for a book edition
    /**
     * Inserts a new order for a specific book edition into the Orders table.
     *
     * @param orderId          the unique identifier for the order.
     * @param numCopy          the number of copies requested.
     * @param price            the total price of the order.
     * @param shippingCost     the shipping cost for the order.
     * @param placeDate        the date the order was placed.
     * @param isPrinted        whether the edition has already been printed.
     * @param plannedPrintDate the planned date for printing.
     * @param deliveryDate     the requested delivery date.
     * @param distId           the ID of the distributor placing the order.
     * @param isbn             the ISBN of the book edition being ordered.
     * @throws SQLException if the database insert fails.
     */
    public void inputOrderForEdition(Integer orderId, Integer numCopy, Double price, Double shippingCost,
                                     Date placeDate, boolean isPrinted, Date plannedPrintDate,
                                     Date deliveryDate, Integer distId, String isbn) throws SQLException {
        String sql = "INSERT INTO Orders (OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, " +
                     "PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, orderId);
            stmt.setObject(2, numCopy);
            stmt.setObject(3, price);
            stmt.setObject(4, shippingCost);
            stmt.setDate(5, placeDate);
            stmt.setBoolean(6, isPrinted);
            stmt.setDate(7, plannedPrintDate);
            stmt.setDate(8, deliveryDate);
            stmt.setObject(9, distId);
            stmt.setString(10, isbn);
            stmt.executeUpdate();
        }
    }

    // Operation 4 (cont.): Input order for a periodical issue
    /**
     * Inserts a new order for a specific periodical issue into the Orders table.
     *
     * @param orderId          the unique identifier for the order.
     * @param numCopy          the number of copies requested.
     * @param price            the total price of the order.
     * @param shippingCost     the shipping cost for the order.
     * @param placeDate        the date the order was placed.
     * @param isPrinted        whether the issue has already been printed.
     * @param plannedPrintDate the planned date for printing.
     * @param deliveryDate     the requested delivery date.
     * @param distId           the ID of the distributor placing the order.
     * @param issueId          the ID of the periodical issue being ordered.
     * @throws SQLException if the database insert fails.
     */
    public void inputOrderForIssue(Integer orderId, Integer numCopy, Double price, Double shippingCost,
                                   Date placeDate, boolean isPrinted, Date plannedPrintDate,
                                   Date deliveryDate, Integer distId, Integer issueId) throws SQLException {
        String sql = "INSERT INTO Orders (OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, " +
                     "PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, orderId);
            stmt.setObject(2, numCopy);
            stmt.setObject(3, price);
            stmt.setObject(4, shippingCost);
            stmt.setDate(5, placeDate);
            stmt.setBoolean(6, isPrinted);
            stmt.setDate(7, plannedPrintDate);
            stmt.setDate(8, deliveryDate);
            stmt.setObject(9, distId);
            stmt.setObject(10, issueId);
            stmt.executeUpdate();
        }
    }

    // Operation 5: Bill distributor for an order (increase balance by order cost)
    /**
     * Increases the balance of the distributor associated with the given order by the order's price.
     *
     * @param orderId the ID of the order to bill.
     * @return the number of rows affected (1 if billed, 0 if order not found).
     * @throws SQLException if the database update fails.
     */
    public int billDistributorForOrder(Integer orderId) throws SQLException {
        String sql = "UPDATE Distributors d " +
                "JOIN Orders o ON d.DistID = o.DistID " +
                "SET d.Balance = d.Balance + o.Price " +
                "WHERE o.OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, orderId);
            return stmt.executeUpdate();
        }
    }

    // Operation 6: Receive a payment from distributor and decrease balance
    /**
     * Records an incoming payment from a distributor and decrements their account balance in a single transaction.
     * Inserts a Payments record, links it via DistPayments, and updates the distributor's balance.
     * Rolls back automatically if any step fails.
     *
     * @param paymentId the unique identifier for the payment.
     * @param amount    the payment amount received.
     * @param distId    the ID of the distributor making the payment.
     * @throws SQLException if any database operation fails.
     */
    public void receivePayment(Integer paymentId, Double amount, Integer distId) throws SQLException {
        try { //Will catch any errors thrown by database and rollback
            //Start transaction
            connection.setAutoCommit(false);

            // Attempt to insert the payment
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Payments (PaymentID, Amount, ClaimDate) VALUES (?, ?, NULL)")) {
                stmt.setObject(1, paymentId);
                stmt.setObject(2, amount);
                stmt.executeUpdate();
            }

            //Attempt to insert the distributor payment
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO DistPayments (PaymentID, DistID) VALUES (?, ?)")) {
                stmt.setObject(1, paymentId);
                stmt.setObject(2, distId);
                stmt.executeUpdate();
            }

            //Attempt to update the distributor's balance
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Distributors SET Balance = Balance - ? WHERE DistID = ?")) {
                stmt.setObject(1, amount);
                stmt.setObject(2, distId);
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

    // Operation 7: Identify distributors whose total billed amount != sum of recorded payments
    /**
     * Returns all distributors whose stored balance does not match the difference between
     * their total billed amount (sum of order prices) and total paid amount (sum of payments).
     *
     * @return a list of DistributorMismatchRecord objects, one per mismatched distributor.
     * @throws SQLException if the database query fails.
     */
    public List<DistributorMismatchRecord> getDistributorsWithMismatchedPayments() throws SQLException {
        String sql =
            "SELECT d.DistID, d.Name, d.Balance, " +
            "       COALESCE(tb.TotalBilled, 0) AS TotalBilled, " +
            "       COALESCE(tp.TotalPaid, 0) AS TotalPaid " +
            "FROM Distributors d " +
            "LEFT JOIN ( " +
            "    SELECT DistID, SUM(Price) AS TotalBilled " +
            "    FROM Orders " +
            "    GROUP BY DistID " +
            ") tb ON d.DistID = tb.DistID " +
            "LEFT JOIN ( " +
            "    SELECT dp.DistID, SUM(p.Amount) AS TotalPaid " +
            "    FROM DistPayments dp " +
            "    JOIN Payments p ON dp.PaymentID = p.PaymentID " +
            "    GROUP BY dp.DistID " +
            ") tp ON d.DistID = tp.DistID " +
            "WHERE ABS(d.Balance - (COALESCE(tb.TotalBilled, 0) - COALESCE(tp.TotalPaid, 0))) > 0.001 " +
            "ORDER BY d.DistID";

        List<DistributorMismatchRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new DistributorMismatchRecord(
                        rs.getInt("DistID"),
                        rs.getString("Name"),
                        rs.getDouble("Balance"),
                        rs.getDouble("TotalBilled"),
                        rs.getDouble("TotalPaid")
                ));
            }
        }
        return results;
    }

    // Operation 8: List all distributors of a specific type located in a given city
    /**
     * Returns all distributors matching the given category and city, ordered by DistID.
     *
     * @param category the distributor category to filter by (e.g. Retail, Academic, Wholesale).
     * @param city     the city to filter by.
     * @return a list of DistributorRecord objects matching the filter criteria.
     * @throws SQLException if the database query fails.
     */
    public List<DistributorRecord> getDistributorsByTypeAndCity(String category, String city) throws SQLException {
        String sql = "SELECT DistID, Name, Category, Phone, Representative, City, Street, Balance " +
                     "FROM Distributors WHERE Category = ? AND City = ? ORDER BY DistID";
        List<DistributorRecord> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            stmt.setString(2, city);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new DistributorRecord(
                            rs.getInt("DistID"),
                            rs.getString("Name"),
                            rs.getString("Category"),
                            rs.getString("Phone"),
                            rs.getString("Representative"),
                            rs.getString("City"),
                            rs.getString("Street"),
                            rs.getDouble("Balance")
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
     * Record representing a Distributor.
     */
    public static class DistributorRecord {
        private final Integer distId;
        private final String name;
        private final String category;
        private final String phone;
        private final String representative;
        private final String city;
        private final String street;
        private final Double balance;

        /**
         * Constructs a DistributorRecord with all distributor fields.
         *
         * @param distId         the distributor's unique ID.
         * @param name           the distributor's name.
         * @param category       the distributor's category.
         * @param phone          the distributor's phone number.
         * @param representative the name of the contact person.
         * @param city           the city where the distributor is located.
         * @param street         the street address of the distributor.
         * @param balance        the current account balance owed to GutenbergPubs.
         */
        public DistributorRecord(Integer distId, String name, String category, String phone,
                                 String representative, String city, String street, Double balance) {
            this.distId = distId;
            this.name = name;
            this.category = category;
            this.phone = phone;
            this.representative = representative;
            this.city = city;
            this.street = street;
            this.balance = balance;
        }

        /**
         * Returns the distributor's unique ID.
         *
         * @return the distributor ID.
         */
        public Integer getDistId() { return distId; }

        /**
         * Returns the distributor's name.
         *
         * @return the distributor name.
         */
        public String getName() { return name; }

        /**
         * Returns the distributor's category.
         *
         * @return the category string.
         */
        public String getCategory() { return category; }

        /**
         * Returns the distributor's phone number.
         *
         * @return the phone number string.
         */
        public String getPhone() { return phone; }

        /**
         * Returns the name of the distributor's contact person.
         *
         * @return the representative name.
         */
        public String getRepresentative() { return representative; }

        /**
         * Returns the city where the distributor is located.
         *
         * @return the city string.
         */
        public String getCity() { return city; }

        /**
         * Returns the street address of the distributor.
         *
         * @return the street string.
         */
        public String getStreet() { return street; }

        /**
         * Returns the current account balance owed to GutenbergPubs.
         *
         * @return the balance as a double.
         */
        public Double getBalance() { return balance; }
    }

    /**
     * Record representing a Distributor's mismatched balance.
     */
    public static class DistributorMismatchRecord {
        private final Integer distId;
        private final String name;
        private final Double balance;
        private final Double totalBilled;
        private final Double totalPaid;

        /**
         * Constructs a DistributorMismatchRecord with balance reconciliation data.
         *
         * @param distId       the distributor's unique ID.
         * @param name         the distributor's name.
         * @param balance      the stored account balance.
         * @param totalBilled  the sum of all order prices billed to this distributor.
         * @param totalPaid    the sum of all payments recorded from this distributor.
         */
        public DistributorMismatchRecord(Integer distId, String name, Double balance,
                                         Double totalBilled, Double totalPaid) {
            this.distId = distId;
            this.name = name;
            this.balance = balance;
            this.totalBilled = totalBilled;
            this.totalPaid = totalPaid;
        }

        /**
         * Returns the distributor's unique ID.
         *
         * @return the distributor ID.
         */
        public Integer getDistId() { return distId; }

        /**
         * Returns the distributor's name.
         *
         * @return the distributor name.
         */
        public String getName() { return name; }

        /**
         * Returns the stored account balance of the distributor.
         *
         * @return the balance as a double.
         */
        public Double getBalance() { return balance; }

        /**
         * Returns the total amount billed to the distributor across all orders.
         *
         * @return the total billed amount as a double.
         */
        public Double getTotalBilled() { return totalBilled; }

        /**
         * Returns the total amount paid by the distributor across all recorded payments.
         *
         * @return the total paid amount as a double.
         */
        public Double getTotalPaid() { return totalPaid; }
    }
}
