package Manager;

import Task.Epic;
import Task.Subtask;
import Task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    protected Subtask subtask;
    protected Epic epic;
    protected Task task;
    protected  InMemoryTaskManager manager;
    @BeforeEach
    void beforeEach() {
        task = new Task("Name1","Decription1");
        epic = new Epic("Epic1","descriptionEpic");
        manager = new InMemoryTaskManager();

    }
    //проверка что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void shouldBeAddDifferentTaskAndGetOnId() {
        final int taskId = manager.addTask(task);
        final int epicId = manager.addEpic(epic);
        subtask = new Subtask("Subtask1","descriptionSubtask", epicId);
        final int subtaskId = manager.addSubtask(subtask);
        assertNotNull(taskId, "не создана таска");
        assertNotNull(epicId,"не создан эпик");
        assertNotNull(subtaskId,"не создана сабтаска");
        assertEquals(task,manager.getTaskById(taskId),"не найдена таска по ID");
        assertEquals(epic,manager.getEpicById(epicId),"не найден эпик по ID");
        assertEquals(subtask,manager.getSubtaskById(subtaskId),"не найдена сабтаска по ID");
    }
    //проверка что задачи с генерированым ID и заданным не будут конфликтовать
    @Test
    void shouldBeAddSetIdTaskAndGenerationIdTask() {
        int setId = 1;
        final int taskId1 = manager.addTask(task);
        final int taskId2 = manager.addTask(task);
        task.setId(setId);
        assertEquals(taskId1,task.getId(),"задачи конфликтуют внутри менеджера");

    }

}