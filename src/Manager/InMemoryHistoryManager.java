package Manager;

import Task.*;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final int lastViewedTasks = 10;
    private final ArrayList<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            historyTasks.add(task);
            if (historyTasks.size() > lastViewedTasks) {
                historyTasks.remove(0);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyTasks);
    }
}