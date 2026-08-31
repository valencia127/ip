
package ip.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParsedDateTime {

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
