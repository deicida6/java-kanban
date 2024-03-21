package manager;

import task.*;
import logic.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
   private int idCount = 0;
   protected HistoryManager historyManager = Managers.getDefaultHistory();
   protected final Map<Integer, Task> tasks = new HashMap<>();
   protected final Map<Integer, Subtask> subtasks = new HashMap<>();
   protected final Map<Integer, Epic> epics = new HashMap<>();
   protected TreeSet<Task> priorityTree;
   private final TimeValidation timeValidation = new TimeValidation();
   private final EpicStatusUpdater epicStatusUpdater = new EpicStatusUpdater();
   private final EpicDurationUpdater epicDurationUpdater = new EpicDurationUpdater();

    public InMemoryTaskManager() {
        priorityTree = new TreeSet<>((Task o1, Task o2) -> {
            if (o1.getStartTime() != null && o2.getStartTime() != null) {
                if (o1.getStartTime().isAfter(o2.getStartTime())) {
                    return 1;
                } else if (o1.getStartTime() == (o2.getStartTime())) {
                    return -1;
                }
            } else if (o1.getStartTime() == null && o2.getStartTime() != null) {
                return 1;
            } else if (o1.getStartTime() != null && o2.getStartTime() == null) {
                return -1;
            }
            return -1;
        });
    }

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


    @Override
    public void removeAllTasks() {
        for (Map.Entry<Integer, Task> taskEntry : tasks.entrySet()) {
              historyManager.remove(taskEntry.getKey());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Map.Entry<Integer, Subtask> subtaskEntry : subtasks.entrySet()) {
            historyManager.remove(subtaskEntry.getKey());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpic(epic);
        }
    }

    @Override
    public void removeAllEpic() {
       for (Map.Entry<Integer,Epic> epicEntry : epics.entrySet()) {
           for (Map.Entry<Integer, Subtask> subtaskEntry : subtasks.entrySet()) {
               historyManager.remove(subtaskEntry.getKey());
           }
           historyManager.remove(epicEntry.getKey());
       }
        epics.clear();
        subtasks.clear();
    }


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


    @Override
    public int addTask(Task task) {
        Collection<? extends Task> set = getPrioritizedTasks();
        if(timeValidation.validate(task,set) != null) {
            task.setId(++idCount);
            tasks.put(task.getId(),task);
            priorityTree.add(task);
            return task.getId();
        } else {
            System.out.println("Таска не создана из-за пересечения по времени");
            return 0;
        }

    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(++idCount);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        Collection<? extends Task> set = getPrioritizedTasks();
        if(timeValidation.validate(subtask,set) != null) {
            subtask.setId(++idCount);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().add(subtask.getId());
            epicStatusUpdater.verifyStatus(epic,subtasks);
            if(subtask.getStartTime() != null) {
                epicDurationUpdater.verifyTime(epic, subtasks);
            }
            priorityTree.add(subtask);
            return subtask.getId();
        } else {
            System.out.println("Таска не создана из-за пересечения по времени");
            return 0;
        }

    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epicStatusUpdater.verifyStatus(epic,subtasks);
        epicDurationUpdater.verifyTime(epic,subtasks);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epicStatusUpdater.verifyStatus(epic,subtasks);
        epicDurationUpdater.verifyTime(epic,subtasks);
    }

    //2f удаление по идентификатору
    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
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
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(Integer.valueOf(id));
        epicStatusUpdater.verifyStatus(epic,subtasks);
        epicDurationUpdater.verifyTime(epic,subtasks);
        historyManager.remove(id);

    }

    @Override
    public List<Subtask> getAllSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (Integer idSubtask : epic.getSubtaskIds()) {
            listSubtasks.add(subtasks.get(idSubtask));
        }
        return listSubtasks;

    }

    @Override
    public Collection<? extends Task> getPrioritizedTasks() {
        return priorityTree;
    }


}
