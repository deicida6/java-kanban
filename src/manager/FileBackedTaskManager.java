package manager;


import exceptions.*;

import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (file.exists()) {
            loadFromFile(file);
        }
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,description,status,epicid\n");

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.write(taskToString(getTaskById((entry.getKey()))) + "\n");
            }
            writer.write("\n"); //отделение задач
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.write(epicToString(getEpicById(entry.getKey())) + "\n");
            }
            writer.write("\n"); //отделение эпиков
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.write(subTaskToString(getSubtaskById(entry.getKey())) + "\n");
            }
            writer.write("\n"); //отделение подзадач

            if (!historyManager.getHistory().isEmpty()) { //Если история не пустая записать в файл
                writer.write(historyToString(historyManager)); //записываем
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при записи файла");
        }
    }

    public String taskToString(Task task) {
        if (task.getStartTime() == null) {
            return task.getId() + ",TASK,"
                    + task.getNameTask() + ","
                    + task.getStatusTask() + ","
                    + task.getDescriptionTask()
                    + ",n/a,n/a,n/a,\n";
        } else
            return task.getId() + ",TASK,"
                    + task.getNameTask() + ","
                    + task.getStatusTask() + ","
                    + task.getDescriptionTask()
                    + "," + task.getStartTime() + "," + task.getDuration()
                    + "," + task.getEndTime() + ",\n";
    }

    public String epicToString(Epic epic) {
        if (!epic.getSubtaskIds().isEmpty() && epic.getStartTime() != null) {
            return epic.getId() + ",EPIC,"
                    + epic.getNameTask() + ","
                    + epic.getStatusTask() + ","
                    + epic.getDescriptionTask()
                    + "," + epic.getStartTime() + ","
                    + epic.getDuration() + ","
                    + epic.getEndTime() + ",\n";
        } else return epic.getId() + ",EPIC,"
                + epic.getStatusTask() + ","
                + epic.getNameTask() + ","
                + epic.getDescriptionTask()
                + ",n/a,n/a,n/a,\n";
    }

    public String subTaskToString(Subtask subTask) {
        if (subTask.getStartTime() == null) {
            return subTask.getId() + ",SUBTASK,"
                    + subTask.getNameTask() + ","
                    + subTask.getStatusTask() + ","
                    + subTask.getDescriptionTask() + ","
                    + subTask.getEpicId() + ","
                    + ",n/a,n/a,n/a,\n";
        } else
            return subTask.getId() + ",SUBTASK,"
                    + subTask.getNameTask() + ","
                    + subTask.getStatusTask() + ","
                    + subTask.getDescriptionTask() + ","
                    + subTask.getEpicId() + ","
                    + subTask.getStartTime()
                    + "," + subTask.getDuration() + ","
                    + subTask.getEndTime() + ",\n";
    }


    public void loadFromFile(File file) {

        try (BufferedReader buffReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String[] taskParts;
            while (buffReader.ready()) {
                String stringLine = buffReader.readLine();
                if (stringLine.endsWith(",")) {
                    taskParts = stringLine.split(",");
                    int id = Integer.parseInt(taskParts[0]);
                    String type = taskParts[1];
                    Task task = fromString(stringLine);
                    task.setId(id);
                    switch (type) {
                        case "TASK": {
                            tasks.put(id, task);
                            break;
                        }
                        case "EPIC": {
                            epics.put(id, (Epic) task);
                            break;
                        }
                        case "SUBTASK": {
                            subtasks.put(id, (Subtask) task);
                            epics.get(((Subtask) task).getEpicId()).getSubtaskIds().add(task.getId());
                            break;
                        }
                    }
                } else if (!stringLine.startsWith("id") && !stringLine.isEmpty()) {
                    List<Integer> historyFromFile = historyFromString(stringLine);
                    for (Integer id : historyFromFile) {
                        if (tasks.containsKey(id)) {
                            getTaskById(id);
                        } else if (epics.containsKey(id)) {
                            getEpicById(id);
                        } else {
                            getSubtaskById(id);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new ManagerLoadException("Произошла ошибка загрузки из файла.");
        }
        prioritiziedTasks.addAll(tasks.values());
        prioritiziedTasks.addAll(subtasks.values());
    }

    public String historyToString(HistoryManager historyManager) {

        List<Task> historyList = historyManager.getHistory();
        if (historyList != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Task task : historyList) {
                stringBuilder
                        .append(task.getId())
                        .append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return String.valueOf(stringBuilder);
        } else return "";
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> idOfHistory = new ArrayList<>();
        String[] valueLikeArray = value.split(",");
        for (String id : valueLikeArray) {
            idOfHistory.add(Integer.valueOf(id));
        }
        return idOfHistory;
    }

    private static Task fromString(String value) {
        String[] values = value.split(",");
        String id = values[0];
        String type = values[1];
        String name = values[2];
        String taskStatus = values[3];
        String description = values[4];
        Integer idOfEpic = type.equals(TaskType.SUBTASK.toString()) ? Integer.valueOf(values[7]) : null;
        switch (type) {
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(Integer.parseInt(id));
                epic.setStatusTask(TaskStatus.valueOf(taskStatus.toUpperCase()));
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(name, description, idOfEpic);
                subtask.setId(Integer.parseInt(id));
                subtask.setStatusTask(TaskStatus.valueOf(taskStatus.toUpperCase()));
                return subtask;
            case "TASK":
                Task task = new Task(name, description);
                task.setId(Integer.parseInt(id));
                task.setStatusTask(TaskStatus.valueOf(taskStatus.toUpperCase()));
                return task;
            default:
                return null;
        }
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;

    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> task = super.getAllTasks();
        return task;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtask = super.getAllSubtasks();
        return subtask;
    }

    @Override
    public List<Epic> getAllEpic() {
        List<Epic> epic = super.getAllEpic();
        return epic;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskHistory = super.getHistory();
        save();
        return taskHistory;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();

    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();

    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();

    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();

    }

    @Override
    public List<Subtask> getAllSubtasksOfEpic(Epic epic) {
        List<Subtask> subtasks = super.getAllSubtasksOfEpic(epic);
        save();
        return subtasks;
    }
}
