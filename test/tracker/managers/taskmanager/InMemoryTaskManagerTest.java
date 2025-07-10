package tracker.managers.taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;


class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void addTask_shouldAddTaskInMap_taskHasAnId() {
        Task task = new Task("Отдых", "Посмотреть сериал вечером");
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

        Subtask subtask = new Subtask("1я книга", "Этюд в багровых тонах", epic.getId());
        taskManager.addSubtask(subtask);

        Assertions.assertTrue(taskManager.getSubtasks().contains(subtask));
    }

    @Test
    public void getTask_returnsTaskById() {
        Task task = new Task("Отдых", "Посмотреть сериал вечером");
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

        Subtask subtask = new Subtask("1я книга", "Этюд в багровых тонах", epic.getId());
        taskManager.addSubtask(subtask);

        Assertions.assertNotNull(taskManager.getSubtask(subtask.getId()));
        Assertions.assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    public void addTask_mustAddTaskInMap_generateIdNotSameWithSpecifiedId() {
        Task task_1 = new Task("Сдать ФЗ-5", "Написать последний тест");
        task_1.setId(1);
        taskManager.addTask(task_1);
        Task task_2 = new Task("Стихотворение", "Сочинить");
        taskManager.addTask(task_2);

        Assertions.assertNotEquals(task_1.getId(), task_2.getId());
    }

    @Test
    public void removeSubtask_returnsNull_theSubtaskIdIsNullAfterDeletion() {
        Epic epic = new Epic("Epic", "EpicDescription");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "SubtaskDescription", epic.getId());
        taskManager.addSubtask(subtask);
        Assertions.assertNotNull(subtask.getId());

        taskManager.removeSubtask(subtask.getId());
        Assertions.assertNull(subtask.getId());
    }

    @Test
    public void removeSubtask_theSubtaskIdHasBeenRemovedFromTheListOfSubtasksOfItsEpic() {
        Epic epic = new Epic("Epic", "EpicDescription");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "SubtaskDescription", epic.getId());
        taskManager.addSubtask(subtask);
        Integer id = subtask.getId();
        Assertions.assertTrue(epic.getSubtaskIds().contains(id));

        taskManager.removeSubtask(subtask.getId());
        Assertions.assertFalse(epic.getSubtaskIds().contains(id));
    }
}