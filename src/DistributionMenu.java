import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Console menu for the Distribution task group.
 *
 * Responsibilities:
 * 1. Display the Distribution sub-menu and read user selections
 * 2. Collect input fields required by each operation and forward them to DistributionService
 * 3. Format and print query results (mismatched payments, distributor listings) to standard output
 * 4. Handle navigation back to the main menu
 */
public class DistributionMenu {

    private final Input input;
    private final DistributionService service;

    /**
     * Constructs a DistributionMenu with the given database connection and input handler.
     *
     * @param connection the active database connection passed to DistributionService.
     * @param input the input handler used to read user input from the console.
     */
    public DistributionMenu(Connection connection, Input input) {
        this.input = input;
        this.service = new DistributionService(connection);
    }

    /**
     * Starts the Distribution menu loop, repeatedly prompting the user until they choose to exit.
     * Catches and reports any SQLException thrown by the individual operation methods.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = input.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        enterNewDistributor();
                        break;
                    case 2:
                        updateDistributorInformation();
                        break;
                    case 3:
                        deleteDistributor();
                        break;
                    case 4:
                        inputOrderForEdition();
                        break;
                    case 5:
                        inputOrderForIssue();
                        break;
                    case 6:
                        billDistributorForOrder();
                        break;
                    case 7:
                        receivePayment();
                        break;
                    case 8:
                        identifyMismatchedPayments();
                        break;
                    case 9:
                        listDistributorsByTypeAndCity();
                        break;
                    case 10:
                        running = false;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("Database operation failed.");
                e.printStackTrace();
            }

            if (running) {
                System.out.println();
            }
        }
    }

    /**
     * Prints the Distribution sub-menu options to standard output.
     */
    private void printMenu() {
        System.out.println("===============================================");
        System.out.println("Distribution");
        System.out.println("===============================================");
        System.out.println("1  Enter new distributor");
        System.out.println("2  Update distributor information");
        System.out.println("3  Delete distributor");
        System.out.println("4  Input order for book edition");
        System.out.println("5  Input order for periodical issue");
        System.out.println("6  Bill distributor for an order");
        System.out.println("7  Receive payment from distributor");
        System.out.println("8  Identify distributors with mismatched payments");
        System.out.println("9  List distributors by type and city");
        System.out.println("10 Back to main menu");
        System.out.println("===============================================");
    }

    /**
     * Prompts the user for all fields of a new distributor and delegates insertion to the service.
     *
     * @throws SQLException if the database insert fails.
     */
    private void enterNewDistributor() throws SQLException {
        Integer distId = input.readInt("DistID: ");
        String name = input.readString("Name: ");
        String category = input.readString("Category (Retail / Academic / Wholesale): ");
        String phone = input.readString("Phone: ");
        String representative = input.readString("Representative: ");
        String city = input.readString("City: ");
        String street = input.readString("Street: ");
        Double balance = input.readDouble("Initial Balance: ");

        service.enterNewDistributor(distId, name, category, phone, representative, city, street, balance);
        System.out.println("Distributor inserted.");
    }

    /**
     * Prompts the user for updated distributor fields and delegates the update to the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database update fails.
     */
    private void updateDistributorInformation() throws SQLException {
        Integer distId = input.readInt("DistID: ");
        String name = input.readString("New Name: ");
        String category = input.readString("New Category: ");
        String phone = input.readString("New Phone: ");
        String representative = input.readString("New Representative: ");
        String city = input.readString("New City: ");
        String street = input.readString("New Street: ");

        int rows = service.updateDistributorInformation(distId, name, category, phone, representative, city, street);
        if (rows > 0) {
            System.out.println("Distributor updated.");
        } else {
            System.out.println("Distributor not found.");
        }
    }

    /**
     * Prompts the user for a distributor ID and delegates deletion to the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database delete fails.
     */
    private void deleteDistributor() throws SQLException {
        Integer distId = input.readInt("DistID: ");

        int rows = service.deleteDistributor(distId);
        if (rows > 0) {
            System.out.println("Distributor deleted.");
        } else {
            System.out.println("Distributor not found.");
        }
    }

    /**
     * Prompts the user for all fields of a new order for a book edition and delegates insertion to the service.
     *
     * @throws SQLException if the database insert fails.
     */
    private void inputOrderForEdition() throws SQLException {
        Integer orderId = input.readInt("OrderID: ");
        Integer numCopy = input.readInt("Number of Copies: ");
        Double price = input.readDouble("Price: ");
        Double shippingCost = input.readDouble("Shipping Cost: ");
        Date placeDate = input.readDate("Place Date (YYYY-MM-DD): ");
        Integer isPrintedInt = input.readInt("Is Printed (1=yes, 0=no): ");
        Date plannedPrintDate = input.readDate("Planned Print Date (YYYY-MM-DD): ");
        Date deliveryDate = input.readDate("Delivery Date (YYYY-MM-DD): ");
        Integer distId = input.readInt("DistID: ");
        String isbn = input.readString("ISBN: ");

        service.inputOrderForEdition(orderId, numCopy, price, shippingCost, placeDate,
                isPrintedInt == 1, plannedPrintDate, deliveryDate, distId, isbn);
        System.out.println("Order for edition inserted.");
    }

