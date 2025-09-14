package tracker.managers.historymanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add_shouldAddToHistory() {
        Task task = new Task("Name", "Description",
                LocalDateTime.of(2025, 8, 29, 14, 0), Duration.ofMinutes(60));
        task.setId(1203);
        historyManager.add(task);

        Assertions.assertTrue(historyManager.getHistory().contains(task));
    }

    @Test
    public void add_addTheTaskToTheEndOfTheList() {
        InMemoryHistoryManager history = new InMemoryHistoryManager();
        Task task = new Task("Name 1", "Task",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(60));
        task.setId(427);
        history.add(task);

        Epic epic = new Epic("Name 2", "Epic");
        epic.setId(538);
        history.add(epic);

        Assertions.assertEquals(epic, history.getLast());
    }

    @Test
    public void remove_mustDeleteTheTaskFromTheHistory() {
        Task task = new Task("Name 1", "Task",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(60));
        task.setId(384);
        Epic epic = new Epic("Name 2", "Epic");
        epic.setId(618);
        historyManager.add(task);
        historyManager.add(epic);
        Assertions.assertEquals(2, historyManager.getHistory().size());

        historyManager.remove(task.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(epic, historyManager.getHistory().getFirst());
    }

    @Test
    public void add_mustSavePreviousData_changingDataAfterSavingItInTheHistory() {
        Task task = new Task("Name", "Description",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(60));
        historyManager.add(task);
        task.setStatus(Status.DONE);
        int index = historyManager.getHistory().indexOf(task);

        Assertions.assertEquals(Status.NEW, historyManager.getHistory().get(index).getStatus());
    }
}