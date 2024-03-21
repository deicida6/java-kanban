package manager;

import org.junit.jupiter.api.*;
import task.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Subtask createSubtask(int epicId) {
        return new Subtask("name1", "description1",epicId);
    }
    protected Epic createEpic() {
        return new Epic("name1", "description1");
    }
    protected Task createTask() {
        return new Task("name1", "description1");
    }
    // создание Таски
    @Test
    void shouldBeAddTaskInTaskManager(){
        Task task = createTask();
        assertEquals(task,manager.getTaskById(manager.addTask(task)));
    }
    // создание Эпика
    @Test
    void shouldBeAddEpicInTaskManager(){
        Epic epic = createEpic();
        assertEquals(epic,manager.getEpicById(manager.addEpic(epic)));
    }
    // создание Сабтаска
    @Test
    void shouldBeAddSubtaskInTaskManager(){
        Subtask subtask = createSubtask(manager.addEpic(createEpic()));
        assertEquals(subtask,manager.getSubtaskById(manager.addSubtask(subtask)));
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
        Subtask subtask = createSubtask(manager.addEpic(createEpic()));
        manager.addSubtask(subtask);
        manager.removeSubtaskById(subtask.getId());
        for (Subtask elem : manager.getAllSubtasks()){
            System.out.println(elem);
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
        manager.addTask(task);
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        manager.addSubtask(subtask);
        assertNotNull(task, "не создана таска");
        assertNotNull(epic,"не создан эпик");
        assertNotNull(subtask,"не создана сабтаска");
        assertEquals(task,manager.getTaskById(task.getId()),"не найдена таска по ID");
        assertEquals(epic,manager.getEpicById(epic.getId()),"не найден эпик по ID");
        assertEquals(subtask,manager.getSubtaskById(subtask.getId()),"не найдена сабтаска по ID");
    }
    // Удаляемые подзадачи не должны хранить внутри себя старые ID
    @Test
    void shouldDoNotSaveOldIdINDeletedSubtaskInTaskManager() {
        Epic epic = createEpic();
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = createSubtask(epicId);
        Subtask subtask2 = createSubtask(epicId);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeSubtaskById(subtask2.getId());
        assertEquals(List.of(subtask1),manager.getAllSubtasks());
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
    //проверка добавления в историю
    @Test
    void getHistory() {
        Task task1 = createTask();
        Task task2 = createTask();
        manager.getTaskById(manager.addTask(task1));
        manager.getTaskById(manager.addTask(task2));
        assertEquals(manager.getHistory(),List.of(task1,task2));
    }

    //тесты по ТЗ_8

    @Test
    void shouldBeEpicStatusIfAllSubtaskStatusNew() {
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask(epic1.getId());
        Subtask subtask2 = createSubtask(epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW,epic1.getStatusTask(),"статусы различаются");
    }

    @Test
    void shouldBeEpicStatusIfAllSubtaskStatusDone() {
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask(epic1.getId());
        Subtask subtask2 = createSubtask(epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        subtask1.setStatusTask(TaskStatus.DONE);
        subtask2.setStatusTask(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE,epic1.getStatusTask(),"статусы различаются");
    }

    @Test
    void shouldBeEpicStatusIfSubtaskStatusDoneAndNew() {
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask(epic1.getId());
        Subtask subtask2 = createSubtask(epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        subtask2.setStatusTask(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS,epic1.getStatusTask(),"статусы различаются");
    }
    @Test
    void shouldBeEpicStatusIfSubtaskStatusInProgress() {
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask(epic1.getId());
        Subtask subtask2 = createSubtask(epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        subtask2.setStatusTask(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS,epic1.getStatusTask(),"статусы различаются");
    }
    // task2 не создасться и не получит id ввиду пересечения по времени с task1
    @Test
    void correctlyWillBeClaculationIntersectionOfIntervals() {
        Task task1 = new Task("name1","description1","2024-03-21 13:00","50");
        Task task2 = new Task("name1","description1","2024-03-21 13:30","50");
        Task task3 = new Task("name1","description1","2024-03-21 13:51","50");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        assertEquals(0,task2.getId());
        assertNotNull(task1.getId());
        assertNotNull(task3.getId());
    }

    @Test
    void shouldBeAddEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }
}
