import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Entry point for CSC540 Project 3.
 * *
 * Responsibilities:
 * 1. Initialize DB connection
 * 2. Start the top-level menu
 * 3. Ensure resources are closed before exiting
 */
public class Main {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        Scanner scanner = new Scanner(System.in);
        Input input = new Input(scanner);

        try {
            dbManager.connect();

            Connection connection = dbManager.getConnection();
            MenuManager menuManager = new MenuManager(connection, input);
            menuManager.start();

        } catch (ClassNotFoundException e) {
            System.out.println("MariaDB JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error occurred while starting the program.");
            e.printStackTrace();
        } finally {
            dbManager.close();
            scanner.close();
            System.out.println("Program closed.");
        }
    }
}