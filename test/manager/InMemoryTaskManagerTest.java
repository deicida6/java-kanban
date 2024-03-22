package manager;


import org.junit.jupiter.api.TestInstance;


import org.junit.jupiter.api.BeforeEach;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }



}