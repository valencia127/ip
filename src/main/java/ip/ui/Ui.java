
package ip.ui;

import java.util.List;
import java.util.Scanner;

import ip.collection.TaskList;
import ip.model.Task;

public class Ui {

    private static final String DIVIDER = "    ____________________________________________________________";
    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "bye";
    }

    public void showWelcome() {
        showLine();
        System.out.println("     What can I do for you?");
        showLine();
        System.out.println();
    }

    public void showLine() {
        System.out.println(DIVIDER);
    }

    public void showError(String message) {
        showLine();
        System.out.println("     OOPS!!! " + message);
        showLine();
    }

    public void showLoadingError() {
        System.out.println("     Warning: Could not load saved tasks. Starting with an empty list.");
    }

    public void showBye() {
        showLine();
        System.out.println("     Bye. Hope to see you again soon!");
        showLine();
    }

    public void showTaskAdded(Task task, int taskCount) {
        showLine();
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    public void showTaskDeleted(Task task, int taskCount) {
        showLine();
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    public void showMarkedDone(Task task) {
        showLine();
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + task);
        showLine();
    }

    public void showMarkedNotDone(Task task) {
        showLine();
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
        showLine();
    }

    public void showTaskList(TaskList tasks) {
        showLine();
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /** Displays tasks whose descriptions match the supplied keyword. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showLine();
        System.out.println("     Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + matchingTasks.get(i));
        }
        showLine();
    }

    public void close() {
        scanner.close();
    }
}
