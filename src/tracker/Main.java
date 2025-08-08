package tracker;

import tracker.managers.Managers;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Epic;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Name 1", "Task");
        manager.addTask(task1);
        Task task2 = new Task("Name 2", "Task");
        manager.addTask(task2);
        Epic epic1 = new Epic("Name 1", "Epic");
        manager.addEpic(epic1);
        System.out.println(task1.getId() + ", " + task2.getId() + ", " + epic1.getId());



    }
}