package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, null, Duration.ZERO);
        subtaskIds = new ArrayList<>();
        this.type = TaskType.EPIC;
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
        epicCopy.setStartTime(this.getStartTime());
        epicCopy.setDuration(this.getDuration());

        return epicCopy;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }
        this.endTime = this.getStartTime().plus(this.getDuration());
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}