package duke.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import duke.exception.EmptyListException;
import duke.exception.IncorrectFormatException;
import duke.exception.InvalidDateTimeException;
import duke.exception.InvalidDurationException;
import duke.exception.InvalidIndexException;
import duke.exception.MessageEmptyException;
import duke.ui.Ui;

/**
 * Represents the list of tasks.
 */
public class TaskList {

    /** The list of Task. */
    private final ArrayList<Task> taskList;

    /**
     * Constructor for TaskList.
     */
    public TaskList() {
        taskList = new ArrayList<>();
    }

    /**
     * Overloaded constructor for TaskList that accepts a task list as an ArrayList.
     *
     * @param taskList list of tasks.
     */
    public TaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    /**
     * Adds a task to the list of Tasks with a confirmation message printed out after.
     * @param task The duke.tasks.Task to be added to the list of Tasks.
     */
    public String addToList(Task task) {
        taskList.add(task);

        String message = "Got it. I've added this task: Added: " + task;
        String taskGrammar = (taskList.size() == 1) ? " task" : " tasks";
        return message + "\nNow you have " + taskList.size() + taskGrammar + " in the list.";
    }

    /**
     * Prints out the full contents of the list of Tasks.
     *
     * @throws EmptyListException if the list of Tasks is empty and there is nothing to be printed.
     */
    public String displayList() throws EmptyListException {
        if (taskList.size() == 0) {
            throw new EmptyListException();
        }
        return Ui.printList(taskList);
    }

    /**
     * Marks a current Task in the list of Tasks as Done.
     *
     * @param taskIndex The index of the duke.tasks.Task in the list of Tasks to be marked as Done.
     * @throws EmptyListException If the list of Tasks is empty and there is nothing to be marked as Done.
     * @throws InvalidIndexException If the index of the Task provided is out of range of the current list of Tasks.
     */
    public String markDone(int taskIndex) throws EmptyListException, InvalidIndexException {
        int taskListSize = taskList.size();

        if (taskListSize == 0) {
            throw new EmptyListException();
        } else if (taskIndex < 0 || taskIndex >= taskListSize) {
            throw new InvalidIndexException(1, taskListSize, taskIndex + 1);
        }

        Task task = taskList.get(taskIndex);
        if (task.isDone()) {
            return task + " has already been marked as done!";
        }
        task.markAsDone();

        return "Nice! I've marked this task as done:\n" + task;
    }

    /**
     * Adds a Deadline to the list of Tasks.
     *
     * @param deadline The Deadline to be added to the list of Tasks which is the whole input barring the command.
     * @throws IncorrectFormatException If the deadline command is used but a "/by" is not present in the message.
     */
    public String addDeadline(String deadline) throws IncorrectFormatException,
            InvalidDateTimeException, MessageEmptyException {

        String[] result = deadline.split("/by");

        if (result.length == 0) {
            throw new MessageEmptyException();
        } else if (result.length == 1) {
            // throws an error if "/by" is not present in the message
            throw new IncorrectFormatException("deadline", "/by");
        } else if (result[0].trim().equals("")) {
            throw new MessageEmptyException();
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
        return addToList(d);
    }

    /**
     * Adds a Todo to the list of Tasks.
     *
     * @param todo Todo to be added to the list of Tasks.
     */
    public String addTodo(String todo) {
        Todo tempTask = new Todo(todo);
        return addToList(tempTask);
    }

    /**
     * Adds an Event to the list of Tasks.
     * @param event The Event to be added to the list of Tasks, which is the entire user input barring the command.
     * @throws IncorrectFormatException If the event command is used but a "/at" is not present in the message.
     */
    public String addEvent(String event) throws IncorrectFormatException, MessageEmptyException,
            InvalidDateTimeException, InvalidDurationException {
        String[] result = event.split("/at");

        if (result.length == 0) {
            throw new MessageEmptyException();
        } else if (result.length == 1) {
            // throws an error if "/at" is not present in the message
            throw new IncorrectFormatException("event", "/at");
        } else if (result[0].trim().equals("")) {
            throw new MessageEmptyException();
        }
        String description = result[0].trim(); // trims the additional spaces to the left and right of "at"
        String at = result[1].trim(); // trims the additional spaces to the left and right of "at"

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

        return addToList(e);
    }

    /**
     * Deletes a Task from the list of Tasks.
     *
     * @param taskIndex Index of the Task to be deleted.
     * @throws EmptyListException If the list of Tasks is empty and there is nothing to be deleted.
     * @throws InvalidIndexException If the index of the Task provided is out of range of the current list of Tasks.
     */
    public String deleteTask(int taskIndex) throws EmptyListException, InvalidIndexException {
        int taskListSize = taskList.size();

        if (taskListSize == 0) {
            throw new EmptyListException();
        } else if (taskIndex < 0 || taskIndex >= taskListSize) {
            throw new InvalidIndexException(1, taskListSize, taskIndex + 1);
        }

        Task task = taskList.remove(taskIndex);

        String message = "Noted. I've removed this task:\n" + task;

        String taskGrammar = (taskList.size() == 1) ? " task" : " tasks";

        return message + "\nNow you have " + taskList.size() + taskGrammar + " in the list.";
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
