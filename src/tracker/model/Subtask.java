package tracker.model;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
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
        Subtask subtaskCopy = new Subtask(this.getName(), this.getDescription(), this.epicId);
        subtaskCopy.setId(this.getId());
        subtaskCopy.setStatus(this.getStatus());

        return subtaskCopy;
    }
}