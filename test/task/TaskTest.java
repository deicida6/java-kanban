package task;

import manager.InMemoryTaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
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
    //проверка что экземпляры класса Task равны если равны их ID
    @Test
    void shouldBePositiveTaskWhenIdAreEqual() {
       final int taskId = manager.addTask(task);
       final Task savedTask = manager.getTaskById(taskId);
       assertEquals(task,savedTask,"задачи не совпадают");
    }
    //проверка что наследники класса Task (epic) равны если равны их ID
    @Test
    void shouldBePositiveEpicWhenIdAreEqual() {
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpicById(epicId);
        assertEquals(epic,savedEpic,"Эпики не совпадают");

    }
    //проверка что наследники класса Task (epic) равны если равны их ID
    @Test
    void shouldBePositiveSubtaskWhenIdAreEqual() {
        final int epicId = manager.addEpic(epic);
        subtask = new Subtask("Subtask1","descriptionSubtask", epicId);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtaskById(subtaskId);
        assertEquals(subtask,savedSubtask,"Сабтаски не совпадают");

    }


}
