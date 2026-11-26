import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * This class acts as a sub-menu for all operations related to the production of a book edition or periodical issue.
 * *
 * It is interacted with via the command-line interface and prompts the Input object for input and processing, then
 * hands the processed input to the ProductionService object for database interaction.
 */
public class ProductionMenu {
    private final Input input;
    private final ProductionService service;

    /**
     * Creates a new menu with the provided database connection and input handler.
     *
     * @param connection The active database connection
     * @param input      The input utility used to read user input from console
     */
    public ProductionMenu(Connection connection, Input input) {
        this.input = input;
        this.service = new ProductionService(connection);
    }

    /**
     * Starts the sub-menu loop. Displays options to the user and executes the chosen production operation (or exit).
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = input.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        enterEdition();
                        break;
                    case 2:
                        enterIssue();
                        break;
                    case 3:
                        updateEdition();
                        break;
                    case 4:
                        updateIssue();
                        break;
                    case 5:
                        deleteEdtion();
                        break;
                    case 6:
                        deleteIssue();
                        break;
                    case 7:
                        enterPublicationUnit();
                        break;
                    case 8:
                        updatePublicationUnit();
                        break;
                    case 9:
                        searchTopic();
                        break;
                    case 10:
                        searchDate();
                        break;
                    case 11:
                        searchAuthor();
                        break;
                    case 12:
                        enterEmployeePayment();
                        break;
                    case 13:
                        updateEmployeePayment();
                        break;
                    case 14:
                        searchEmployeePayments();
                        break;
                    case 15:
                        compareIssues();
                        break;
                    case 16:
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
     * Prints the production menu to console.
     */
    private void printMenu() {
        System.out.println("===============================================");
        System.out.println("Production");
        System.out.println("===============================================");
        System.out.println("1  Enter new book edition");
        System.out.println("2  Enter new issue");
        System.out.println("3  Update book edition");
        System.out.println("4  Update issue");
        System.out.println("5  Delete book edition");
        System.out.println("6  Delete issue");
        System.out.println("7  Enter article/chapter");
        System.out.println("8  Update article/chapter");
        System.out.println("9  Search by topic");
        System.out.println("10 Search by date");
        System.out.println("11 Search by author");
        System.out.println("12 Enter employee payment");
        System.out.println("13 Update employee payment claims");
        System.out.println("14 Find unclaimed employee payments");
        System.out.println("15 Compare issue articles");
        System.out.println("16 Back to main menu");
        System.out.println("===============================================");
    }

    /**
     * Prompts the user for attributes and enters a new edition into database.
     * @throws SQLException if a database access error occurs
     */
    private void enterEdition() throws SQLException {
        String isbn = input.readString("ISBN: ");
        Integer editionNum = input.readInt("Edition number: ");
        Date writtenDate = input.readDate("Date written (YYYY-MM-DD): ");
        Date publishedDate = input.readDate("Date published (YYYY-MM-DD): ");
        Integer pubID = input.readInt("Publication ID: ");

        service.enterEdition(isbn, editionNum, writtenDate, publishedDate, pubID);
        System.out.println("Edition inserted.");
    }

    /**
     * Prompts the user for attributes and enters a new issue into database.
     * @throws SQLException if a database access error occurs
     */
    private void enterIssue() throws SQLException {
        Integer issueID = input.readInt("Issue ID: ");
        String issueTitle = input.readString("Issue title: ");
        Date producedDate = input.readDate("Date produced (YYYY-MM-DD): ");
        Date publishedDate = input.readDate("Date published (YYYY-MM-DD): ");
        Integer pubID = input.readInt("Publication ID: ");

        service.enterIssue(issueID, issueTitle, producedDate, publishedDate, pubID);
        System.out.println("Issue inserted.");
    }

    /**
     * Updates the existing book edition's attributes with the given changes.
     * @throws SQLException if a database access error occurs
     */
    private void updateEdition() throws SQLException {
        String isbn = input.readString("ISBN: ");
        Integer editionNum = input.readInt("Edition number: ");
        Date writtenDate = input.readDate("Date written (YYYY-MM-DD): ");
        Date publishedDate = input.readDate("Date published (YYYY-MM-DD): ");

        int rows = service.updateEdition(isbn, editionNum, writtenDate, publishedDate);
        if (rows > 0) {
            System.out.println("Edition updated.");
        } else {
            System.out.println("Edition not found.");
        }
    }

