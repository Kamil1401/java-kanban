package tracker.model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;
    protected TaskType type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = Status.NEW;
        this.type = TaskType.TASK;
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

    public Task copy() {
        Task taskCopy = new Task(this.name, this.description);
        taskCopy.setId(this.id);
        taskCopy.setStatus(this.status);

        return taskCopy;
    }

    public TaskType getType() {
        return this.type;
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
                '}';
    }
}