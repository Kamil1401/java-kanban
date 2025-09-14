package tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {

    @Test
    public void equals_returnsTrue_idsAreSame() {
        Task task1 = new Task("Уборка", "Помыть посуду и пропылесосить",
                LocalDateTime.of(2025, 8, 24, 14, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Ремонт", "Починить дверь шкафа",
                LocalDateTime.of(2025, 8, 25, 14, 0), Duration.ofMinutes(60));
        task1.setId(88);
        task2.setId(task1.getId());

        Assertions.assertEquals(task1.getId(), task2.getId());
        Assertions.assertEquals(task1, task2);
    }
}