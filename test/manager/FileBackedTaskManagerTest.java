package manager;

import org.junit.jupiter.api.TestInstance;
import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File f;

    @BeforeEach
    public void setUp() throws IOException {
        f = File.createTempFile("testSave","csv");
        manager = new FileBackedTaskManager(f);
    }
    @Test
    public void shouldSaveAndLoadEmptyFile() throws IOException {
        assertEquals(manager.getAllTasks(),manager.getHistory());
        manager = new FileBackedTaskManager(f);
        assertEquals(manager.getAllTasks(),manager.getHistory());
    }

    @Test
    public void shouldSaveAndLoadSomeTasks() throws IOException {
        Task task = createTask();
        Task task1 = createTask();
        manager.addTask(task);
        manager.addTask(task1);
        assertEquals(List.of(task,task1), manager.getAllTasks());
        manager = new FileBackedTaskManager(f);
        assertEquals(List.of(task,task1), manager.getAllTasks());
    }
}