package tracker.managers.taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void isOverlap_returnsTrue_ifTwoTasksOverlap() {
        Task task1 = new Task("Task1", "Description 1",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(120));
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Description 2",
                LocalDateTime.of(1958, 8, 29, 14, 30), Duration.ofMinutes(60));
        taskManager.addTask(task2);

        Assertions.assertTrue(taskManager.isOverlap(task1, task2));
    }
}