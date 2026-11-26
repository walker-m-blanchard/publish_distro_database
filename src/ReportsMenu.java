/**
 * This class provides a menu-driven interface for all report-related operations.
 *
 * It interacts with the user through the command-line interface, collects input
 * using the Input class, and uses the ReportsService class to process the input data.
 * The results are then formatted and displayed to the user.
 *
 * @author Morgan Simon
 */

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * This class acts as a sub-menu for all operations related to the generation of reports.
 * *
 * It is interacted with via the command-line interface and prompts the Input object for input and processing, then
 * hands the processed input to the ReportsService object for database interaction.
 */
public class ReportsMenu {
    private final Input input;
    private final ReportsService reportsService;

    /**
     * Initializes the ReportsMenu with a database connection and input handler.
     *
     * @param connection the database connection used by ReportsService
     * @param input the input handler used to read user input
     */
    public ReportsMenu(Connection connection, Input input) {
        this.input = input;
        this.reportsService = new ReportsService(connection);
    }

    /**
     * Starts the reports menu loop and processes user selections.
     * Calls the appropriate report methods based on user input.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = input.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        viewCopiesAndTotalPricePerDistributor();
                        break;
                    case 2:
                        viewCopiesAndTotalPricePerWeek();
                        break;
                    case 3:
                        viewCopiesAndTotalPricePerMonth();
                        break;
                    case 4:
                        viewTotalRevenue();
                        break;
                    case 5:
                        viewTotalExpenses();
                        break;
                    case 6:
                        viewTotalCurrentNumberOfDistributors();
                        break;
                    case 7:
                        viewTotalRevenuePerCity();
                        break;
                    case 8:
                        viewTotalRevenuePerDistributor();
                        break;
                    case 9:
                        viewTotalPaymentsPerMonth();
                        break;
                    case 10:
                        viewTotalPaymentsPerWorkType();
                        break;
                    case 11:
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
     * Displays the list of available report options to the user.
     */
    private void printMenu() {
        System.out.println("===============================================");
        System.out.println("Reports Menu");
        System.out.println("===============================================");
        System.out.println("1  Number and total price of copies per distributor");
        System.out.println("2  Number and total price of copies per week");
        System.out.println("3  Number and total price of copies per month");
        System.out.println("4  Total revenue of the publishing house");
        System.out.println("5  Total expenses (shipping costs and salaries)");
        System.out.println("6  Total current number of distributors");
        System.out.println("7  Total revenue per city");
        System.out.println("8  Total revenue per distributor");
        System.out.println("9  Total payments to editors and authors per month");
        System.out.println("10 Total payments per work type");
        System.out.println("11 Return to Main Menu");
        System.out.println("===============================================");
    }

