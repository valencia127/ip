
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static final String FILE_DELIMITER = " \\| ";
    private final Path filePath;

    public Storage(String filePathStr) {
        this.filePath = Paths.get(filePathStr);
    }

    public List<Task> load() throws TabbyException {
        List<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                Task task = parseTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new TabbyException("Could not load saved tasks.");
        }
        return loadedTasks;
    }

    public void save(TaskList tasks) throws TabbyException {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                lines.add(tasks.get(i).toFileFormat());
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new TabbyException("Unable to save tasks to file.");
        }
    }

    private Task parseTask(String line) {
        String[] parts = line.split(FILE_DELIMITER);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = null;
        try {
            switch (type) {
                case "T" ->
                    task = new Todo(description);
                case "D" -> {
                    if (parts.length >= 4) {
                        ParsedDateTime by = Parser.parseDateTime(parts[3]);
                        task = new Deadline(description, by);
                    }
                }
                case "E" -> {
                    if (parts.length >= 5) {
                        ParsedDateTime from = Parser.parseDateTime(parts[3]);
                        ParsedDateTime to = Parser.parseDateTime(parts[4]);
                        task = new Event(description, from, to);
                    }
                }
                default -> {
                }
            }
        } catch (TabbyException e) {
            return null;
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }
}
