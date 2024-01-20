package Manager;

import Task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    protected Subtask subtask;
    protected Epic epic;
    protected Task task;
    protected  TaskManager manager;
    protected HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault();
        task = new Task("Name1","Decription1");
    }
    //проверка что задачи добавляемые в HistoryManager сохраняют предыдущую версию задачи и её данных
    @Test
    void shouldBeSavedAllHistoryTasks() {
        historyManager.add(task);
        historyManager.add(task);
        for (Task history : historyManager.getHistory()) {
            assertEquals(history, task,"не сохранилась история");
        }
    }
}