package tracker.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        subtaskIds = new ArrayList<>();
    }

    public void addSubtaskId(Integer subtaskId) {
        if (subtaskId.equals(this.getId())) {
            System.out.println("Эпик не может быть добавлен в список собственных подзадач");
            return;
        }
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public Epic copy() {
        Epic epicCopy = new Epic(this.getName(), this.getDescription());
        epicCopy.setId(this.getId());
        epicCopy.setStatus(this.getStatus());
        epicCopy.subtaskIds = new ArrayList<>(subtaskIds);

        return epicCopy;
    }
}