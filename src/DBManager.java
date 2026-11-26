import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connection lifecycle.
 * Uses the same MariaDB connection style as Homework 2.
 */
public class DBManager {
    private static final String JDBC_URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    private Connection connection;
    private Statement statement;

    /**
     * Connects to the database.
     * @throws ClassNotFoundException if MariaDB driver not found
     * @throws SQLException if a database access error occurs
     */
    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");

        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        statement = connection.createStatement();

        System.out.println("Connected to database successfully.");
    }

    /**
     * Returns the active database connection.
     * @return the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Not required for prepared statements, but kept for flexibility.
     *
     * @return the shared statement
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Closes statement first, then connection.
     */
    public void close() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Failed to close statement.");
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Failed to close database connection.");
                e.printStackTrace();
            }
        }
    }
}