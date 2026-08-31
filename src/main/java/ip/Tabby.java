
package ip;

import ip.collection.TaskList;
import ip.exception.TabbyException;
import ip.model.Task;
import ip.parser.Parser;
import ip.storage.Storage;
import ip.ui.Ui;

public class Tabby {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    public Tabby(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TabbyException e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    public void run() {
        ui.showWelcome();

        while (true) {
            String fullCommand = ui.readCommand();
            if (fullCommand.equals("bye")) {
                ui.showBye();
                break;
            }

            try {
                executeCommand(fullCommand);
            } catch (TabbyException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    private void executeCommand(String input) throws TabbyException {
        if (input.equals("list")) {
            ui.showTaskList(tasks);
        } else if (input.startsWith("mark")) {
            int index = Parser.parseTaskIndex(input, tasks.size());
            Task task = tasks.get(index);
            task.markAsDone();
            storage.save(tasks);
            ui.showMarkedDone(task);
        } else if (input.startsWith("unmark")) {
            int index = Parser.parseTaskIndex(input, tasks.size());
            Task task = tasks.get(index);
            task.markAsNotDone();
            storage.save(tasks);
            ui.showMarkedNotDone(task);
        } else if (input.startsWith("delete")) {
            int index = Parser.parseTaskIndex(input, tasks.size());
            Task removedTask = tasks.delete(index);
            storage.save(tasks);
            ui.showTaskDeleted(removedTask, tasks.size());
        } else if (input.startsWith("todo")) {
            Task task = Parser.parseTodo(input);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        } else if (input.startsWith("deadline")) {
            Task task = Parser.parseDeadline(input);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        } else if (input.startsWith("event")) {
            Task task = Parser.parseEvent(input);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        } else {
            throw new TabbyException("I'm sorry, but I don't know what that means :-(");
        }
    }

    public static void main(String[] args) {
        new Tabby("data/tabby.txt").run();
    }
}
