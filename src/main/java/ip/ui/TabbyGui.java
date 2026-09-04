package ip.ui;

import java.util.List;

import ip.collection.TaskList;
import ip.exception.TabbyException;
import ip.model.Task;
import ip.parser.Parser;
import ip.storage.Storage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides a JavaFX graphical interface for the Tabby task manager. */
public class TabbyGui extends Application {

    private final Storage storage = new Storage("data/tabby.txt");
    private final TaskList tasks = new TaskList();
    private final ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    private final ListView<Task> taskListView = new ListView<>(displayedTasks);
    private final TextField commandField = new TextField();
    private final Label statusLabel = new Label("Welcome to Tabby!");

    @Override
    public void start(Stage stage) {
        loadTasks();
        Label title = new Label("Tabby Task Manager");
        title.getStyleClass().add("title");
        commandField.setPromptText("todo read lecture notes");
        commandField.setOnAction(event -> executeCommand());

        Button runButton = new Button("Run command");
        runButton.setOnAction(event -> executeCommand());
        Button listButton = new Button("List");
        listButton.setOnAction(event -> refresh(tasks));
        Button markButton = new Button("Mark done");
        markButton.setOnAction(event -> changeSelectedTask(true));
        Button unmarkButton = new Button("Unmark");
        unmarkButton.setOnAction(event -> changeSelectedTask(false));
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> deleteSelectedTask());

        HBox commandBar = new HBox(8, commandField, runButton);
        HBox actions = new HBox(8, listButton, markButton, unmarkButton, deleteButton);
        VBox top = new VBox(10, title, commandBar, actions);
        top.setPadding(new Insets(16));

        taskListView.setPlaceholder(new Label("No tasks yet. Add one above."));
        taskListView.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setText(empty || task == null ? null : (getIndex() + 1) + ". " + task);
            }
        });

        BorderPane root = new BorderPane(taskListView);
        root.setTop(top);
        root.setBottom(statusLabel);
        BorderPane.setMargin(taskListView, new Insets(0, 16, 16, 16));
        BorderPane.setMargin(statusLabel, new Insets(0, 16, 16, 16));
        Scene scene = new Scene(root, 760, 520);
        scene.getStylesheets().add(getClass().getResource("/tabby.css").toExternalForm());
        stage.setTitle("Tabby");
        stage.setScene(scene);
        stage.show();
    }

    private void loadTasks() {
        try {
            List<Task> loadedTasks = storage.load();
            loadedTasks.forEach(tasks::add);
            refresh(tasks);
        } catch (TabbyException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void executeCommand() {
        String input = commandField.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText("Please enter a command.");
            return;
        }
        try {
            if (input.equals("list")) {
                refresh(tasks);
            } else if (input.startsWith("find")) {
                String keyword = input.substring(4).trim();
                if (keyword.isEmpty()) {
                    throw new TabbyException("Please specify a keyword to find.");
                }
                refresh(new TaskList(tasks.find(keyword)));
            } else if (input.startsWith("mark")) {
                updateTask(input, true);
            } else if (input.startsWith("unmark")) {
                updateTask(input, false);
            } else if (input.startsWith("delete")) {
                int index = Parser.parseTaskIndex(input, tasks.size());
                tasks.delete(index);
                saveAndRefresh("Task deleted.");
            } else if (input.startsWith("todo")) {
                tasks.add(Parser.parseTodo(input));
                saveAndRefresh("Todo added.");
            } else if (input.startsWith("deadline")) {
                tasks.add(Parser.parseDeadline(input));
                saveAndRefresh("Deadline added.");
            } else if (input.startsWith("event")) {
                tasks.add(Parser.parseEvent(input));
                saveAndRefresh("Event added.");
            } else if (input.equals("bye")) {
                javafx.application.Platform.exit();
            } else {
                throw new TabbyException("I'm sorry, but I don't know what that means.");
            }
        } catch (TabbyException exception) {
            statusLabel.setText(exception.getMessage());
        }
        commandField.clear();
    }

    private void updateTask(String input, boolean markDone) throws TabbyException {
        int index = Parser.parseTaskIndex(input, tasks.size());
        Task task = tasks.get(index);
        if (markDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        saveAndRefresh(markDone ? "Task marked as done." : "Task marked as not done.");
    }

    private void changeSelectedTask(boolean markDone) {
        int index = taskListView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= tasks.size()) {
            statusLabel.setText("Select a task first.");
            return;
        }
        try {
            Task task = tasks.get(index);
            if (markDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            saveAndRefresh(markDone ? "Task marked as done." : "Task marked as not done.");
        } catch (TabbyException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void deleteSelectedTask() {
        int index = taskListView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= tasks.size()) {
            statusLabel.setText("Select a task first.");
            return;
        }
        try {
            tasks.delete(index);
            saveAndRefresh("Task deleted.");
        } catch (TabbyException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void saveAndRefresh(String message) throws TabbyException {
        storage.save(tasks);
        refresh(tasks);
        statusLabel.setText(message);
    }

    private void refresh(TaskList source) {
        displayedTasks.setAll(source.find(""));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
