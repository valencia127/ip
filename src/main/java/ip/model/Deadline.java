
package ip.model;

import ip.parser.ParsedDateTime;

public class Deadline extends Task {

    protected ParsedDateTime by;

    public Deadline(String description, ParsedDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.toFileFormat();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
