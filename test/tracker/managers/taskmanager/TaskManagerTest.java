package tracker.managers.taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @Test
    public void addTask_shouldAddTaskInMap_taskHasAnId() {
        Task task = new Task("Отдых", "Посмотреть сериал вечером",
                LocalDateTime.of(1958, 8, 29, 20, 0), Duration.ofMinutes(420));
        taskManager.addTask(task);

        Assertions.assertTrue(taskManager.getTasks().contains(task));
    }

    @Test
    public void addEpic_shouldAddTaskInMap_epicHasAnId() {
        Epic epic = new Epic("Чтение", "Прочесть все книги А. Конандойла о Шерлоке Холмсе");
        taskManager.addEpic(epic);

        Assertions.assertTrue(taskManager.getEpics().contains(epic));
    }

    @Test
    public void addSubtask_shouldAddTaskInMap_subtaskHasAnId() {
        Epic epic = new Epic("Чтение", "Прочесть все книги А. Конандойла о Шерлоке Холмсе");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("1я книга", "Этюд в багровых тонах", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask);

        Assertions.assertTrue(taskManager.getSubtasks().contains(subtask));
    }

    @Test
    public void getTask_returnsTaskById() {
        Task task = new Task("Отдых", "Посмотреть сериал вечером",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addTask(task);

        Assertions.assertNotNull(taskManager.getTask(task.getId()));
        Assertions.assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    public void getEpic_returnsEpicById() {
        Epic epic = new Epic("Чтение", "Прочесть все книги А. Конандойла о Шерлоке Холмсе");
        taskManager.addEpic(epic);

        Assertions.assertNotNull(taskManager.getEpic(epic.getId()));
        Assertions.assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    public void getSubtask_returnsSubtaskById() {
        Epic epic = new Epic("Чтение", "Прочесть все книги А. Конандойла о Шерлоке Холмсе");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("1я книга", "Этюд в багровых тонах", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask);

        Assertions.assertNotNull(taskManager.getSubtask(subtask.getId()));
        Assertions.assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    public void addTask_mustAddTaskInMap_generateIdNotSameWithSpecifiedId() {
        Task task_1 = new Task("Сдать ФЗ-8", "Написать последний тест",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        task_1.setId(1);
        taskManager.addTask(task_1);
        Task task_2 = new Task("Стихотворение", "Сочинить",
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addTask(task_2);

        Assertions.assertNotEquals(task_1.getId(), task_2.getId());
    }

    @Test
    public void removeSubtask_returnsNull_theSubtaskIdIsNullAfterDeletion() {
        Epic epic = new Epic("Epic", "EpicDescription");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "SubtaskDescription", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask);
        Assertions.assertNotNull(subtask.getId());

        taskManager.removeSubtask(subtask.getId());
        Assertions.assertNull(subtask.getId());
    }

    @Test
    public void removeSubtask_theSubtaskIdHasBeenRemovedFromTheListOfSubtasksOfItsEpic() {
        Epic epic = new Epic("Epic", "EpicDescription");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "SubtaskDescription", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask);
        Integer id = subtask.getId();
        Assertions.assertTrue(epic.getSubtaskIds().contains(id));

        taskManager.removeSubtask(subtask.getId());
        Assertions.assertFalse(epic.getSubtaskIds().contains(id));
    }

    @Test
    public void updateEpicStatus_setTheEpicStatusToNew_allSubtasksHaveTheNewStatus() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        epic.setStatus(Status.DONE);
        Subtask subtask1 = new Subtask("Subtask", "Description", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask", "Description", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 15, 30), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask2);

        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void updateEpicStatus_setTheEpicStatusToDone_allSubtasksHaveTheDoneStatus() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "Description", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        Subtask subtask2 = new Subtask("Subtask", "Description", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 15, 30), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateEpic(epic);

        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void updateEpicStatus_setTheEpicStatusToInProgress_allSubtasksHaveTheDifferentOrInProgressStatus() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask", "Description", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 14, 0), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask1);
        subtask1.setStatus(Status.NEW);

        Subtask subtask2 = new Subtask("Subtask", "Description", epic.getId(),
                LocalDateTime.of(1958, 8, 29, 15, 30), Duration.ofMinutes(90));
        taskManager.addSubtask(subtask2);
        subtask2.setStatus(Status.DONE);

        taskManager.updateEpic(epic);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
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