package tracker;

import tracker.managers.Managers;
import tracker.managers.taskmanager.FileBackedTaskManager;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        File file = new File("C:\\Users\\User\\IdeaProjects\\Final_Work_6\\java-kanban","Work.csv");

        Task task1 = new Task("Task1", "Task1");
        Task task2 = new Task("Task2", "Task2");
        Task task3 = new Task("Task3", "Task3");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        Epic epic1 = new Epic("Epic1", "Epic1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1", epic1.getId());
        manager.addSubtask(subtask1);

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());

        TaskManager manager1 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(manager1.getHistory());
    }
}