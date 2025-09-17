package tracker.managers.taskmanager;

import tracker.managers.Managers;
import tracker.managers.historymanager.HistoryManager;
import tracker.model.*;

import java.time.Duration;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected int idSequence = 0;
    protected HistoryManager historyManager;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(new TaskComparator());

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    private int generateId() {
        return ++idSequence;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected <T extends Task> void addToSetOfPrioritizedTasks(T task) {
        if (task.getStartTime() == null) {
            throw new IllegalArgumentException("Приоритет выставляется по времени старта задачи. " +
                    "Он не может являться пустым значением.");
        }
        if (hasOverlap(task)) {
            return;
        }
        if (prioritizedTasks.contains(task)) {
            return;
        }
        prioritizedTasks.add(task);
    }

    protected <T extends Task> boolean isOverlap(T task1, T task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null
                || task1.getDuration() == null || task2.getDuration() == null) {
            throw new IllegalArgumentException("Значения полей 'startTime' и 'duration' не могут быть пустыми.");
        }
        return task1.getStartTime().isBefore(task2.getEndTime())
                && task2.getStartTime().isBefore(task1.getEndTime());
    }

    private <T extends Task> boolean hasOverlap(T task) {
        return getPrioritizedTasks().stream()
                .anyMatch(taskItem -> isOverlap(task, taskItem));
    }


    // TASK METHODS
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Нельзя добавить задачу со значением null");
        }
        if (task.getStartTime() == null || task.getDuration() == null) {
            throw new IllegalArgumentException("Значения полей 'startTime' и 'duration' не могут быть пустыми");
        }
        task.setId(generateId());
        if (hasOverlap(task)) {
            return;
        }
        tasks.put(task.getId(), task);
        addToSetOfPrioritizedTasks(task);
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Нельзя заменить на задачу со значением null");
        }
        if (task.getStartTime() == null || task.getDuration() == null) {
            throw new IllegalArgumentException("Значения полей 'startTime' и 'duration' не могут быть пустыми");
        }
        if (hasOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается с другой задачей");
        }
        prioritizedTasks.remove(task);
        tasks.put(task.getId(), task);
        addToSetOfPrioritizedTasks(task);
        return task;
    }

    @Override
    public void removeTask(Integer id) {
        Task task = tasks.remove(id);
        historyManager.remove(task.getId());
        prioritizedTasks.remove(task);
    }

    // EPIC METHODS
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        prioritizedTasks.removeAll(epics.values());
        prioritizedTasks.removeAll(subtasks.values());
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Нельзя добавить задачу со значением null");
        }
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Нельзя заменить на задачу со значением null");
        }
        prioritizedTasks.remove(epic);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        addToSetOfPrioritizedTasks(epic);
        return epic;
    }

    @Override
    public void removeEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            for (Task task : historyManager.getHistory()) {
                if (subtaskId.equals(task.getId())) {
                    historyManager.remove(task.getId());
                }
            }
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        }
        epic.getSubtaskIds().clear();
        historyManager.getHistory().forEach(task -> {
            if (epic.getId().equals(task.getId())) {
                historyManager.remove(task.getId());
            }
        });
        prioritizedTasks.remove(epic);
        epics.remove(id);
    }

    // SUBTASKS METHODS
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
            if (epic.getStartTime() == null) {
                prioritizedTasks.remove(epic);
            }
        });
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Нельзя добавить задачу со значением null");
        }
        if (subtask.getStartTime() == null || subtask.getDuration() == null) {
            throw new IllegalArgumentException("Значения полей 'startTime' и 'duration' не могут быть пустыми");
        }
        subtask.setId(generateId());

        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        if (epicOfSubtask == null || !epics.containsKey(epicOfSubtask.getId())) {
            return;
        }
        if (hasOverlap(subtask)) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        addToSetOfPrioritizedTasks(subtask);
        epicOfSubtask.addSubtaskId(subtask.getId());
        updateEpicStatus(epicOfSubtask);
        updateEpicTime(epicOfSubtask);

        addToSetOfPrioritizedTasks(epicOfSubtask);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Нельзя заменить на задачу со значением null");
        }
        if (subtask.getStartTime() == null || subtask.getDuration() == null) {
            throw new IllegalArgumentException("Значения полей 'startTime' и 'duration' не могут быть пустыми");
        }
        if (hasOverlap(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается с другой задачей");
        }
        prioritizedTasks.remove(subtask);
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        addToSetOfPrioritizedTasks(subtask);
        updateEpicStatus(epicOfSubtask);
        updateEpicTime(epicOfSubtask);
        return subtask;
    }

    @Override
    public void removeSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        epicOfSubtask.getSubtaskIds().remove(id);
        historyManager.getHistory().forEach(task -> {
            if (subtask.getId().equals(task.getId())) {
                historyManager.remove(task.getId());
            }
        });
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        subtask.deleteId();
        updateEpicStatus(epicOfSubtask);
        updateEpicTime(epicOfSubtask);
        if (epicOfSubtask.getStartTime() == null) {
            prioritizedTasks.remove(epicOfSubtask);
        }
    }


    @Override
    public ArrayList<Subtask> getSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        epic.getSubtaskIds().forEach(subtaskId -> listOfSubtasks.add(subtasks.get(subtaskId)));

        return listOfSubtasks;
    }

    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (!subtask.getStatus().equals(Status.NEW)) {
                allNew = false;
            }
            if (!subtask.getStatus().equals(Status.DONE)) {
                allDone = false;
            }
        }
        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateEpicTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }
        List<Subtask> listOfSubtasks = getSubtasksForEpic(epic);
        listOfSubtasks.stream()
                .sorted(new TaskComparator())
                .forEach(subtask -> {
                    if (subtask.getStartTime() == null || subtask.getDuration() == null) {
                        throw new IllegalArgumentException(
                                "Значения полей 'startTime' и 'duration' не могут быть пустыми");
                    }
                    epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
                });
        epic.setStartTime(listOfSubtasks.getFirst().getStartTime());
    }
}