package Manager;

import Task.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
   private int idCount = 0;
   private final HashMap<Integer, Task> tasks = new HashMap<>();
   private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
   private final HashMap<Integer, Epic> epics = new HashMap<>();

    //2a получение списка задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    //2b удаление всех задач
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpic(epic);
        }
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    //2c получение по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    //2d создание
    public int addTask(Task task) {
        task.setId(++idCount);
        tasks.put(task.getId(),task);
        return task.getId();
    }

    public int addEpic(Epic epic) {
        epic.setId(++idCount);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public int addSubtask(Subtask subtask) {
        subtask.setId(++idCount);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().add(subtask.getId());
        updateEpicStatus(epic);
        return subtask.getId();
    }

    //2e обновление
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    //2f удаление по идентификатору
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = getEpicById(id);
        for (int idSubtask: epic.getSubtaskIds()) {
            subtasks.remove(idSubtask);
        }
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.getSubtaskIds().remove(Integer.valueOf(id));
        updateEpicStatus(epic);

    }

    //3a получение списка всех подзадач определенного эпика
    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (Integer idSubtask : epic.getSubtaskIds()) {
            listSubtasks.add(getSubtaskById(idSubtask));
        }
        return listSubtasks;

    }

    //4b логика обновления статуса у эпиков
    private void updateEpicStatus(Epic epic) {
        int newCount = 0;
        int doneCount = 0;
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatusTask(TaskStatus.NEW);
            return;
        }
        for (Integer idSub : epic.getSubtaskIds()) {
            if (getSubtaskById(idSub).getStatusTask() == TaskStatus.NEW) {
                newCount++;
            }
            if (getSubtaskById(idSub).getStatusTask() == TaskStatus.DONE) {
                doneCount++;
            }
        }
        int countOfSubtasks = epic.getSubtaskIds().size();
        if (newCount == countOfSubtasks) {
            epic.setStatusTask(TaskStatus.NEW);
        } else if (doneCount == countOfSubtasks) {
            epic.setStatusTask(TaskStatus.DONE);
        } else {
            epic.setStatusTask(TaskStatus.IN_PROGRESS);
        }
    }
}
