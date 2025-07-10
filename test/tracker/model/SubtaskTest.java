package tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    @Test
    public void setId_notSetId_idSameWithEpicId() {
        Subtask subtask = new Subtask("Отдых", "Скорее лечь спать", 555);
        subtask.setId(subtask.getEpicId());

        Assertions.assertNotEquals(subtask.getId(), subtask.getEpicId());
    }
}