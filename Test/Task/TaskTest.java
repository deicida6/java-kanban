package Task;

import Manager.InMemoryTaskManager;

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
    //проверка что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void shouldNotBeAddEpicToEpic() {
        int idEpic = -1;
        subtask = new Subtask("Subtask1","descriptionSubtask", idEpic);
        final int subtaskId1 = manager.addSubtask(subtask);
        assertNotNull(subtaskId1, "добавили epic сам в себя");
    }
    //проверка что объект Subtask нельзя сделать своим же эпиком
    @Test
    void shouldNotBeAddSubtaskToEpic() {
        final int epicId = manager.addEpic(epic);
        subtask = new Subtask("Subtask1","descriptionSubtask", epicId);
        final int subtaskId1 = manager.addSubtask(subtask);
        subtask = new Subtask("Subtask2","descriptionSubtask1", subtaskId1);
        final int subtaskId2 = manager.addSubtask(subtask);
        assertNotNull(subtaskId2,"subtask1 стал эпиком");

    }

}
