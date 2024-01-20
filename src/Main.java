import Task.*;
import Manager.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task_1", "Тренироваться");
        Task task2 = new Task("Task_2", "Заниматься");
        int task1Id = manager.addTask(task1);
        int task2Id = manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Работать");
        int epic1Id = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Сходить на встречу", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Написать отчет",epic1.getId());
        int subtask1Id = manager.addSubtask(subtask1);
        int subtask2Id = manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "Бассейн");
        int epic2Id = manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask3", "Плавать 1 час", epic2.getId());
        int subtask3Id = manager.addSubtask(subtask3);

        System.out.println("добавили задач");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));

        Task taskToUpdate = manager.getTaskById(task1Id);
        taskToUpdate.setStatusTask(TaskStatus.IN_PROGRESS);
        manager.updateTask(taskToUpdate);

        taskToUpdate = manager.getTaskById(task2Id);
        taskToUpdate.setStatusTask(TaskStatus.DONE);
        manager.updateTask(taskToUpdate);

        Subtask subtaskToUpdate = manager.getSubtaskById(subtask1Id);
        subtaskToUpdate.setStatusTask(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtaskToUpdate);

        Epic epicToUpdate = manager.getEpicById(epic1Id);
        epicToUpdate.setDescriptionTask("Не работать*");
        manager.updateEpic(epicToUpdate);

        System.out.println("обновили задачи");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));

        historyManager.add(task1);
        historyManager.add(epic2);

        System.out.println("отправили в историю");
        System.out.println(historyManager.getHistory());

        manager.removeTaskById(task1Id);
        manager.removeEpicById(epic2Id);
        manager.removeSubtaskById(subtask2Id);

        System.out.println("удалили задачи");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));

        System.out.println(historyManager.getHistory());

        System.out.println("Поехали!");



    }
}
