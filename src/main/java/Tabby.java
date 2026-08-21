
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tabby {

    // Represents an individual task with a description and completion state
    public static class Task {

        private String description;
        private boolean isDone;

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

        @Override
        public String toString() {
            String statusIcon = isDone ? "X" : " ";
            return "[" + statusIcon + "] " + description;
        }
    }

    public static String formatList(List<Task> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("     Here are the tasks in your list:\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append("     ")
                    .append(i + 1)
                    .append(".")
                    .append(items.get(i).toString())
                    .append("\n");
        }
        return sb.toString();
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
            } else {
                tasks.add(new Task(input));
                System.out.println(divider);
                System.out.println("     added: " + input);
                System.out.println(divider);
            }
        }

        scanner.close();
    }
}
