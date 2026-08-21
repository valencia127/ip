
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tabby {

    // Base Task class
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

    // Subclass for ToDos
    public static class Todo extends Task {

        public Todo(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    // Subclass for Deadlines
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

    // Subclass for Events
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
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println(divider);
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            } else if (input.equals("list")) {
                System.out.println(divider);
                System.out.print(formatList(tasks));
                System.out.println(divider);
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
                Task task = tasks.get(taskIndex);
                task.markAsDone();

                System.out.println(divider);
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       " + task);
                System.out.println(divider);
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
                Task task = tasks.get(taskIndex);
                task.markAsNotDone();

                System.out.println(divider);
                System.out.println("     OK, I've marked this task as not done yet:");
                System.out.println("       " + task);
                System.out.println(divider);
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                Task task = new Todo(description);
                tasks.add(task);

                printTaskAdded(divider, task, tasks.size());
            } else if (input.startsWith("deadline ")) {
                String[] parts = input.substring(9).split(" /by ");
                Task task = new Deadline(parts[0], parts[1]);
                tasks.add(task);

                printTaskAdded(divider, task, tasks.size());
            } else if (input.startsWith("event ")) {
                String[] parts = input.substring(6).split(" /from ");
                String description = parts[0];
                String[] timeParts = parts[1].split(" /to ");
                String from = timeParts[0];
                String to = timeParts[1];

                Task task = new Event(description, from, to);
                tasks.add(task);

                printTaskAdded(divider, task, tasks.size());
            }
        }

        scanner.close();
    }
}