    /**
     * Updates the existing issues's attributes with the given changes.
     * @throws SQLException if a database access error occurs
     */
    private void updateIssue() throws SQLException {
        Integer issueID = input.readInt("Issue ID: ");
        String issueTitle = input.readString("Issue title: ");
        Date producedDate = input.readDate("Date produced (YYYY-MM-DD): ");
        Date publishedDate = input.readDate("Date published (YYYY-MM-DD): ");

        int rows = service.updateIssue(issueID, issueTitle, producedDate, publishedDate);
        if (rows > 0) {
            System.out.println("Issue updated.");
        } else {
            System.out.println("Issue not found.");
        }
    }

    /**
     * Deletes the edition from the database with the given ISBN.
     * @throws SQLException if a database access error occurs
     */
    private void deleteEdtion() throws SQLException {
        String isbn = input.readString("ISBN: ");

        service.deleteEdition(isbn);
        System.out.println("Edition deleted.");
    }

    /**
     * Deletes the issue from the database with the given Issue ID.
     * @throws SQLException if a database access error occurs.
     */
    private void deleteIssue() throws SQLException {
        Integer issueID = input.readInt("Issue ID: ");

        service.deleteIssue(issueID);
        System.out.println("Issue deleted.");
    }

    /**
     * Prompts the user for attributes and enters a new publication unit (Chapter or Article).
     * @throws SQLException if a database access error occurs
     */
    private void enterPublicationUnit() throws SQLException {
        Integer publicationUnitID = input.readInt("Publication unit ID: ");
        String publicationUnitTitle = input.readString("Title: ");
        String publicationUnitType = input.readString("Type (Chapter/Article): ");
        Date writtenDate = input.readDate("Date written (YYYY-MM-DD): ");
        String fullTextContent = input.readString("Text Content: ");
        String topic = input.readString("Topic: ");
        Integer employeeID = input.readInt("Author's employee ID: ");

        service.enterPublicationUnit(publicationUnitID, publicationUnitTitle, publicationUnitType, writtenDate,
                fullTextContent, topic, employeeID);
        System.out.println("Publication Unit inserted.");
    }

    /**
     * Updates the existing publication unit's attributes with the given changes.
     * @throws SQLException if a database access error occurs
     */
    private void updatePublicationUnit() throws SQLException {
        Integer publicationUnitID = input.readInt("Publication unit ID: ");
        String publicationUnitTitle = input.readString("Title: ");
        Date writtenDate = input.readDate("Date written (YYYY-MM-DD): ");
        String fullTextContent = input.readString("Text Content: ");
        String topic = input.readString("Topic: ");
        Integer employeeID = input.readInt("Author's employee ID: ");

        int rows = service.updatePublicationUnit(publicationUnitID, publicationUnitTitle, writtenDate, fullTextContent,
                topic, employeeID);
        if (rows > 0) {
            System.out.println("Publication Unit updated.");
        } else {
            System.out.println("Publication Unit not found.");
        }
    }

