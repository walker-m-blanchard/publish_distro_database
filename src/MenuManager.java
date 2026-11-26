import java.sql.Connection;

/**
 * Handles the top-level menu for the DBMS project.
 *
 * Current scope:
 * - top-level outer loop
 * - Editing and Publishing module wired in
 * - placeholder handlers for the other 3 major task groups
 */
public class MenuManager {
    private final Connection connection;
    private final Input input;

    public MenuManager(Connection connection, Input input) {
        this.connection = connection;
        this.input = input;
    }

    /**
     * Starts the top-level menu loop.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = input.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    handleEditingAndPublishing();
                    break;
                case 2:
                    handleProduction();
                    break;
                case 3:
                    handleDistribution();
                    break;
                case 4:
                    handleReports();
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }

            if (running) {
                System.out.println();
                System.out.println("Returning to the main menu...");
                System.out.println();
            }
        }
    }

    /**
     * Prints the top-level menu.
     */
    private void printMainMenu() {
        System.out.println("=================================================");
        System.out.println("        GutenbergPubs DBMS - Main Menu");
        System.out.println("=================================================");
        System.out.println("1. Editing and Publishing");
        System.out.println("2. Production of Book Editions or Issues");
        System.out.println("3. Distribution");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
        System.out.println("=================================================");
    }

    /**
     * Launches the Editing and Publishing submenu.
     */
    private void handleEditingAndPublishing() {
        EditingPublishingMenu editingMenu = new EditingPublishingMenu(connection, input);
        editingMenu.start();
    }

    /**
     * Launches the Production module.
     */
    private void handleProduction() {
        ProductionMenu productionMenu = new ProductionMenu(connection, input);
        productionMenu.start();
    }

    /**
     * Launches the Distribution submenu.
     */
    private void handleDistribution() {
        DistributionMenu distributionMenu = new DistributionMenu(connection, input);
        distributionMenu.start();
    }

    /**
     * Launches the Reports module.
     */
    private void handleReports() {
        ReportsMenu reportsMenu = new ReportsMenu(connection, input);
        reportsMenu.start();
    }
}