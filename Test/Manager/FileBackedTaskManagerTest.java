package Manager;

import Task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
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

    File file;

    @BeforeEach
    void beforeEach() {
        file = new File("save.csv");
        manager = new FileBackedTaskManager(file);
    }
    @Test
    public void shouldSaveAndLoadEmptyFile() throws IOException {
        assertEquals(manager.getAllTasks(),manager.getHistory());
        manager = Managers.getDefault(file);
        assertEquals(manager.getAllTasks(),manager.getHistory());
    }

    @Test
    public void shouldSaveAndLoadSomeTasks() throws IOException {
        Task task = createTask();
        Task task1 = createTask();
        manager.addTask(task);
        manager.addTask(task1);
        assertEquals(List.of(task,task1), manager.getAllTasks());
        manager = Managers.getDefault(file);
        assertEquals(List.of(task,task1), manager.getAllTasks());
    }

}