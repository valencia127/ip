
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tabby {

    public static String formatList(List<String> items) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            sb.append("     ")
                    .append(i + 1)
                    .append(". ")
                    .append(items.get(i))
                    .append("\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // initialise a scanner object
        String divider = "    ____________________________________________________________";
        List<String> a = new ArrayList<>();

        System.out.println("     What can I do for you?");
        System.out.println(divider + "\n");

        while (scanner.hasNextLine()) {

            String input = scanner.nextLine(); // wait for user input

            if (input.equals("bye")) {
                System.out.println(divider);
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            } else if (input.equals("list")) {
                System.out.println(divider);
                System.out.println(formatList(a)); // Use .print() to avoid an extra trailing newline
                System.out.println(divider);
            } else {
                a.add(input);
                System.out.println(divider);
                System.out.println("     added: " + input);
                System.out.println(divider);
            }
        }

        scanner.close();
    }
}
