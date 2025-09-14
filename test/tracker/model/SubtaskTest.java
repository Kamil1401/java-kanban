package tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {

    @Test
    public void setId_notSetId_idSameWithEpicId() {
        Subtask subtask = new Subtask("Отдых", "Скорее лечь спать", 555,
                LocalDateTime.of(2025, 8, 24, 14, 0), Duration.ofMinutes(60));
        subtask.setId(subtask.getEpicId());

        Assertions.assertNotEquals(subtask.getId(), subtask.getEpicId());
    }
}