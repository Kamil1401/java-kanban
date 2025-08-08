package tracker.managers.taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Task;

import java.io.*;
import java.nio.file.Files;

class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    File file = new File("C:\\Users\\User\\IdeaProjects\\Final_Work_6\\java-kanban", "Test.csv");

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file);
    }

    @Test
    public void save_mustSaveDataToAFile() {
        Task task = new Task("Name", "Description");
        manager.addTask(task);

        String str;
        try {
            str = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] parts = str.split("\n");
        String[] fields = parts[1].split(",");
        Integer idOfTask = Integer.parseInt(fields[0]);

        Assertions.assertEquals(idOfTask, task.getId());
        Assertions.assertEquals(fields[2], task.getName());
    }

    @Test
    public void loadFromFile_mustRecoverDataFromAFile() {
        Task task = new Task("Name", "Description");
        manager.addTask(task);
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNotNull(taskManager.getTask(task.getId()));
        Assertions.assertEquals(task, taskManager.getTask(task.getId()));
    }

}