    /**
     * Displays the publication units which match the requested topic.
     * @throws SQLException if a database access error occurs
     */
    private void searchTopic() throws SQLException {
        String topic = input.readString("Topic: ");

        List<ProductionService.PublicationUnitRecord> records = service.searchTopic(topic);

        if (records.isEmpty()) {
            System.out.println("No published works with that topic found.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-18s %-41s %-20s %-24s %-12s %-15s%n",
                "PublicationUnitID", "PublicationUnitTitle", "PublicationUnitType", "Topic", "WrittenDate", "AuthorName");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

        for (ProductionService.PublicationUnitRecord r : records) {
            System.out.printf("%-18d %-41s %-20s %-24s %-12s %-15s%n",
                    r.getPublicationUnitID(), r.getPublicationUnitTitle(), r.getPublicationUnitType(),
                    r.getTopic(), r.getWrittenDate(), r.getAuthorName());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Displays the publication units whose writtenDate falls between the requested dates.
     * @throws SQLException if a database access error occurs
     */
    private void searchDate() throws SQLException {
        Date startDate = input.readDate("Start date (YYYY-MM-DD): ");
        Date endDate = input.readDate("End date (YYYY-MM-DD): ");

        List<ProductionService.PublicationUnitRecord> records = service.searchDate(startDate, endDate);

        if (records.isEmpty()) {
            System.out.println("No published works found between those dates.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-18s %-41s %-20s %-24s %-12s %-15s%n",
                "PublicationUnitID", "PublicationUnitTitle", "PublicationUnitType", "Topic", "WrittenDate", "AuthorName");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

        for (ProductionService.PublicationUnitRecord r : records) {
            System.out.printf("%-18d %-41s %-20s %-24s %-12s %-15s%n",
                    r.getPublicationUnitID(), r.getPublicationUnitTitle(), r.getPublicationUnitType(),
                    r.getTopic(), r.getWrittenDate(), r.getAuthorName());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Displays the publication units which match the requested author.
     * @throws SQLException if a database access error occurs
     */
    private void searchAuthor() throws SQLException {
        String authorName = input.readString("Author Name: ");

        List<ProductionService.PublicationUnitRecord> records = service.searchAuthor(authorName);

        if (records.isEmpty()) {
            System.out.println("No published works with that author found.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-18s %-41s %-20s %-24s %-12s %-15s%n",
                "PublicationUnitID", "PublicationUnitTitle", "PublicationUnitType", "Topic", "WrittenDate", "AuthorName");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

        for (ProductionService.PublicationUnitRecord r : records) {
            System.out.printf("%-18d %-41s %-20s %-24s %-12s %-15s%n",
                    r.getPublicationUnitID(), r.getPublicationUnitTitle(), r.getPublicationUnitType(),
                    r.getTopic(), r.getWrittenDate(), r.getAuthorName());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Enters an employee payment record into the database.
     * @throws SQLException if a database access error occurs
     */
    private void enterEmployeePayment() throws SQLException {
        Integer paymentID = input.readInt("Payment ID: ");
        Double amount = input.readDouble("Amount: ");
        Date claimDate = input.readDate("Claim Date (YYYY-MM-DD): ");
        Date issueDate = input.readDate("Issued Date (YYYY-MM-DD): ");
        Integer employeeID = input.readInt("Employee ID: ");
        Integer publicationUnitID = input.readInt("Publication Unit ID: ");

        service.enterEmployeePayment(paymentID, amount, claimDate, issueDate, employeeID, publicationUnitID);
        System.out.println("Employee payment inserted.");
    }

    /**
     * Updates the claim date for the employee payment with the given Payment ID.
     * @throws SQLException if a database access error occurs
     */
    private void updateEmployeePayment() throws SQLException {
        Integer paymentID = input.readInt("Payment ID: ");
        Date claimDate = input.readDate("Claim Date (YYYY-MM-DD): ");

        int rows = service.updateEmployeePayment(paymentID, claimDate);
        if (rows > 0) {
            System.out.println("Payment updated.");
        } else {
            System.out.println("Payment not found.");
        }
    }

    /**
     * Displays employee payments that have not been claimed, with an issue date within the given date range.
     * @throws SQLException if a database access error occurs
     */
    private void searchEmployeePayments() throws SQLException {
        Date startDate = input.readDate("Start date (YYYY-MM-DD): ");
        Date endDate = input.readDate("End date (YYYY-MM-DD): ");

        List<ProductionService.PaymentRecord> records = service.searchEmployeePayments(startDate, endDate);

        if (records.isEmpty()) {
            System.out.println("No unclaimed payments found for that time period.");
            return;
        }

        System.out.println("----------------------------------------------------------------");
        System.out.printf("%-10s %-10s %-11s %-11s %-18s%n",
                "PaymentID", "Amount", "IssueDate", "EmployeeID", "PublicationUnitID");
        System.out.println("----------------------------------------------------------------");

        for (ProductionService.PaymentRecord r : records) {
            System.out.printf("%-10d %-10.2f %-11s %-11d %-18d%n",
                    r.getPaymentID(), r.getAmount(), r.getIssueDate(), r.getEmployeeID(), r.getPublicationUnitID());
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    /**
     * Displays the articles found in two issues for comparison.
     * @throws SQLException if a database access error occurs
     */
    private void compareIssues() throws SQLException {
        Integer issue1 = input.readInt("Issue ID 1: ");
        Integer issue2 = input.readInt("Issue ID 2: ");

        List<ProductionService.IssueRecord> records = service.compareIssues(issue1, issue2);

        if (records.isEmpty()) {
            System.out.println("At least one issue not found.");
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-18s %-41s %-24s %-12s %-15s%n",
                "IssueID", "IssueTitle", "PublicationUnitID", "PublicationUnitTitle", "Topic", "WrittenDate", "AuthorName");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");

        for (ProductionService.IssueRecord r : records) {
            System.out.printf("%-8d %-20s %-18d %-41s %-24s %-12s %-15s%n",
                    r.getIssueID(), r.getIssueTitle(), r.getPublicationUnitID(), r.getPublicationUnitTitle(),
                    r.getTopic(), r.getWrittenDate(), r.getAuthorName());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}