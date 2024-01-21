package Manager;

import Task.*;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int maxHistorySize = 10;
    private final List<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (maxHistorySize <= historyTasks.size()) {
            historyTasks.remove(0);
        }
        historyTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTasks);
    }
}