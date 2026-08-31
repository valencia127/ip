
package ip.model;

import ip.parser.ParsedDateTime;

public class Event extends Task {

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
