import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import characters.Item;
import characters.Player;
import shop.Shop;

class ShopTest {
    @Test
    void storesDuplicateOffersAndSellsToPlayer() {
        Shop shop = new Shop(new Item[] {
            new Item("Sword", 3, 1, 0, 20, 4),
            new Item("Sword", 4, 0, 0, 30, 4)
        });
        Player player = new Player("Hero", 0, 25, new Item[5]);
        assertEquals(2, shop.size());
        assertTrue(shop.buy(player, "sword"));
        assertEquals(5, player.getGold());
        assertTrue(shop.removeItem("Sword"));
        assertEquals(1, shop.size());
    }

    @Test
    void rejectsInvalidShopActions() {
        Shop shop = new Shop(new Item[0]);
        assertFalse(shop.addItem(null));
        assertFalse(shop.removeItem("missing"));
        assertNull(shop.findItem(null));
        assertFalse(shop.buy(null, "missing"));
    }

    @Test
    void rejectsInvalidConstruction() {
        assertEquals(6, new Shop().size());
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> new Shop(null));
    }
}
