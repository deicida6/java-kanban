import Task.*;
import Manager.*;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File file = new File("save.csv");
        TaskManager manager = new FileBackedTaskManager(file);
        // дополнительное задание Спринт 7

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

        TaskManager manager1 = new FileBackedTaskManager(file);

        System.out.println("*****************************");
        System.out.println("Создали новый экземпляр таск менеджера используя файл");
        System.out.println("*****************************");
        System.out.println(manager1.getAllTasks());
        System.out.println(manager1.getAllEpic());
        System.out.println(manager1.getAllSubtasks());
    }
}
