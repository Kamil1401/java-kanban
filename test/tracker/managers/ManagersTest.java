package tracker.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.managers.historymanager.HistoryManager;
import tracker.managers.historymanager.InMemoryHistoryManager;
import tracker.managers.taskmanager.FileBackedTaskManager;
import tracker.managers.taskmanager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class ManagersTest {

    @Test
    public void getDefault_returnsTrue_getInstanceOfInMemoryTaskManager() throws IOException {
        File file = Files.createTempFile("Test1", ".csv").toFile();
        TaskManager taskManager = Managers.getDefault();

        Assertions.assertNotNull(taskManager);
        Assertions.assertInstanceOf(taskManager.getClass(),
                FileBackedTaskManager.getNewFailBackedTaskManager(file));
    }

    @Test
    public void getDefaultHistory_returnsTrue_getInstanceOfInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, historyManager);
        Assertions.assertNotNull(historyManager);
    }
}