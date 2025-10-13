package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.status = Status.NEW;
        this.type = TaskType.SUBTASK;
    }

    public Subtask() {
        super(null, null, null, null);
    }


    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public void setId(Integer id) {
        if (id.equals(epicId)) {
            System.out.println("Ошибка. Подзадача не может являться собственным Эпиком.");
            return;
        }
        super.setId(id);
    }

    public Subtask copy() {
        Subtask subtaskCopy = new Subtask(this.getName(), this.getDescription(), this.epicId,
                this.getStartTime(), this.getDuration());
        subtaskCopy.setId(this.getId());
        subtaskCopy.setStatus(this.getStatus());

        return subtaskCopy;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", epicId=" + epicId +
                ", status=" + this.getStatus() +
                ", type=" + type +
                '}';
    }
}