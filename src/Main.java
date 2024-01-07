import Task.*;
import Manager.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Task_1", "Тренироваться");
        Task task2 = new Task("Task_2", "Заниматься");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Работать");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Сходить на встречу", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Написать отчет",epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "Бассейн");
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask3", "Плавать 1 час", epic2.getId());
        manager.addSubtask(subtask3);

        System.out.println("добавили задач");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));




        manager.updateTask(new Task(task1.getId(),"Task_1", "Тренироваться",TaskStatus.IN_PROGRESS));
        manager.updateTask(new Task(task2.getId(),"Task_2", "Заниматься",TaskStatus.DONE));
        manager.updateSubtask(new Subtask(subtask1.getId(),"Subtask1", "Сходить на встречу", TaskStatus.IN_PROGRESS, subtask1.getEpicId()));
        manager.updateSubtask(new Subtask(subtask3.getId(),"Subtask3", "Плавать 1 час", TaskStatus.DONE, subtask3.getEpicId()));
        manager.updateEpic(new Epic(epic1.getId(), "Epic1", "не работать",epic1.getSubtaskIds()));

        System.out.println("обновили задачи");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));

        manager.removeTaskById(task1.getId());
        manager.removeEpicById(epic2.getId());
        manager.removeSubtaskById(subtask2.getId());

        System.out.println("удалили задачи");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllSubtasksOfEpic(epic1));




        System.out.println("Поехали!");
    }
}
