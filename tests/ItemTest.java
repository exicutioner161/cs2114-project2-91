import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import characters.Item;

class ItemTest {
    @Test
    void storesValidValues() {
        Item item = new Item("Sword", 10, 20, 30, 100, 4);
        assertEquals("Sword", item.getName());
        assertEquals(10, item.getAggro());
        assertEquals(20, item.getControl());
        assertEquals(30, item.getMidrange());
        assertEquals(100, item.getPrice());
        assertEquals(4, item.getType());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class,
            () -> new Item("", 1, 1, 1, 10, 0));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("Sword", 1, 1, 1, -1, 0));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("Sword", 1, 1, 1, 10, 5));
    }
}
