
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tabby {

    // Custom exception class for chatbot-specific errors
    public static class TabbyException extends Exception {

        public TabbyException(String message) {
            super(message);
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
            return (isDone ? "X" : " ");
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
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    public static class Deadline extends Task {

        protected String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }

    public static class Event extends Task {

        protected String from;
        protected String to;

        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    public static String formatList(List<Task> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("     Here are the tasks in your list:\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append("     ")
                    .append(i + 1)
                    .append(".")
                    .append(items.get(i))
                    .append("\n");
        }
        return sb.toString();
    }

    private static void printTaskAdded(String divider, Task task, int taskCount) {
        System.out.println(divider);
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        System.out.println(divider);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String divider = "    ____________________________________________________________";
        List<Task> tasks = new ArrayList<>();

        System.out.println("     What can I do for you?");
        System.out.println(divider + "\n");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    System.out.println(divider);
                    System.out.println("     Bye. Hope to see you again soon!");
                    System.out.println(divider);
                    break;
                } else if (input.equals("list")) {
                    System.out.println(divider);
                    System.out.print(formatList(tasks));
                    System.out.println(divider);
                } else if (input.startsWith("mark")) {
                    String[] parts = input.split(" ");
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new TabbyException("Please specify a task number to mark.");
                    }

                    int taskIndex;
                    try {
                        taskIndex = Integer.parseInt(parts[1]) - 1;
                    } catch (NumberFormatException e) {
                        throw new TabbyException("Please enter a valid task number.");
                    }

                    if (taskIndex < 0 || taskIndex >= tasks.size()) {
                        throw new TabbyException("Task number out of range.");
                    }

                    Task task = tasks.get(taskIndex);
                    task.markAsDone();

                    System.out.println(divider);
                    System.out.println("     Nice! I've marked this task as done:");
                    System.out.println("       " + task);
                    System.out.println(divider);
                } else if (input.startsWith("unmark")) {
                    String[] parts = input.split(" ");
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new TabbyException("Please specify a task number to unmark.");
                    }

                    int taskIndex;
                    try {
                        taskIndex = Integer.parseInt(parts[1]) - 1;
                    } catch (NumberFormatException e) {
                        throw new TabbyException("Please enter a valid task number.");
                    }

                    if (taskIndex < 0 || taskIndex >= tasks.size()) {
                        throw new TabbyException("Task number out of range.");
                    }

                    Task task = tasks.get(taskIndex);
                    task.markAsNotDone();

                    System.out.println(divider);
                    System.out.println("     OK, I've marked this task as not done yet:");
                    System.out.println("       " + task);
                    System.out.println(divider);
                } else if (input.startsWith("todo")) {
                    String description = input.length() > 4 ? input.substring(4).trim() : "";
                    if (description.isEmpty()) {
                        throw new TabbyException("The description of a todo cannot be empty.");
                    }

                    Task task = new Todo(description);
                    tasks.add(task);
                    printTaskAdded(divider, task, tasks.size());
                } else if (input.startsWith("deadline")) {
                    String body = input.length() > 8 ? input.substring(8).trim() : "";
                    if (body.isEmpty()) {
                        throw new TabbyException("The description of a deadline cannot be empty.");
                    }
                    if (!body.contains(" /by ")) {
                        throw new TabbyException("A deadline must include '/by' followed by a time.");
                    }

                    String[] parts = body.split(" /by ", 2);
                    if (parts[0].trim().isEmpty()) {
                        throw new TabbyException("The description of a deadline cannot be empty.");
                    }
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new TabbyException("The '/by' time of a deadline cannot be empty.");
                    }

                    Task task = new Deadline(parts[0].trim(), parts[1].trim());
                    tasks.add(task);
                    printTaskAdded(divider, task, tasks.size());
                } else if (input.startsWith("event")) {
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
                    if (timeParts[0].trim().isEmpty() || timeParts.length < 2 || timeParts[1].trim().isEmpty()) {
                        throw new TabbyException("The '/from' and '/to' times of an event cannot be empty.");
                    }

                    Task task = new Event(description, timeParts[0].trim(), timeParts[1].trim());
                    tasks.add(task);
                    printTaskAdded(divider, task, tasks.size());
                } else {
                    throw new TabbyException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (TabbyException e) {
                System.out.println(divider);
                System.out.println("     Oh No!!! " + e.getMessage());
                System.out.println(divider);
            }
        }

        scanner.close();
    }
}