    /**
     * Prompts the user for all fields of a new order for a periodical issue and delegates insertion to the service.
     *
     * @throws SQLException if the database insert fails.
     */
    private void inputOrderForIssue() throws SQLException {
        Integer orderId = input.readInt("OrderID: ");
        Integer numCopy = input.readInt("Number of Copies: ");
        Double price = input.readDouble("Price: ");
        Double shippingCost = input.readDouble("Shipping Cost: ");
        Date placeDate = input.readDate("Place Date (YYYY-MM-DD): ");
        Integer isPrintedInt = input.readInt("Is Printed (1=yes, 0=no): ");
        Date plannedPrintDate = input.readDate("Planned Print Date (YYYY-MM-DD): ");
        Date deliveryDate = input.readDate("Delivery Date (YYYY-MM-DD): ");
        Integer distId = input.readInt("DistID: ");
        Integer issueId = input.readInt("IssueID: ");

        service.inputOrderForIssue(orderId, numCopy, price, shippingCost, placeDate,
                isPrintedInt == 1, plannedPrintDate, deliveryDate, distId, issueId);
        System.out.println("Order for issue inserted.");
    }

    /**
     * Prompts the user for an order ID and bills the associated distributor by delegating to the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database update fails.
     */
    private void billDistributorForOrder() throws SQLException {
        Integer orderId = input.readInt("OrderID: ");

        int rows = service.billDistributorForOrder(orderId);
        if (rows > 0) {
            System.out.println("Distributor billed for order " + orderId + ".");
        } else {
            System.out.println("Order not found.");
        }
    }

    /**
     * Prompts the user for payment details and delegates payment recording to the service.
     *
     * @throws SQLException if the database insert or update fails.
     */
    private void receivePayment() throws SQLException {
        Integer paymentId = input.readInt("PaymentID: ");
        Double amount = input.readDouble("Amount: ");
        Integer distId = input.readInt("DistID: ");

        service.receivePayment(paymentId, amount, distId);
        System.out.println("Payment recorded and balance updated.");
    }

    /**
     * Retrieves and displays distributors whose stored balance does not match their billed-minus-paid total.
     * Prints a table of mismatched records or a message if none are found.
     *
     * @throws SQLException if the database query fails.
     */
    private void identifyMismatchedPayments() throws SQLException {
        List<DistributionService.DistributorMismatchRecord> records =
                service.getDistributorsWithMismatchedPayments();

        if (records.isEmpty()) {
            System.out.println("No distributors with mismatched payment records found.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-8s %-28s %-12s %-14s %-12s%n",
                "DistID", "Name", "Balance", "Total Billed", "Total Paid");
        System.out.println("----------------------------------------------------------------------------------");

        for (DistributionService.DistributorMismatchRecord r : records) {
            System.out.printf("%-8d %-28s %-12.2f %-14.2f %-12.2f%n",
                    r.getDistId(), r.getName(), r.getBalance(),
                    r.getTotalBilled(), r.getTotalPaid());
        }

        System.out.println("----------------------------------------------------------------------------------");
    }

    /**
     * Prompts the user for a distributor category and city, then retrieves and displays matching distributors.
     * Prints a formatted table or a message if no distributors are found.
     *
     * @throws SQLException if the database query fails.
     */
    private void listDistributorsByTypeAndCity() throws SQLException {
        String category = input.readString("Category (Retail / Academic / Wholesale): ");
        String city = input.readString("City: ");

        List<DistributionService.DistributorRecord> records =
                service.getDistributorsByTypeAndCity(category, city);

        if (records.isEmpty()) {
            System.out.println("No distributors found for that type and city.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-28s %-12s %-15s %-20s %-15s %-25s %-10s%n",
                "DistID", "Name", "Category", "Phone", "Representative", "City", "Street", "Balance");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");

        for (DistributionService.DistributorRecord r : records) {
            System.out.printf("%-8d %-28s %-12s %-15s %-20s %-15s %-25s %-10.2f%n",
                    r.getDistId(), r.getName(), r.getCategory(), r.getPhone(),
                    r.getRepresentative(), r.getCity(), r.getStreet(), r.getBalance());
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
