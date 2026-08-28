
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Tabby task manager.
 */
public class Tabby {

    private static final String DIVIDER = "    ____________________________________________________________";
    private static final String FILE_DELIMITER = " \\| ";
    private static final Path FILE_PATH = Paths.get("data", "tabby.txt");

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[]{
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
        DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("d/M/yyyy")
    };

    /**
     * Custom exception class for chatbot-specific errors.
     */
    public static class TabbyException extends Exception {

        public TabbyException(String message) {
            super(message);
        }
    }

    /**
     * Helper class to wrap date and time representation and formatting.
     */
    public static class ParsedDateTime {

        private final LocalDateTime dateTime;
        private final boolean hasTime;

        public ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }

        public String toFileFormat() {
            return hasTime
                    ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"))
                    : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        @Override
        public String toString() {
            return hasTime
                    ? dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma"))
                    : dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        }
    }

    public static class Task {

        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public void markAsDone() {
            this.isDone = true;
        }

        public void markAsNotDone() {
            this.isDone = false;
        }

        public String getStatusIcon() {
            return isDone ? "X" : " ";
        }

        public String toFileFormat() {
            return (isDone ? "1" : "0") + " | " + description;
        }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    public static class Todo extends Task {

        public Todo(String description) {
            super(description);
        }

        @Override
        public String toFileFormat() {
            return "T | " + super.toFileFormat();
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    public static class Deadline extends Task {

        protected ParsedDateTime by;

        public Deadline(String description, ParsedDateTime by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toFileFormat() {
            return "D | " + super.toFileFormat() + " | " + by.toFileFormat();
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }

    public static class Event extends Task {

        protected ParsedDateTime from;
        protected ParsedDateTime to;

        public Event(String description, ParsedDateTime from, ParsedDateTime to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toFileFormat() {
            return "E | " + super.toFileFormat() + " | " + from.toFileFormat() + " | " + to.toFileFormat();
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = loadTasks();

        System.out.println("     What can I do for you?");
        System.out.println(DIVIDER + "\n");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("bye")) {
                System.out.println(DIVIDER);
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            try {
                processCommand(input, tasks);
            } catch (TabbyException exception) {
                System.out.println(DIVIDER);
                System.out.println("     OOPS!!! " + exception.getMessage());
                System.out.println(DIVIDER);
            }
        }

        scanner.close();
    }

    private static void processCommand(String input, List<Task> tasks) throws TabbyException {
        if (input.equals("list")) {
            printList(tasks);
        } else if (input.startsWith("mark")) {
            handleMark(input, tasks);
        } else if (input.startsWith("unmark")) {
            handleUnmark(input, tasks);
        } else if (input.startsWith("delete")) {
            handleDelete(input, tasks);
        } else if (input.startsWith("todo")) {
            handleTodo(input, tasks);
        } else if (input.startsWith("deadline")) {
            handleDeadline(input, tasks);
        } else if (input.startsWith("event")) {
            handleEvent(input, tasks);
        } else {
            throw new TabbyException("I'm sorry, but I don't know what that means :-(");
        }
    }

    private static ParsedDateTime parseDateTime(String input) throws TabbyException {
        String trimmed = input.trim();

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(trimmed, formatter);
                return new ParsedDateTime(ldt, true);
            } catch (DateTimeParseException ignored) {
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate ld = LocalDate.parse(trimmed, formatter);
                return new ParsedDateTime(ld.atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
            }
        }

        try {
            LocalDateTime ldt = LocalDateTime.parse(trimmed);
            return new ParsedDateTime(ldt, true);
        } catch (DateTimeParseException ignored) {
        }

        try {
            LocalDate ld = LocalDate.parse(trimmed);
            return new ParsedDateTime(ld.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
        }

        throw new TabbyException("Invalid date format. Use 'yyyy-MM-dd', 'd/M/yyyy HHmm', or 'yyyy-MM-dd HHmm'.");
    }

    private static void printList(List<Task> tasks) {
        StringBuilder builder = new StringBuilder();
        builder.append("     Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            builder.append("     ")
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i))
                    .append("\n");
        }
        System.out.println(DIVIDER);
        System.out.print(builder.toString());
        System.out.println(DIVIDER);
    }

    private static int parseTaskIndex(String input, int taskListSize) throws TabbyException {
        String[] parts = input.split(" ");
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new TabbyException("Please specify a task number.");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new TabbyException("Please enter a valid task number.");
        }

        if (taskIndex < 0 || taskIndex >= taskListSize) {
            throw new TabbyException("Task number out of range.");
        }

        return taskIndex;
    }

    private static void handleMark(String input, List<Task> tasks) throws TabbyException {
        int taskIndex = parseTaskIndex(input, tasks.size());
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        saveTasks(tasks);

        System.out.println(DIVIDER);
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + task);
        System.out.println(DIVIDER);
    }

    private static void handleUnmark(String input, List<Task> tasks) throws TabbyException {
        int taskIndex = parseTaskIndex(input, tasks.size());
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        saveTasks(tasks);

        System.out.println(DIVIDER);
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
        System.out.println(DIVIDER);
    }

    private static void handleDelete(String input, List<Task> tasks) throws TabbyException {
        int taskIndex = parseTaskIndex(input, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        saveTasks(tasks);

        System.out.println(DIVIDER);
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + removedTask);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    private static void handleTodo(String input, List<Task> tasks) throws TabbyException {
        String description = input.length() > 4 ? input.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new TabbyException("The description of a todo cannot be empty.");
        }

        Task task = new Todo(description);
        tasks.add(task);
        saveTasks(tasks);
        printTaskAdded(task, tasks.size());
    }

    private static void handleDeadline(String input, List<Task> tasks) throws TabbyException {
        String body = input.length() > 8 ? input.substring(8).trim() : "";
        if (body.isEmpty()) {
            throw new TabbyException("The description of a deadline cannot be empty.");
        }
        if (!body.contains(" /by ")) {
            throw new TabbyException("A deadline must include '/by' followed by a time.");
        }

        String[] parts = body.split(" /by ", 2);
        String description = parts[0].trim();
        String byTime = parts.length > 1 ? parts[1].trim() : "";

        if (description.isEmpty()) {
            throw new TabbyException("The description of a deadline cannot be empty.");
        }
        if (byTime.isEmpty()) {
            throw new TabbyException("The '/by' time of a deadline cannot be empty.");
        }

        ParsedDateTime by = parseDateTime(byTime);
        Task task = new Deadline(description, by);
        tasks.add(task);
        saveTasks(tasks);
        printTaskAdded(task, tasks.size());
    }

    private static void handleEvent(String input, List<Task> tasks) throws TabbyException {
        String body = input.length() > 5 ? input.substring(5).trim() : "";
        if (body.isEmpty()) {
            throw new TabbyException("The description of an event cannot be empty.");
        }
        if (!body.contains(" /from ") || !body.contains(" /to ")) {
            throw new TabbyException("An event must include both '/from' and '/to' times.");
        }

        String[] parts = body.split(" /from ", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new TabbyException("The description of an event cannot be empty.");
        }

        String[] timeParts = parts[1].split(" /to ", 2);
        String fromTime = timeParts[0].trim();
        String toTime = timeParts.length > 1 ? timeParts[1].trim() : "";

        if (fromTime.isEmpty() || toTime.isEmpty()) {
            throw new TabbyException("The '/from' and '/to' times of an event cannot be empty.");
        }

        ParsedDateTime from = parseDateTime(fromTime);
        ParsedDateTime to = parseDateTime(toTime);
        Task task = new Event(description, from, to);
        tasks.add(task);
        saveTasks(tasks);
        printTaskAdded(task, tasks.size());
    }

    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    private static List<Task> loadTasks() {
        List<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (String line : lines) {
                Task task = parseTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException ioException) {
            System.out.println("     Warning: Could not load saved tasks.");
        }
        return loadedTasks;
    }

    private static void saveTasks(List<Task> tasks) {
        try {
            if (FILE_PATH.getParent() != null) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileFormat());
            }
            Files.write(FILE_PATH, lines);
        } catch (IOException ioException) {
            System.out.println("     Error: Unable to save tasks to file.");
        }
    }

    private static Task parseTask(String line) {
        String[] parts = line.split(FILE_DELIMITER);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = null;
        try {
            switch (type) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    if (parts.length >= 4) {
                        ParsedDateTime by = parseDateTime(parts[3]);
                        task = new Deadline(description, by);
                    }
                    break;
                case "E":
                    if (parts.length >= 5) {
                        ParsedDateTime from = parseDateTime(parts[3]);
                        ParsedDateTime to = parseDateTime(parts[4]);
                        task = new Event(description, from, to);
                    }
                    break;
                default:
                    break;
            }
        } catch (TabbyException e) {
            return null;
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }
}
