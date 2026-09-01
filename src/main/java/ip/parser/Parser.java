
package ip.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ip.exception.TabbyException;
import ip.model.Deadline;
import ip.model.Event;
import ip.model.Todo;

/** Converts user commands and date values into application objects. */
public class Parser {

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

    /** Parses a supported date or date-time string. */
    public static ParsedDateTime parseDateTime(String input) throws TabbyException {
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

    /** Extracts and validates a one-based task number, returning a zero-based index. */
    public static int parseTaskIndex(String input, int taskListSize) throws TabbyException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2) {
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

    /** Parses a todo command and validates its description. */
    public static Todo parseTodo(String input) throws TabbyException {
        String description = input.length() > 4 ? input.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new TabbyException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /** Parses a deadline command and its required due date or time. */
    public static Deadline parseDeadline(String input) throws TabbyException {
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
        return new Deadline(description, by);
    }

    /** Parses an event command and its required start and end values. */
    public static Event parseEvent(String input) throws TabbyException {
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
        return new Event(description, from, to);
    }
}
