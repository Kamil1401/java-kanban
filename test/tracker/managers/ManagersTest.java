package tracker.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.managers.historymanager.HistoryManager;
import tracker.managers.historymanager.InMemoryHistoryManager;
import tracker.managers.taskmanager.InMemoryTaskManager;
import tracker.managers.taskmanager.TaskManager;

class ManagersTest {

    @Test
    public void getDefault_returnsTrue_getInstanceOfInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertInstanceOf(InMemoryTaskManager.class, taskManager);
        Assertions.assertNotNull(taskManager);
    }

    @Test
    public void getDefaultHistory_returnsTrue_getInstanceOfInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, historyManager);
        Assertions.assertNotNull(historyManager);
    }
}