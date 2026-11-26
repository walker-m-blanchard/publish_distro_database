import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Console menu for the Editing and Publishing task group.
 *
 * Responsibilities:
 * 1. Display the Editing and Publishing sub-menu and read user selections
 * 2. Collect input fields required by each operation and forward them to EditingPublishingService
 * 3. Format and print query results (publications by editor) to standard output
 * 4. Handle navigation back to the main menu
 */
public class EditingPublishingMenu {

    private final Input input;
    private final EditingPublishingService service;

    /**
     * Constructs an EditingPublishingMenu with the given database connection and input handler.
     *
     * @param connection the active database connection passed to EditingPublishingService.
     * @param input      the input handler used to read user input from the console.
     */
    public EditingPublishingMenu(Connection connection, Input input) {
        this.input = input;
        this.service = new EditingPublishingService(connection);
    }

    /**
     * Starts the Editing and Publishing menu loop, repeatedly prompting the user until they choose to exit.
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
                        enterNewPublication();
                        break;

                    case 2:
                        updatePublicationInformation();
                        break;

                    case 3:
                        assignEditor();
                        break;

                    case 4:
                        removeEditor();
                        break;

                    case 5:
                        viewPublicationsByEditor();
                        break;

                    case 6:
                        addArticleToIssue();
                        break;

                    case 7:
                        deleteArticleFromIssue();
                        break;

                    case 8:
                        addChapterToEdition();
                        break;

                    case 9:
                        deleteChapterFromEdition();
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
     * Prints the Editing and Publishing sub-menu options to standard output.
     */
    private void printMenu() {
        System.out.println("===============================================");
        System.out.println("Editing and Publishing");
        System.out.println("===============================================");
        System.out.println("1  Enter new publication");
        System.out.println("2  Update publication information");
        System.out.println("3  Assign editor to publication");
        System.out.println("4  Remove editor from publication");
        System.out.println("5  View publications by editor");
        System.out.println("6  Add article to issue");
        System.out.println("7  Delete article from issue");
        System.out.println("8  Add chapter to edition");
        System.out.println("9  Delete chapter from edition");
        System.out.println("10 Back to main menu");
        System.out.println("===============================================");
    }

    /**
     * Prompts the user for publication details and delegates insertion to the service.
     * Routes to either the book or periodical creation path based on the entered type.
     *
     * @throws SQLException if the database insert fails.
     */
    private void enterNewPublication() throws SQLException {
        Integer pubId = input.readInt("PubID: ");
        String title = input.readString("Title: ");
        String type = input.readString("Type (Book / Magazine / Journal): ");
        String topic = input.readString("Topic: ");

        if (type.equalsIgnoreCase("Book")) {
            service.enterNewBookPublication(pubId, title, topic);
        } else {
            String periodicity = input.readString("Periodicity: ");
            service.enterNewPeriodicalPublication(pubId, title, type, topic, periodicity);
        }

        System.out.println("Publication inserted.");
    }

    /**
     * Prompts the user for a publication ID and a new title, then delegates the update to the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database update fails.
     */
    private void updatePublicationInformation() throws SQLException {
        Integer pubId = input.readInt("PubID: ");
        String newTitle = input.readString("New title: ");

        int rows = service.updatePublicationInformation(pubId, newTitle);

        if (rows > 0) {
            System.out.println("Publication updated.");
        } else {
            System.out.println("Publication not found.");
        }
    }

    /**
     * Prompts the user for an editor's employee ID and a publication ID, then assigns the editor via the service.
     * Prints a confirmation or a failure message based on the number of rows affected.
     *
     * @throws SQLException if the database insert fails.
     */
    private void assignEditor() throws SQLException {
        Integer employeeId = input.readInt("Editor EmployeeID: ");
        Integer pubId = input.readInt("Publication PubID: ");

        int rows = service.assignEditorToPublication(employeeId, pubId);

        if (rows > 0) {
            System.out.println("Editor assigned.");
        } else {
            System.out.println("Assignment failed.");
        }
    }

