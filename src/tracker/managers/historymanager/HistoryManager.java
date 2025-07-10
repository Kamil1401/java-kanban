package tracker.managers.historymanager;

import tracker.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task instance);

    List<Task> getHistory();

    void remove(Integer id);
}