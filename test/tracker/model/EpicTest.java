package tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    public void addSubtaskId_notAddId_idSameWithEpicId() {
        Epic epic = new Epic("Бассейн", "Построить бассейн и зону отдыха на заднем дворе");
        epic.setId(55);
        epic.addSubtaskId(epic.getId());

        Assertions.assertFalse(epic.getSubtaskIds().contains(epic.getId()));
    }

    @Test
    public void equals_returnsTrue_theIdsOfTheHeirsAreEqual() {
        Epic epic1 = new Epic("День рождения", "Организовать ДР");
        Epic epic2 = new Epic("Путешествие", "Съездить в Европу");
        epic1.setId(99);
        epic2.setId(epic1.getId());

        Assertions.assertEquals(epic1.getId(), epic2.getId());
        Assertions.assertEquals(epic1, epic2);
    }
}