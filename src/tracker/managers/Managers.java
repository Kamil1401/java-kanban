package tracker.managers;

import tracker.managers.historymanager.HistoryManager;
import tracker.managers.historymanager.InMemoryHistoryManager;
import tracker.managers.taskmanager.FileBackedTaskManager;
import tracker.managers.taskmanager.TaskManager;

import java.io.File;

public class Managers {
     static File file = new File("C:\\Users\\User\\IdeaProjects\\Final_Work_6\\java-kanban",
             "Work.csv");

    public static TaskManager getDefault() {
        return FileBackedTaskManager.getNewFailBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}