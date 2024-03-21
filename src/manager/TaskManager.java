package manager;

import task.*;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    //Получение списка всех задач
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpic();

    //просмотр истории
    List<Task> getHistory();

    //Удаление всех задач
    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpic();

    //Получение по идентификатору
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //Создание задач
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    //Обновление задач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //Удаление по идентификатору
    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    //Получение списка всех подзадач определенного эпика
    List<Subtask> getAllSubtasksOfEpic(Epic epic);
    Collection<? extends Task> getPrioritizedTasks();
}
