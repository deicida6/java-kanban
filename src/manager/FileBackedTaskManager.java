package manager;


import exceptions.*;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,description,status,epicid\n");

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.write(getTaskById(entry.getKey()).toString() + "\n");
            }
            writer.write("\n"); //отделение задач
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.write(getEpicById(entry.getKey()).toString() + "\n");
            }
            writer.write("\n"); //отделение эпиков
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.write(getSubtaskById(entry.getKey()).toString() + "\n");
            }
            writer.write("\n"); //отделение подзадач

            if (!historyManager.getHistory().isEmpty()) { //Если история не пустая записать в файл
                writer.write(historyToString(historyManager)); //записываем
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при записи файла");
        }
    }

    public void loadFromFile(File file) {
        FileBackedTaskManager backedManager;
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            backedManager = new FileBackedTaskManager(file);
            while (reader.ready()) {
                line = reader.readLine();
                if (line.isEmpty()) {
                    continue;
                }

                String[] lineArr = line.split(",");

                if (lineArr[1].equalsIgnoreCase("TASK")) {
                    Task task = fromString(line);
                    tasks.put(Objects.requireNonNull(task).getId(), task);
                } else if (lineArr[1].equalsIgnoreCase("EPIC")) {
                    Task task = fromString(line);
                    epics.put(Objects.requireNonNull(task).getId(), (Epic) task);
                } else if (lineArr[1].equalsIgnoreCase("SUBTASK")) {
                    Task task = fromString(line);
                    subtasks.put(Objects.requireNonNull(task).getId(), (Subtask) task);
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при загрузке данных");
        }
        List<Integer> listForHistory = new ArrayList<>();
        if (!line.isEmpty()) {
            listForHistory = historyFromString(line);
        }

        for (Integer historyId : listForHistory) {
            int id = historyId;

            if (epics.containsKey(id)) {
                backedManager.getHistory().add(epics.get(id));
            }

            if (subtasks.containsKey(id)) {
                backedManager.getHistory().add(subtasks.get(id));
            }

            if (tasks.containsKey(id)) {
                backedManager.getHistory().add(subtasks.get(id));
            }
        }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder idOfHistory = new StringBuilder();
        for (Task task : manager.getHistory()) {
            idOfHistory.append(task.getId()).append(",");
        }
        return idOfHistory.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> idOfHistory = new ArrayList<>();
        String[] valueLikeArray = value.split(",");
        for (String id : valueLikeArray) {
            idOfHistory.add(Integer.parseInt(id));
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
        save();
        return task;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtask = super.getAllSubtasks();
        save();
        return subtask;
    }

    @Override
    public List<Epic> getAllEpic() {
        List<Epic> epic = super.getAllEpic();
        save();
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
       // save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
       // save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        //save();
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
