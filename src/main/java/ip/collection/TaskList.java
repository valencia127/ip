
package ip.collection;

import java.util.ArrayList;
import java.util.List;

import ip.model.Task;

public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        assert tasks != null : "Task list storage must not be null";
        this.tasks = tasks;
    }

    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    public Task delete(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public List<Task> find(String keyword) {
        assert keyword != null : "Search keywords must not be null";
        String normalizedKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(normalizedKeyword))
                .toList();
    }
}
