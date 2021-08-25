package duke.tasks;

import duke.exception.EmptyListException;
import duke.exception.IncorrectFormatException;
import duke.exception.InvalidDateTimeException;
import duke.exception.InvalidDurationException;
import duke.exception.InvalidIndexException;
import duke.exception.MessageEmptyException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;

public class TaskList {

    /**
     * taskList is the list of Tasks
     */

    private final ArrayList<Task> taskList;

    public TaskList() {
        taskList = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    /**
     * Adds a task to the list of Tasks with a confirmation message printed out after.
     * @param task The duke.tasks.Task to be added to the list of Tasks
     */

    public void addToList(Task task) {
        taskList.add(task);

        System.out.println("Got it. I've added this task:");
        System.out.println("Added: " + task);
        String taskGrammar = (taskList.size() == 1) ? " task" : " tasks";
        System.out.println("Now you have " + taskList.size() + taskGrammar + " in the list.");
    }

    /**
     * Prints out the full contents of the list of Tasks.
     * @throws EmptyListException If the list of Tasks is empty and there is nothing to be printed.
     */

    public void displayList() throws EmptyListException {
        if (taskList.size() == 0) {
            throw new EmptyListException();
        }

        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            System.out.println(i + 1 + ". " + task);
        }
    }

    /**
     * Marks a current duke.tasks.Task in the list of Tasks as Done.
     * @param taskIndex The index of the duke.tasks.Task in the list of Tasks to be marked as Done.
     * @throws EmptyListException If the list of Tasks is empty and there is nothing to be marked as Done.
     * @throws InvalidIndexException If the index of the duke.tasks.Task provided is out of range of the current list of Tasks.
     */

    public void markDone(String taskIndex) throws EmptyListException, InvalidIndexException {
        int intTaskIndex = Integer.parseInt(taskIndex) - 1; // -1 because user inputs start from 1 not 0
        int taskListSize = taskList.size();

        if (taskListSize == 0) {
            throw new EmptyListException();
        } else if (intTaskIndex < 0 || intTaskIndex >= taskListSize) {
            throw new InvalidIndexException(1, taskListSize, intTaskIndex + 1);
        }

        Task task = taskList.get(intTaskIndex);
        task.markAsDone();

        System.out.println("Nice! I've marked this task as done:\n" + task);
    }

    /**
     * Adds a duke.tasks.Deadline to the list of Tasks.
     * @param deadline The duke.tasks.Deadline to be added to the list of Tasks which is the whole input barring the command.
     * @throws IncorrectFormatException If the deadline command is used but a "/by" is not present in the message.
     */

    public void addDeadline(String deadline) throws IncorrectFormatException, InvalidDateTimeException, MessageEmptyException {
        String[] result = deadline.split("/by");

        if (result.length == 0) {
            throw new MessageEmptyException();
        } else if (result.length == 1) {
            // throws an error if "/by" is not present in the message
            throw new IncorrectFormatException("deadline", "/by");
        }

        String description = result[0].trim(); // trims the additional spaces to the left and right of "by"
        String by = result[1].trim(); // trims the additional spaces to the left and right of "by"

        LocalDateTime finalBy;

        try {
            // checks if the formats of the input date and time are correct
            finalBy = LocalDateTime.parse(by, DateTimeFormatter.ofPattern("yyyy/MM/dd HHmm"));
        } catch (DateTimeParseException e) {
            throw new InvalidDateTimeException();
        }

        Deadline d = new Deadline(description, finalBy);
        addToList(d);
    }

    /**
     * Adds a duke.tasks.Todo to the list of Tasks.
     * @param todo The duke.tasks.Todo to be added to the list of Tasks.
     */

    public void addTodo(String todo) {
        Todo tempTask = new Todo(todo);
        addToList(tempTask);
    }

    /**
     * Adds an duke.tasks.Event to the list of Tasks.
     * @param event The duke.tasks.Event to be added to the list of Tasks, which is the entire user input barring the command.
     * @throws IncorrectFormatException If the event command is used but a "/at" is not present in the message.
     */

    public void addEvent(String event) throws IncorrectFormatException, MessageEmptyException, InvalidDateTimeException, InvalidDurationException {
        String[] result = event.split("/at");

        if (result.length == 0) {
            throw new MessageEmptyException();
        } else if (result.length == 1) {
            // throws an error if "/at" is not present in the message
            throw new IncorrectFormatException("event", "/at");
        }
        String description = result[0].trim();    // trims the additional spaces to the left and right of "at"
        String at = result[1].trim();             // trims the additional spaces to the left and right of "at"

        // throws error if it doesn't even contain sufficient number of characters for correct format
        if (at.replaceAll("\\s", "").length() < 19) { // YYYY/MM/DD HHMM - HHMM
            throw new InvalidDurationException();
        }

        String date = at.substring(0, 10).trim(); // at this point, date contains 10 chars YYYY/MM/DD
        String eventDuration = at.substring(11).trim();
        String[] eventTimes = eventDuration.split("-");

        // if no "-" present
        if (eventTimes.length != 2) {
            throw new InvalidDurationException();
        }

        String startTime = eventTimes[0].trim();
        String endTime = eventTimes[1].trim();

        LocalDate finalDate;
        LocalTime finalStartTime;
        LocalTime finalEndTime;

        try {
            // checks if the formats of the input date and time are correct
            finalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            finalStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HHmm"));
            finalEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HHmm"));
        } catch (DateTimeParseException e) {
            throw new InvalidDateTimeException();
        }

        Event e = new Event(description, finalDate, finalStartTime, finalEndTime);

        addToList(e);
    }

    /**
     * Deletes a Task from the list of Tasks.
     * @param taskIndex Index of the duke.tasks.Task to be deleted.
     * @throws EmptyListException If the list of Tasks is empty and there is nothing to be deleted.
     * @throws InvalidIndexException If the index of the duke.tasks.Task provided is out of range of the current list of Tasks.
     */

    public void deleteTask(String taskIndex) throws EmptyListException, InvalidIndexException {
        int intTaskIndex = Integer.parseInt(taskIndex) - 1; // -1 because user inputs start from 1 not 0
        int taskListSize = taskList.size();

        if (taskListSize == 0) {
            throw new EmptyListException();
        } else if (intTaskIndex < 0 || intTaskIndex >= taskListSize) {
            throw new InvalidIndexException(1, taskListSize, intTaskIndex + 1);
        }

        Task task = taskList.remove(intTaskIndex);
        System.out.println("Noted. I've removed this task:\n" + task);
        String taskGrammar = (taskList.size() == 1) ? " task" : " tasks";
        System.out.println("Now you have " + taskList.size() + taskGrammar + " in the list.");
    }

    /**
     * Finds all Tasks that matches the query and is case insensitive.
     *
     * @param query case insensitive query to find from Task list.
     * @return ArrayList of Tasks filled with Tasks that match the query.
     */
    public ArrayList<Task> findTask(String query) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        // regex that matches any string to the query and is case insensitive
        String regex = "(?i)(.*)(" + query + ")(.*)";

        for (Task task : taskList) {
            String taskString = task.toString();

            if (taskString.matches(regex)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

}
