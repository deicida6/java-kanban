import Task.*;
import Manager.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        // дополнительное задание Спринт 6
        Task task1 = new Task("Task_1", "Тренироваться");
        Task task2 = new Task("Task_2", "Заниматься");
        int task1Id = manager.addTask(task1);
        int task2Id = manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Работать");
        int epic1Id = manager.addEpic(epic1);
        Epic epic2 = new Epic("Epic2", "Бассейн");
        int epic2Id = manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1", "Сходить на встречу", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Написать отчет",epic1.getId());
        Subtask subtask3 = new Subtask("Subtask3", "отправить руководителю отчет",epic1.getId());
        int subtask1Id = manager.addSubtask(subtask1);
        int subtask2Id = manager.addSubtask(subtask2);
        int subtask3Id = manager.addSubtask(subtask3);

        System.out.println("*****************************");
        System.out.println("добавили задач");
        System.out.println("*****************************");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));
        System.out.println(manager.getAllSubtasksOfEpic(epic2));

        System.out.println("*****************************");
        System.out.println("по разному посмотрели задачи в истории 1");
        System.out.println("*****************************");
        manager.getTaskById(task1Id);
        manager.getTaskById(task2Id);
        manager.getEpicById(epic1Id);
        manager.getEpicById(epic2Id);
        System.out.println(manager.getHistory());

        System.out.println("*****************************");
        System.out.println("по разному посмотрели задачи в истории 2");
        System.out.println("*****************************");
        manager.getSubtaskById(subtask1Id);
        manager.getSubtaskById(subtask2Id);
        manager.getSubtaskById(subtask3Id);
        manager.getTaskById(task2Id);
        System.out.println(manager.getHistory());

        System.out.println("*****************************");
        System.out.println("удалили задачи Таск2, Эпик1 с подзадачами");
        System.out.println("*****************************");
        manager.removeTaskById(task2Id);
        manager.removeEpicById(epic1Id);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));
        System.out.println("*****************************");
        System.out.println("Проверили удаление в истории");
        System.out.println("*****************************");
        System.out.println(manager.getHistory());
    }
}
