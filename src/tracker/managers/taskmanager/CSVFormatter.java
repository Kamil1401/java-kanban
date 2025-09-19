package tracker.managers.taskmanager;

import tracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class CSVFormatter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");

    public static String getHeader() {
        return "Id, type, name, status, description, start, duration, end, epic\n";
    }

    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s", task.getId(), TaskType.TASK, task.getName(), task.getStatus(),
                task.getDescription(), task.getStartTime().format(formatter), task.getDuration().toMinutes(),
                task.getEndTime().format(formatter));
    }

    public static String toString(Epic epic) {
        String start = (epic.getStartTime() == null) ? "" : epic.getStartTime().format(formatter);
        String end = (epic.getEndTime() == null) ? "" : epic.getEndTime().format(formatter);

        return String.format("%d,%s,%s,%s,%s,%s,%d,%s", epic.getId(), TaskType.EPIC, epic.getName(), epic.getStatus(),
                epic.getDescription(), start, epic.getDuration().toMinutes(), end);
    }

    public static String toString(Subtask subtask) {

        return String.format("%d,%s,%s,%s,%s,%s,%d,%s,%d", subtask.getId(), TaskType.SUBTASK, subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getStartTime().format(formatter),
                subtask.getDuration().toMinutes(), subtask.getEndTime().format(formatter), subtask.getEpicId());
    }

    public static Task fromString(String str) {
        String[] fields = str.split(",");

        Integer id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = (fields[5].isBlank()) ? null : LocalDateTime.parse(fields[5], formatter);
        Duration duration = Duration.ofMinutes(Integer.parseInt(fields[6]));
        LocalDateTime endTime = (fields[7].isBlank()) ? null : LocalDateTime.parse(fields[7], formatter);


        switch (type) {
            case SUBTASK:
                Integer epicId = Integer.parseInt(fields[8]);
                Subtask subtask = new Subtask(name, description, epicId, startTime, duration);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;

            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
                return epic;

            case TASK:
                Task task = new Task(name, description, startTime, duration);
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