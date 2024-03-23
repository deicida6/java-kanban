package manager;


import exceptions.*;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
   private int idCount = 0;
   protected HistoryManager historyManager = Managers.getDefaultHistory();
   protected final Map<Integer, Task> tasks = new HashMap<>();
   protected final Map<Integer, Subtask> subtasks = new HashMap<>();
   protected final Map<Integer, Epic> epics = new HashMap<>();
   protected TreeSet<Task> prioritiziedTasks;

    public InMemoryTaskManager() {
        prioritiziedTasks = new TreeSet<>((Task o1, Task o2) -> {
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
        if(timeIntersectionCheck(task,this.prioritiziedTasks) == null) {
            throw new TimeIntersectionException("Таска не создана из-за пересечения по времени");
        }
        task.setId(++idCount);
        tasks.put(task.getId(),task);
        prioritiziedTasks.add(task);
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
        if(timeIntersectionCheck(subtask,this.prioritiziedTasks) == null) {
            throw new TimeIntersectionException("Таска не создана из-за пересечения по времени");
        }
            subtask.setId(++idCount);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().add(subtask.getId());
            epicStatusUpdater(epic,subtasks);
            if(subtask.getStartTime() != null) {
                epicDurationUpdater(epic, subtasks);
            }
            prioritiziedTasks.add(subtask);
            return subtask.getId();

    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epicStatusUpdater(epic,subtasks);
        epicDurationUpdater(epic,subtasks);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epicStatusUpdater(epic,subtasks);
        epicDurationUpdater(epic,subtasks);
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
        epicStatusUpdater(epic,subtasks);
        epicDurationUpdater(epic,subtasks);
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
        return prioritiziedTasks;
    }
    // обновление startTime и duration
    private void epicDurationUpdater(Epic epic, Map<Integer, Subtask> subtaskMap) {
        if (subtaskMap.containsKey(epic.getSubtaskIds().stream().findFirst())) {

            LocalDateTime epicStartTime = Objects.requireNonNull(epic.getSubtaskIds().stream()
                    .map(subtaskMap::get)
                    .min(Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .stream().findFirst().orElse(null)).getStartTime();

            if (epicStartTime != null) {
                LocalDateTime epicEndTime = Objects.requireNonNull(epic.getSubtaskIds().stream()
                        .map(subtaskMap::get)
                        .max(Comparator.comparing(Task::getEndTime))
                        .stream().findFirst().orElse(null)).getEndTime();

                epic.setStartTime(epicStartTime);
                epic.setDuration(Duration.between(epicStartTime, epicEndTime));
            } else {
                epic.setStartTime(null);
                epic.setDuration(null);
            }
        }
    }
    // обновление статуса Эпика по его сабтаскам
     private void epicStatusUpdater(Epic epic, Map<Integer, Subtask> subtaskMap) {

        Collection<Integer> listOfSubTasks = epic.getSubtaskIds();
        int counterSameStatus = 0;
        TaskStatus firstSubtaskStatus = null;

        for (Integer idSubtask : listOfSubTasks) {
            Subtask subtask = subtaskMap.get(idSubtask);
            firstSubtaskStatus = subtask.getStatusTask();

            for (Integer idSecondSubtask : listOfSubTasks) {
                subtask = subtaskMap.get(idSecondSubtask);

                if (firstSubtaskStatus != subtask.getStatusTask()) {
                    epic.setStatusTask(TaskStatus.IN_PROGRESS);
                    break;
                } else {
                    counterSameStatus++;
                }
            }
            break;
        }
        if (counterSameStatus == listOfSubTasks.size()) {
            epic.setStatusTask(firstSubtaskStatus);
        }
    }
    // проверка пересечения по времени
    private Task timeIntersectionCheck(Task task, Collection<? extends Task> tasksTreeSet) {
        if (task.getStartTime() != null) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();
            List<? extends Task> correctionList1 = tasksTreeSet.stream()
                    .filter(taskFromTaskSet ->
                            (       (taskFromTaskSet.getStartTime() == null)
                                    ||
                                    (taskFromTaskSet.getStartTime().isAfter(taskEndTime) &&
                                            taskFromTaskSet.getEndTime().isAfter(taskEndTime))
                                    ||
                                    taskFromTaskSet.getStartTime().isBefore(taskStartTime) &&
                                            taskFromTaskSet.getEndTime().isBefore(taskStartTime))
                                    ||
                                    (taskFromTaskSet.getStartTime().isEqual(taskEndTime))
                                    ||
                                    (taskFromTaskSet.getEndTime().isEqual(taskStartTime)))
                    .collect(Collectors.toList());

            if (correctionList1.size() == tasksTreeSet.size()) {
                return task;
            } else return null;
        }
        return task;
    }
}
