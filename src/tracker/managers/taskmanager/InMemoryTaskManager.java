package tracker.managers.taskmanager;

import tracker.managers.Managers;
import tracker.managers.historymanager.HistoryManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private int idSequence = 0;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private int generateId() {
        return ++idSequence;
    }

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    // TASK METHODS
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void removeTask(Integer id) {
        Task task = tasks.remove(id);
        historyManager.remove(task.getId());
    }

    // EPIC METHODS
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
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
            subtasks.remove(subtaskId);
        }
        epic.getSubtaskIds().clear();
        for (Task task : historyManager.getHistory()) {
            if (epic.getId().equals(task.getId())) {
                historyManager.remove(task.getId());
            }
        }
        epics.remove(id);
    }

    // SUBTASKS METHODS
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubtask(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());

        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        if (epicOfSubtask == null || !epics.containsKey(epicOfSubtask.getId())) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        epicOfSubtask.addSubtaskId(subtask.getId());
        updateEpicStatus(epicOfSubtask);
    }


    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epicOfSubtask);
        return subtask;
    }

    @Override
    public void removeSubtask(Integer id) {

        Subtask subtask = subtasks.get(id);
        Epic epicOfSubtask = epics.get(subtask.getEpicId());
        epicOfSubtask.getSubtaskIds().remove(id);
        for (Task task : historyManager.getHistory()) {
            if (subtask.getId().equals(task.getId())) {
                historyManager.remove(task.getId());
            }
        }
        subtasks.remove(id);
        subtask.clearId();
        updateEpicStatus(epicOfSubtask);
    }


    @Override
    public ArrayList<Subtask> getSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            listOfSubtasks.add(subtasks.get(subtaskId));
        }
        return listOfSubtasks;
    }

    private void updateEpicStatus(Epic epic) {
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
}