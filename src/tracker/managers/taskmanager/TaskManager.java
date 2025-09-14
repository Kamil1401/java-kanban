package tracker.managers.taskmanager;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    <T extends Task> void addToSetOfPrioritizedTasks(T task);

    <T extends Task> boolean isOverlap(T task1, T task2);


    // TASK METHODS
    ArrayList<Task> getTasks();

    void clearTasks();

    Task getTask(Integer id);

    void addTask(Task task);

    Task updateTask(Task task);

    void removeTask(Integer id);

    // EPIC METHODS
    ArrayList<Epic> getEpics();

    void clearEpics();

    Epic getEpic(Integer id);

    void addEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void removeEpic(Integer id);

    // SUBTASKS METHODS
    ArrayList<Subtask> getSubtasks();

    void clearSubtasks();

    Subtask getSubtask(Integer id);

    void addSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    void removeSubtask(Integer id);

    ArrayList<Subtask> getSubtasksForEpic(Epic epic);
}