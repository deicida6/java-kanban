package Manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    /*проверка что утилитарный класс всегда возвращает
     проинициализированные и готовые к работе экземпляры менеджеров*/
    @Test
    void shouldNotBeNullGetDefault() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void shouldNotBeNullInGetDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}