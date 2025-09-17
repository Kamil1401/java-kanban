package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private Integer id;
    private Status status;
    protected TaskType type;
    private LocalDateTime startTime;
    private Duration duration;


    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = Status.NEW;
        this.type = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void deleteId() {
        this.id = null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return this.type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (this.startTime == null || this.duration == null) {
            throw new IllegalArgumentException("Значения полей 'startTime' и 'duration' не могут быть пустыми");
        }
         return this.startTime.plus(this.duration);
    }

    public Task copy() {
        Task taskCopy = new Task(this.name, this.description, this.startTime, this.duration);
        taskCopy.setId(this.id);
        taskCopy.setStatus(this.status);


        return taskCopy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) && Objects.equals(description, otherTask.description)
                && Objects.equals(status, otherTask.status) || Objects.equals(id, otherTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Task {" +
                "name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", id = " + id +
                ", status = " + status +
                ", type = " + type +
                '}';
    }
}