package Manager;

import Task.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
   private int idCount = 0;
   private final HistoryManager historyManager = Managers.getDefaultHistory();
   private final Map<Integer, Task> tasks = new HashMap<>();
   private final Map<Integer, Subtask> subtasks = new HashMap<>();
   private final Map<Integer, Epic> epics = new HashMap<>();

    //2a получение списка задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //2b удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpic(epic);
        }
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    //2c получение по идентификатору
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);

    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    //2d создание
    @Override
    public int addTask(Task task) {
        task.setId(++idCount);
        tasks.put(task.getId(),task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(++idCount);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if(epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++idCount);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().add(subtask.getId());
            updateEpicStatus(epic);
            return subtask.getId();
        }
        return 0;
    }

    //2e обновление
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    //2f удаление по идентификатору
    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = getEpicById(id);
        if(epic.getSubtaskIds()!=null){
            for (int idSubtask: epic.getSubtaskIds()) {
                subtasks.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.getSubtaskIds().remove(Integer.valueOf(id));
        updateEpicStatus(epic);
        historyManager.remove(id);

    }

    //3a получение списка всех подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (Integer idSubtask : epic.getSubtaskIds()) {
            listSubtasks.add(subtasks.get(idSubtask));
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
            if (subtasks.get(idSub).getStatusTask() == TaskStatus.NEW) {
                newCount++;
            }
            if (subtasks.get(idSub).getStatusTask() == TaskStatus.DONE) {
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
