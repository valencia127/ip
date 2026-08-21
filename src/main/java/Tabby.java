
import java.util.Scanner;

public class Tabby {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // initialise a scanner object
        String divider = "    ____________________________________________________________";

        System.out.println("     What can I do for you?");
        System.out.println(divider + "\n");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine(); // wait for user input

            System.out.println(divider);

            // Compare strings in Java using .equals() instead of == or strcmp
            if (input.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(divider);
                break; // Exit the loop
            }

            // Echo input back
            System.out.println("     " + input);
            System.out.println(divider + "\n");
        }

        scanner.close();
    }
}
