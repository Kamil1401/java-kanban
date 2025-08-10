package tracker.managers.taskmanager;

import tracker.model.*;

import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {

    public static String getHeader() {
        return "Id, type, name, status, description, epic\n";
    }

    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s", task.getId(), TaskType.TASK, task.getName(), task.getStatus(),
                task.getDescription());
    }

    public static String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s", epic.getId(), TaskType.EPIC, epic.getName(), epic.getStatus(),
                epic.getDescription());
    }

    public static String toString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TaskType.SUBTASK, subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
    }

    public static Task fromString(String str) {
        String[] fields = str.split(",");

        Integer id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case SUBTASK:
                Integer epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;

            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;

            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;

            default:
                throw new IllegalArgumentException("Неподходящий тип для задачи " + type);
        }
    }

    public static String historyToString(List<Task> history) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            stringBuilder.append(history.get(i).getId());
            if (i < history.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(String str) {
        List<Integer> history = new ArrayList<>();
        if (str == null || str.isBlank()) {
            return history;
        }
        String[] parts = str.split(",");
        for (String part : parts) {
            history.add(Integer.parseInt(part));
        }
        return history;
    }
}