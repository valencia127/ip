
package ip.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a parsed date, optionally including a time. */
public class ParsedDateTime {

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    /** Creates a parsed date-time value. */
    public ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /** Returns the storage-friendly date or date-time format. */
    public String toFileFormat() {
        return hasTime
                ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"))
                : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    /** Returns the user-facing date or date-time format. */
    public String toString() {
        return hasTime
                ? dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma"))
                : dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }
}
