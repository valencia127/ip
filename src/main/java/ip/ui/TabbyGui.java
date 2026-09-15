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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides a JavaFX graphical interface for the Tabby task manager. */
public class TabbyGui extends Application {

    private final Storage storage = new Storage("data/tabby.txt");
    private final TaskList tasks = new TaskList();
    private final ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    private final ListView<Task> taskListView = new ListView<>(displayedTasks);
    private final TextField commandField = new TextField();
    private final Label statusLabel = new Label("Ready when you are.");
    private final Label countLabel = new Label();

    @Override
    public void start(Stage stage) {
        loadTasks();
        Label eyebrow = new Label("TABBY / TASK MANAGER");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label("Stay on top of things.");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Use a command below, or select a task for quick actions.");
        subtitle.getStyleClass().add("subtitle");
        commandField.setPromptText("todo read lecture notes");
        commandField.setOnAction(event -> executeCommand());

        Button runButton = new Button("Run  ↵");
        runButton.getStyleClass().add("primary-button");
        runButton.setOnAction(event -> executeCommand());
        Button listButton = new Button("List");
        listButton.setOnAction(event -> refresh(tasks));
        Button markButton = new Button("Mark done");
        markButton.setOnAction(event -> changeSelectedTask(true));
        Button unmarkButton = new Button("Unmark");
        unmarkButton.setOnAction(event -> changeSelectedTask(false));
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> deleteSelectedTask());

        HBox.setHgrow(commandField, Priority.ALWAYS);
        HBox commandBar = new HBox(10, commandField, runButton);
        commandBar.getStyleClass().add("command-bar");
        HBox actions = new HBox(8, listButton, markButton, unmarkButton, deleteButton);
        countLabel.getStyleClass().add("count-label");
        HBox listHeading = new HBox(new Label("YOUR TASKS"), countLabel);
        listHeading.getStyleClass().add("list-heading");
        HBox.setHgrow(countLabel, Priority.ALWAYS);
        countLabel.setMaxWidth(Double.MAX_VALUE);
        countLabel.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        VBox top = new VBox(5, eyebrow, title, subtitle, commandBar, actions, listHeading);
        top.getStyleClass().add("top-panel");

        Label emptyLabel = new Label("No tasks yet\nAdd your first one above.");
        emptyLabel.getStyleClass().add("empty-state");
        taskListView.setPlaceholder(emptyLabel);
        taskListView.setFixedCellSize(58);
        taskListView.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label number = new Label(String.format("%02d", getIndex() + 1));
                number.getStyleClass().add("task-number");
                Label text = new Label(task.toString());
                text.getStyleClass().add("task-text");
                text.setWrapText(true);
                HBox row = new HBox(14, number, text);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                setGraphic(row);
                setText(null);
                getStyleClass().removeAll("done-task");
                if (task.getStatusIcon().equals("X")) {
                    getStyleClass().add("done-task");
                }
            }
        });

        BorderPane root = new BorderPane(taskListView);
        root.getStyleClass().add("app-shell");
        root.setTop(top);
        root.setBottom(statusLabel);
        BorderPane.setMargin(taskListView, new Insets(0, 24, 12, 24));
        BorderPane.setMargin(statusLabel, new Insets(0, 24, 18, 24));
        statusLabel.getStyleClass().add("status-label");
        Scene scene = new Scene(root, 760, 560);
        scene.getStylesheets().add(getClass().getResource("/tabby.css").toExternalForm());
        stage.setTitle("Tabby");
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(420);
        stage.show();
    }

    private void loadTasks() {
        try {
            List<Task> loadedTasks = storage.load();
            loadedTasks.forEach(tasks::add);
            refresh(tasks);
        } catch (TabbyException exception) {
            showError(exception.getMessage());
        }
    }

    private void executeCommand() {
        String input = commandField.getText().trim();
        if (input.isEmpty()) {
            showError("Please enter a command.");
            return;
        }
        try {
            if (input.equals("help")) {
                statusLabel.setText("Commands: list, find KEYWORD, todo DESCRIPTION, deadline DESCRIPTION /by DATE, "
                        + "event DESCRIPTION /from DATE /to DATE, mark NUMBER, unmark NUMBER, delete NUMBER, bye");
            } else if (input.equals("list")) {
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
            showError(exception.getMessage());
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
            showError("Select a task first.");
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
            showError(exception.getMessage());
        }
    }

    private void deleteSelectedTask() {
        int index = taskListView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= tasks.size()) {
            showError("Select a task first.");
            return;
        }
        try {
            tasks.delete(index);
            saveAndRefresh("Task deleted.");
        } catch (TabbyException exception) {
            showError(exception.getMessage());
        }
    }

    private void saveAndRefresh(String message) throws TabbyException {
        storage.save(tasks);
        refresh(tasks);
        statusLabel.setText(message);
        statusLabel.getStyleClass().remove("error");
    }

    private void showError(String message) {
        statusLabel.setText("!  " + message);
        if (!statusLabel.getStyleClass().contains("error")) {
            statusLabel.getStyleClass().add("error");
        }
    }

    private void refresh(TaskList source) {
        displayedTasks.setAll(source.find(""));
        countLabel.setText(displayedTasks.size() + (displayedTasks.size() == 1 ? " task" : " tasks"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
