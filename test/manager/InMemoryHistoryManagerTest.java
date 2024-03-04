package manager;

import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    protected Subtask createSubtask(int epicId) {
        return new Subtask("name1", "description1",epicId);
    };
    protected Epic createEpic() {
        return new Epic("name1", "description1");
    };
    protected Task createTask() {
        return new Task("name1", "description1");
    };
    protected TaskManager manager;
    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
    }
    //проверка что в историю записывается неограниченное кол-во задач
    @Test
    public void shouldBeInfinityTasksInHistory() {
        int random = new Random().nextInt(1000);
        for (int i = 0; i < random; i++) {
            manager.getTaskById(manager.addTask(createTask()));
        }
        assertEquals(random, manager.getHistory().size());
    }
    //проверка добавления Таски в историю
    @Test
    public void shouldBeAddTasksInHistory() {
        Task task = createTask();
        manager.getTaskById(manager.addTask(task));
        assertEquals(List.of(task),manager.getHistory());
    }
    //проверка добавления Эпик в историю
    @Test
    public void shouldBeAddEpicInHistory() {
        Epic epic = createEpic();
        manager.getTaskById(manager.addTask(epic));
        assertEquals(List.of(epic),manager.getHistory());
    }
    //проверка добавления Сабтаск в историю
    @Test
    public void shouldBeAddSubtaskInHistory() {
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        Subtask subtask = createSubtask(epicId);
        int subtaskId = manager.addSubtask(subtask);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);
        assertEquals(List.of(epic,subtask),manager.getHistory());
    }
    //проверка удаления Таски из истории
    @Test
    public void shouldBeRemoveTasksInHistory() {
        Task task = createTask();
        manager.getTaskById(manager.addTask(task));
        manager.removeTaskById(task.getId());
        System.out.println(manager.getHistory());
        for (Task elem : manager.getHistory()){
            assertEquals(null,elem);
        }
    }

    //проверка удаления Эпика из истории
    @Test
    public void shouldBeRemoveEpicInHistory() {
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        manager.getEpicById(epicId);
        manager.removeEpicById(epicId);
        assertEquals(true,manager.getHistory().isEmpty());
    }
    //проверка удаления Сабтаск из истории
    @Test
    public void shouldBeRemoveSubtaskInHistory() {
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        Subtask subtask = createSubtask(epicId);
        int subtaskId = manager.addSubtask(subtask);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);
        manager.removeSubtaskById(subtaskId);
        assertEquals(List.of(epic),manager.getHistory());
    }

}