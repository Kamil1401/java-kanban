package tracker.managers.taskmanager;

import tracker.managers.Managers;
import tracker.managers.historymanager.HistoryManager;
import tracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected <T extends Task> boolean hasOverlap(T task1, T task2) {
        if (task1.getId().equals(task2.getId())) {
            return false;
        }
        if (task1.getStartTime() == null || task2.getStartTime() == null
                || task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime())
                && task2.getStartTime().isBefore(task1.getEndTime());
    }

    protected <T extends Task> void hasOverlap(T task) {
        if (getPrioritizedTasks().stream()
                .anyMatch(taskItem -> hasOverlap(task, taskItem))) {
            throw new IllegalArgumentException("Задача пересекается с уже существующей");
        }
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
        task.setId(generateId());
        hasOverlap(task);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Нельзя заменить на задачу со значением null");
        }
        hasOverlap(task);
        prioritizedTasks.remove(task);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public void removeTask(Integer id) {
        Task task = tasks.get(id);
        prioritizedTasks.remove(task);
        historyManager.remove(task.getId());
        tasks.remove(id);
    }

    // EPIC METHODS
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        prioritizedTasks.removeAll(subtasks.values());
        getEpics().forEach(epic -> epic.getSubtaskIds().clear());
        subtasks.clear();
        epics.clear();
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
        epics.put(epic.getId(), epic);
        updateStatusAndTimeOfEpic(epic);
        return epic;
    }

    @Override
    public void removeEpic(Integer id) {
        Epic epic = epics.get(id);
        epic.getSubtaskIds().forEach(subtaskId -> {
            historyManager.getHistory().forEach(historyItem -> {
                if (subtaskId.equals(historyItem.getId())) {
                    historyManager.remove(historyItem.getId());
                }
            });
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        });
        epic.getSubtaskIds().clear();
        historyManager.remove(epic.getId());
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
            updateStatusAndTimeOfEpic(epic);
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
        subtask.setId(generateId());
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        if (epicOfSubtask == null || !epics.containsKey(epicOfSubtask.getId())) {
            return;
        }
        hasOverlap(subtask);
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        epicOfSubtask.addSubtaskId(subtask.getId());
        updateStatusAndTimeOfEpic(epicOfSubtask);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Нельзя заменить на задачу со значением null");
        }
        prioritizedTasks.remove(subtask);
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        updateStatusAndTimeOfEpic(epicOfSubtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask;
    }

    @Override
    public void removeSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        epicOfSubtask.getSubtaskIds().remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        subtask.deleteId();
        updateStatusAndTimeOfEpic(epicOfSubtask);
    }


    @Override
    public ArrayList<Subtask> getSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        epic.getSubtaskIds().forEach(subtaskId -> listOfSubtasks.add(subtasks.get(subtaskId)));

        return listOfSubtasks;
    }

    protected void updateEpicStatus(Epic epic) {
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

    protected void updateEpicTime(Epic epic) {
        List<Subtask> subtaskList = getSubtasksForEpic(epic);
        epic.setStartTime(getEpicStartTime(subtaskList));
        epic.setDuration(getEpicDuration(subtaskList));
        epic.setEndTime(getEpicEndTime(subtaskList));
    }

    private LocalDateTime getEpicStartTime(List<Subtask> subtaskList) {
        return subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private Duration getEpicDuration(List<Subtask> subtaskList) {
        return subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private LocalDateTime getEpicEndTime(List<Subtask> subtaskList) {
        return subtaskList.stream()
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    protected void updateStatusAndTimeOfEpic(Epic epic) {
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }
}