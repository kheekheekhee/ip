package duke.exception;

/**
 * Exception of Duke that occurs when an incorrect format is given for the Expense command.
 */
public class InvalidExpenseFormatException extends DukeException {

    /**
     * Constructor of InvalidExpenseFormatException.
     */
    public InvalidExpenseFormatException() {
        super("You entered a wrong format for Expense! Please use the format "
                + "(expense) (index) (purpose) $(amount) or "
                + "(expense) (/listall)");
    }
}
