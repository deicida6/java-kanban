package manager;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import org.junit.jupiter.api.BeforeEach;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertNull;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

}