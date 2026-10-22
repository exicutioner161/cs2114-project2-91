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
        assertEquals(40, player.getGold());
        assertNull(player.getItem(4));
    }

    @Test
    void supportsPublicInventoryAndProgressionMethods() {
        Player player = new Player("Hero", 0, 0, new Item[5]);
        assertTrue(player.addItem(sword));
        assertEquals(sword, player.getItem(4));
        assertEquals(3, player.getAggro());
        assertEquals(1, player.getControl());
        assertEquals(0, player.getMidrange());
        assertEquals(5, player.getInventory().length);
        player.awardGold(10);
        player.advanceLevel();
        assertEquals(10, player.getGold());
        assertEquals(1, player.getLevel());
        assertTrue(player.catalogText().contains("Current Gold: 10"));
        assertEquals(sword, player.removeItem(4));
    }

    @Test
    void rejectsInvalidTransactionsWithoutChangingState() {
        Player player = new Player("Hero", 0, 10, new Item[5]);
        assertFalse(player.buy(sword));
        assertFalse(player.buy(null));
        assertFalse(player.addItem(null));
        assertTrue(player.addItem(new Item("Other", 1, 1, 1, 1, 4)));
        assertFalse(player.addItem(sword));
        assertEquals("Other", player.removeItem(4).getName());
        assertFalse(player.sell(4));
        assertNull(player.removeItem(-1));
        assertNull(player.getItem(5));
        assertThrows(IllegalArgumentException.class, () -> player.awardGold(-1));
        assertThrows(IllegalArgumentException.class,
            () -> new Player("Hero", 0, 1, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Player("Hero", 0, 1, new Item[2]));
    }

    @Test
    void toStringSummarizesProgressAndGear() {
        Player player = new Player("Hero", 1, 10, new Item[5]);
        player.addItem(new Item("Sword", 1, 1, 1, 5, 4));
        player.incrementWins();
        String text = player.toString();
        assertTrue(text.contains("Hero"));
        assertTrue(text.contains("Gold: 10"));
        assertTrue(text.contains("Wins: 1"));
        assertTrue(text.contains("Sword"));
    }
}
