import java.sql.Date;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.time.LocalDate;

/**
 * A utility class designed to safely read and parse various types of input from the user.
 * It handles parsing exceptions and continuously prompts the user.
 * Empty inputs or the string "null" are treated as a null return value.
 */
public class Input {
    private final Scanner scanner;

    /**
     * Creates an instance with the specified scanner.
     *
     * @param scanner the scanner used to read input from the console
     */
    public Input(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Safely reads a string from the user.
     * Displays the prompt and returns the trimmed input.
     * If the user presses enter without typing anything or enters the word "null", it returns null.
     *
     * @param prompt    the message displayed to the user
     * @return          the trimmed string entered by the user, or null if the input is empty or "null"
     */
    protected String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty() || input.equalsIgnoreCase("null")) {
            return null;
        }
        return input;
    }

    /**
     * Safely reads an integer from the user.
     * Continuously prompts the user until a valid integer is entered.
     * If the input is empty or matches the word "null", it returns null.
     *
     * @param prompt    the message displayed to the user
     * @return          the parsed Integer, or null if the input is empty or "null"
     */
    protected Integer readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty() || input.equalsIgnoreCase("null")) {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    /**
     * Safely reads a double from the user.
     * Continuously prompts the user until a valid double is entered.
     * If the input is empty or matches the word "null", it returns null.
     *
     * @param prompt    the message displayed to the user
     * @return          the parsed Double, or null if the input is empty or "null"
     */
    protected Double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty() || input.equalsIgnoreCase("null")) {
                return null;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number with at most two decimal points.");
            }
        }
    }

    /**
     * Safely reads a date from the user in the format of YYYY-MM-DD.
     * Continuously prompts the user until a valid date string is entered.
     * If the input is empty or matches the word "null", it returns null.
     *
     * @param prompt    the message displayed to the user
     * @return          the parsed Date, or null if the input is empty or "null"
     */
    protected Date readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty() || input.equalsIgnoreCase("null")) {
                return null;
            }
            try {
                LocalDate date = LocalDate.parse(input);
                return java.sql.Date.valueOf(date);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid input. Please enter a valid date (YYYY-MM-DD).");
            }
        }
    }
}
