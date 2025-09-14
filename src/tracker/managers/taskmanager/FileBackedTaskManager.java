package tracker.managers.taskmanager;

import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }


    public static FileBackedTaskManager getNewFailBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(CSVFormatter.getHeader());

            for (Task task : getTasks()) {
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(CSVFormatter.toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(CSVFormatter.toString(subtask));
                writer.newLine();
            }
            writer.newLine();

            writer.write(CSVFormatter.historyToString(historyManager.getHistory()));
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при попытке сохранения.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        List<String> storage = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                storage.add(line);
            }

            int i = 1;
            while (i < storage.size() && !storage.get(i).isBlank()) {
                Task task = CSVFormatter.fromString(storage.get(i));

                switch (task.getType()) {
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        fileBackedTaskManager.epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                        if (fileBackedTaskManager.epics.containsKey(subtask.getEpicId())) {
                            Epic epicOfSubtask = fileBackedTaskManager.epics.get(subtask.getEpicId());
                            epicOfSubtask.addSubtaskId(subtask.getId());
                            fileBackedTaskManager.updateEpicStatus(epicOfSubtask);
                            fileBackedTaskManager.updateEpicTime(epicOfSubtask);
                        }
                        break;
                }
                if (fileBackedTaskManager.idSequence < task.getId()) {
                    fileBackedTaskManager.idSequence = task.getId();
                }
                i++;
            }

            if (i + 1 < storage.size()) {
                List<Integer> history = CSVFormatter.historyFromString(storage.get(i + 1));
                for (Integer id : history) {
                    if (fileBackedTaskManager.tasks.containsKey(id)) {
                        fileBackedTaskManager.historyManager.add(fileBackedTaskManager.tasks.get(id));
                    } else if (fileBackedTaskManager.epics.containsKey(id)) {
                        fileBackedTaskManager.historyManager.add(fileBackedTaskManager.epics.get(id));
                    } else if (fileBackedTaskManager.subtasks.containsKey(id)) {
                        fileBackedTaskManager.historyManager.add(fileBackedTaskManager.subtasks.get(id));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при попытке восстановления данных.");
        }
        return fileBackedTaskManager;
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateEpicTime(Epic epic) {
        super.updateEpicTime(epic);
        save();
    }
}