    /**
     * Prompts the user for an editor's employee ID and a publication ID, then removes the assignment via the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database delete fails.
     */
    private void removeEditor() throws SQLException {
        Integer employeeId = input.readInt("Editor EmployeeID: ");
        Integer pubId = input.readInt("Publication PubID: ");

        int rows = service.removeEditorFromPublication(employeeId, pubId);

        if (rows > 0) {
            System.out.println("Editor removed.");
        } else {
            System.out.println("No matching editor-publication assignment found.");
        }
    }

    /**
     * Prompts the user for an editor's employee ID, then retrieves and displays all publications assigned to that editor.
     * Prints a formatted table or a message if no publications are found.
     *
     * @throws SQLException if the database query fails.
     */
    private void viewPublicationsByEditor() throws SQLException {
        Integer employeeId = input.readInt("Editor EmployeeID: ");

        List<EditingPublishingService.PublicationRecord> publications =
                service.getPublicationsByEditor(employeeId);

        if (publications.isEmpty()) {
            System.out.println("No publications found for that editor.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-10s %-37s %-15s %-20s%n", "PubID", "PubTitle", "PubType", "Topic");
        System.out.println("----------------------------------------------------------------------------");

        for (EditingPublishingService.PublicationRecord publication : publications) {
            System.out.printf(
                    "%-10d %-37s %-15s %-20s%n",
                    publication.getPubId(),
                    safeString(publication.getPubTitle()),
                    safeString(publication.getPubType()),
                    safeString(publication.getTopic())
            );
        }

        System.out.println("----------------------------------------------------------------------------");
    }

    /**
     * Prompts the user for an article ID and an issue ID, then adds the article to the issue via the service.
     * Prints a confirmation or a failure message based on the number of rows affected.
     *
     * @throws SQLException if the database insert fails.
     */
    private void addArticleToIssue() throws SQLException {
        Integer articleId = input.readInt("PublicationUnitID: ");
        Integer issueId = input.readInt("IssueID: ");

        int rows = service.addArticleToIssue(articleId, issueId);

        if (rows > 0) {
            System.out.println("Article added to issue.");
        } else {
            System.out.println("Insert failed.");
        }
    }

    /**
     * Prompts the user for an article ID and an issue ID, then removes the article from the issue via the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database delete fails.
     */
    private void deleteArticleFromIssue() throws SQLException {
        Integer articleId = input.readInt("PublicationUnitID: ");
        Integer issueId = input.readInt("IssueID: ");

        int rows = service.deleteArticleFromIssue(articleId, issueId);

        if (rows > 0) {
            System.out.println("Article removed from issue.");
        } else {
            System.out.println("No matching article-issue relationship found.");
        }
    }

    /**
     * Prompts the user for a chapter ID and an ISBN, then adds the chapter to the book edition via the service.
     * Prints a confirmation or a failure message based on the number of rows affected.
     *
     * @throws SQLException if the database insert fails.
     */
    private void addChapterToEdition() throws SQLException {
        Integer chapterId = input.readInt("PublicationUnitID: ");
        String isbn = input.readString("ISBN: ");

        int rows = service.addChapterToEdition(chapterId, isbn);

        if (rows > 0) {
            System.out.println("Chapter added.");
        } else {
            System.out.println("Insert failed.");
        }
    }

    /**
     * Prompts the user for a chapter ID and an ISBN, then removes the chapter from the book edition via the service.
     * Prints a confirmation or a not-found message based on the number of rows affected.
     *
     * @throws SQLException if the database delete fails.
     */
    private void deleteChapterFromEdition() throws SQLException {
        Integer chapterId = input.readInt("PublicationUnitID: ");
        String isbn = input.readString("ISBN: ");

        int rows = service.deleteChapterFromEdition(chapterId, isbn);

        if (rows > 0) {
            System.out.println("Chapter removed.");
        } else {
            System.out.println("No matching chapter-edition relationship found.");
        }
    }

    /**
     * Returns the given string, or the literal "NULL" if the value is null.
     * Used to safely format nullable fields in tabular output.
     *
     * @param value the string to check.
     * @return the original string, or "NULL" if it is null.
     */
    private String safeString(String value) {
        return value == null ? "NULL" : value;
    }
}
