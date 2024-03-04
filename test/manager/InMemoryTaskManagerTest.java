package manager;

import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    protected Subtask createSubtask(int epicId) {
        return new Subtask("name1", "description1",epicId);
    }
    protected Epic createEpic() {
        return new Epic("name1", "description1");
    }
    protected Task createTask() {
        return new Task("name1", "description1");
    }
    protected TaskManager manager;
    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    // создание Таски
    @Test
    void shouldBeAddTaskInTaskManager(){
        Task task = createTask();
        manager.addTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks.getFirst());
        assertEquals(TaskStatus.NEW, task.getStatusTask());
        assertEquals(List.of(task),tasks);
    }
    // создание Эпика
    @Test
    void shouldBeAddEpicInTaskManager(){
        Epic epic = createEpic();
        manager.addEpic(epic);
        List<Epic> epics = manager.getAllEpic();
        assertNotNull(epics.getFirst());
        assertEquals(TaskStatus.NEW, epic.getStatusTask());
        assertEquals(List.of(epic),epics);
    }

    // создание Сабтаска
    @Test
    void shouldBeAddSubtaskInTaskManager(){
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        Subtask subtask = createSubtask(epicId);
        manager.addSubtask(subtask);
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtasks.getFirst());
        assertEquals(TaskStatus.NEW, subtask.getStatusTask());
        assertEquals(List.of(subtask),subtasks);
    }

    // удаление Таски
    @Test
    void shouldBeDeleteTaskInTaskManager(){
        Task task = createTask();
        manager.addTask(task);
        manager.removeTaskById(task.getId());
        for (Task elem : manager.getAllTasks()){
            assertNull(elem);
        }

    }
    // удаление Эпика
    @Test
    void shouldBeDeleteEpicInTaskManager(){
        Epic epic = createEpic();
        manager.addEpic(epic);
        manager.removeEpicById(epic.getId());
        for (Epic elem : manager.getAllEpic()){
            assertNull(elem);
        }
    }

    // удаление Сабтаска
    @Test
    void shouldBeDeleteSubtaskInTaskManager(){
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        Subtask subtask = createSubtask(epicId);
        manager.addSubtask(subtask);
        manager.removeSubtaskById(subtask.getId());
        for (Subtask elem : manager.getAllSubtasks()){
            assertNull(elem);
        }
    }
    // проверка изменения статуса у Таски
    @Test
    void shouldBeChangeStatusTaskToInProgress() {
        Task task = createTask();
        manager.addTask(task);
        task.setStatusTask(TaskStatus.IN_PROGRESS);
        List<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks.getFirst());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatusTask());
        assertEquals(List.of(task),tasks);
    }
    //проверка изменения статуса у Эпика при изменении статуса у его Сабтаска
    @Test
    void shouldBeChangeStatusEpicToInProgressWhenChangeStatusOnSubtask() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        manager.addSubtask(subtask);
        subtask.setStatusTask(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatusTask());
    }
    //проверка что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void shouldBeAddDifferentTaskAndGetOnId() {
        Task task = createTask();
        Epic epic = createEpic();
        final int taskId = manager.addTask(task);
        final int epicId = manager.addEpic(epic);
        Subtask subtask = createSubtask(epicId);
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
        Task task1 = createTask();
        Task task2 = createTask();
        final int taskId1 = manager.addTask(task1);
        final int taskId2 = manager.addTask(task2);
        task1.setId(setId);
        assertEquals(taskId1,task1.getId(),"задачи конфликтуют внутри менеджера");
    }

    // Удаляемые подзадачи не должны хранить внутри себя старые ID
    @Test
    void shouldDoNotSaveOldIdINDeletedSubtaskInTaskManager(){
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = createSubtask(epicId);
        Subtask subtask2 = createSubtask(epicId);
        Subtask subtask3 = createSubtask(epicId);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.removeSubtaskById(subtask2.getId());
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(List.of(subtask1,subtask3),subtasks);
    }

}