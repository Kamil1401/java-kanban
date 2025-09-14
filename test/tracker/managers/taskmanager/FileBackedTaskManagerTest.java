package tracker.managers.taskmanager;

import org.junit.jupiter.api.*;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;


    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = Files.createTempFile("Test", ".csv").toFile();
            return FileBackedTaskManager.getNewFailBackedTaskManager(file);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл для теста", e);
        }
    }

    @Test
    public void save_mustSaveDataToAFile() {
        Task task = new Task("Name", "Description",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addTask(task);

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
        Task task = new Task("Name", "Description",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addTask(task);
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNotNull(taskManager.getTask(task.getId()));
        Assertions.assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    public void loadFromFile_shouldThrowManagerSaveException_whenFileDoesNotExist() {
        File testFile = new File("test_file", ".csv");

        ManagerSaveException exception = Assertions.assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(testFile)
        );
        Assertions.assertEquals("Ошибка при попытке восстановления данных.", exception.getMessage());
    }
}