    /**
     * Displays the number of copies and total price of publications
     * purchased per distributor.
     *
     * Retrieves data from ReportsService and formats the results
     * into a table for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewCopiesAndTotalPricePerDistributor() throws SQLException {
        List<ReportsService.CopiesAndTotalPricePerDistributorRecord> records =
                reportsService.getCopiesAndTotalPricePerDistributor();

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-30s %-20s %-12s %-12s%n",
                "DistID", "Name", "ISBN/IssueID", "TotalCopies", "TotalPrice");
        System.out.println("----------------------------------------------------------------------------------------");

        for (ReportsService.CopiesAndTotalPricePerDistributorRecord r : records) {
            System.out.printf("%-8d %-30s %-20s %-12d %-12.2f%n",
                    r.getDistId(), r.getName(), r.getIsbnIssueId(),
                    r.getTotalCopies(), r.getTotalPrice());
        }

        System.out.println("----------------------------------------------------------------------------------------");
    }

    /**
     * Displays the number of copies and total price of publications
     * within a user-specified date range.
     *
     * Prompts the user for a start and end date, retrieves the data from
     * ReportsService, and formats the results for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewCopiesAndTotalPricePerWeek() throws SQLException {
        Date startDate = input.readDate("Start date (YYYY-MM-DD): ");
        Date endDate = input.readDate("End date (YYYY-MM-DD): ");

        List<ReportsService.CopiesAndTotalPricePerWeekRecord> records =
                reportsService.getCopiesAndTotalPricePerWeek(startDate, endDate);

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-20s %-15s %-15s%n", "ISBN/IssueID", "Copies", "TotalPrice");
        System.out.println("------------------------------------------------------------");

        for (ReportsService.CopiesAndTotalPricePerWeekRecord r : records) {
            System.out.printf("%-20s %-15d %-15.2f%n",
                    r.getIsbnIssueId(), r.getTotalCopies(), r.getTotalPrice());
        }

        System.out.println("------------------------------------------------------------");
    }

    /**
     * Displays the number of copies and total price of publications
     * for a specific month and year.
     *
     * Prompts the user for a year and month, retrieves the data from
     * ReportsService, and formats the results for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewCopiesAndTotalPricePerMonth() throws SQLException {
        Integer year = input.readInt("Year: ");
        Integer month = input.readInt("Month (1-12): ");

        List<ReportsService.CopiesAndTotalPricePerMonthRecord> records =
                reportsService.getCopiesAndTotalPricePerMonth(year, month);

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------");
        System.out.printf("%-20s %-8s %-8s %-12s %-12s%n",
                "ISBN/IssueID", "Year", "Month", "Copies", "TotalPrice");
        System.out.println("------------------------------------------------------------------------");

        for (ReportsService.CopiesAndTotalPricePerMonthRecord r : records) {
            System.out.printf("%-20s %-8d %-8d %-12d %-12.2f%n",
                    r.getIsbnIssueId(), r.getYear(), r.getMonth(),
                    r.getTotalCopies(), r.getTotalPrice());
        }

        System.out.println("------------------------------------------------------------------------");
    }

    /**
     * Displays the total revenue of the publishing house.
     *
     * Retrieves the total revenue from ReportsService and outputs
     * the result to the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalRevenue() throws SQLException {
        double totalRevenue = reportsService.getTotalRevenue();

        System.out.println("-------------------------");
        System.out.println("TotalRevenue");
        System.out.println("-------------------------");
        System.out.printf("%.2f%n", totalRevenue);
        System.out.println("-------------------------");
        System.out.println();
    }

    /**
     * Displays the total expenses of the publishing house.
     *
     * Retrieves total shipping costs, salaries, and overall expenses
     * from ReportsService and outputs the results to the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalExpenses() throws SQLException {
        ReportsService.TotalExpensesRecord r = reportsService.getTotalExpenses();

        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-20s %-15s %-15s%n",
                "TotalShippingCost", "TotalSalaries", "TotalExpenses");
        System.out.println("---------------------------------------------------------------");

        System.out.printf("%-20.2f %-15.2f %-15.2f%n",
                r.getTotalShippingCost(),
                r.getTotalSalaries(),
                r.getTotalExpenses());

        System.out.println("---------------------------------------------------------------");
        System.out.println();
    }

    /**
     * Displays the total number of distributors currently in the system.
     *
     * Retrieves the count from ReportsService and outputs the result
     * to the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalCurrentNumberOfDistributors() throws SQLException {
        int count = reportsService.getTotalCurrentNumberOfDistributors();

        System.out.println("--------------------------------");
        System.out.printf("%-25s%n", "TotalDistributors");
        System.out.println("--------------------------------");

        System.out.printf("%-25d%n", count);

        System.out.println("--------------------------------");
        System.out.println();
    }

    /**
     * Displays the total revenue grouped by distributor city.
     *
     * Retrieves the revenue data from ReportsService and formats
     * the results for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalRevenuePerCity() throws SQLException {
        List<ReportsService.TotalRevenuePerCityRecord> records =
                reportsService.getTotalRevenuePerCity();

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("---------------------------------------------");
        System.out.printf("%-20s %-15s%n", "City", "TotalRevenue");
        System.out.println("---------------------------------------------");

        for (ReportsService.TotalRevenuePerCityRecord r : records) {
            System.out.printf("%-20s %-15.2f%n",
                    r.getCity(), r.getTotalRevenue());
        }

        System.out.println("---------------------------------------------");
    }

    /**
     * Displays the total revenue grouped by distributor.
     *
     * Retrieves the revenue data from ReportsService and formats
     * the results for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalRevenuePerDistributor() throws SQLException {
        List<ReportsService.TotalRevenuePerDistributorRecord> records =
                reportsService.getTotalRevenuePerDistributor();

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("--------------------------------------------------------");
        System.out.printf("%-8s %-30s %-15s%n", "DistID", "Name", "TotalRevenue");
        System.out.println("--------------------------------------------------------");

        for (ReportsService.TotalRevenuePerDistributorRecord r : records) {
            System.out.printf("%-8d %-30s %-15.2f%n",
                    r.getDistId(), r.getName(), r.getTotalRevenue());
        }

        System.out.println("--------------------------------------------------------");
    }

    /**
     * Displays the total payments per month for a given year.
     *
     * Prompts the user for a year, retrieves the payment data from
     * ReportsService, and formats the results for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalPaymentsPerMonth() throws SQLException {
        Integer year = input.readInt("Year: ");

        List<ReportsService.TotalPaymentsPerMonthRecord> records =
                reportsService.getTotalPaymentsPerMonth(year);

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("---------------------------------------------");
        System.out.printf("%-8s %-8s %-15s%n", "Year", "Month", "TotalPayments");
        System.out.println("---------------------------------------------");

        for (ReportsService.TotalPaymentsPerMonthRecord r : records) {
            System.out.printf("%-8d %-8d %-15.2f%n",
                    r.getYear(), r.getMonth(), r.getTotalPayments());
        }

        System.out.println("---------------------------------------------");
    }


    /**
     * Displays the total payments grouped by work type.
     *
     * Retrieves the payment data from ReportsService, where payments are
     * categorized by work type, and formats the results for display in the console.
     *
     * @throws SQLException if a database error occurs while retrieving the data
     */
    private void viewTotalPaymentsPerWorkType() throws SQLException {
        List<ReportsService.TotalPaymentsPerWorkTypeRecord> records =
                reportsService.getTotalPaymentsPerWorkType();

        if (records.isEmpty()) {
            System.out.println("No report records found.");
            return;
        }

        System.out.println("---------------------------------------------");
        System.out.printf("%-30s %-15s%n", "WorkType", "TotalPayments");
        System.out.println("---------------------------------------------");

        for (ReportsService.TotalPaymentsPerWorkTypeRecord r : records) {
            System.out.printf("%-30s %-15.2f%n",
                    r.getWorkType(), r.getTotalPayments());
        }

        System.out.println("---------------------------------------------");
    }
}