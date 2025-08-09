package tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    public void equals_returnsTrue_idsAreSame() {
        Task task1 = new Task("Уборка", "Помыть посуду и пропылесосить");
        Task task2 = new Task("Ремонт", "Починить дверь шкафа");
        task1.setId(88);
        task2.setId(task1.getId());

        Assertions.assertEquals(task1.getId(), task2.getId());
        Assertions.assertEquals(task1, task2);
    }
}