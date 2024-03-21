import task.*;
import manager.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new InMemoryTaskManager();
        // дополнительное задание Спринт 7

        Task task1 = new Task("Task_1", "Тренироваться","1000","1000");
        Task task2 = new Task("Task_2", "Заниматься","1000","1000");
        int task1Id = manager.addTask(task1);
        int task2Id = manager.addTask(task2);

//        Epic epic1 = new Epic("Epic1", "Работать");
//        int epic1Id = manager.addEpic(epic1);
//        Epic epic2 = new Epic("Epic2", "Бассейн");
//        int epic2Id = manager.addEpic(epic2);
//
//        Subtask subtask1 = new Subtask("Subtask1", "Сходить на встречу", epic1.getId());
//        Subtask subtask2 = new Subtask("Subtask2", "Написать отчет",epic1.getId());
//        Subtask subtask3 = new Subtask("Subtask3", "отправить руководителю отчет",epic1.getId());
//        int subtask1Id = manager.addSubtask(subtask1);
//        int subtask2Id = manager.addSubtask(subtask2);
//        int subtask3Id = manager.addSubtask(subtask3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
//        System.out.println(manager.getAllSubtasksOfEpic(epic1));
//        System.out.println(manager.getAllSubtasksOfEpic(epic2));
    }
}
