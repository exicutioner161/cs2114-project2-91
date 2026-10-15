import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import characters.Item;
import characters.Player;

class PlayerTest {
    private final Item sword = new Item("Sword", 3, 1, 0, 40, 4);

    @Test
    void buysSellsAndCalculatesEquipmentStats() {
        Player player = new Player("Hero", 0, 50, new Item[5]);
        assertTrue(player.buy(sword));
        assertEquals(10, player.getGold());
        assertEquals(3, player.getAggro());
        assertTrue(player.sell(4));
        assertEquals(30, player.getGold());
        assertNull(player.getItem(4));
    }

    @Test
    void rejectsInvalidTransactionsWithoutChangingState() {
        Player player = new Player("Hero", 0, 10, new Item[5]);
        assertFalse(player.buy(sword));
        assertFalse(player.buy(null));
        assertFalse(player.sell(4));
        assertNull(player.removeItem(-1));
        assertThrows(IllegalArgumentException.class, () -> player.awardGold(-1));
    }
}
