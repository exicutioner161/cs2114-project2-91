import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import shop.Shop;
import shop.ShopProgression;

class ShopProgressionTest {
    @Test
    void storesAndRetrievesLevelSpecificShops() {
        ShopProgression progression = new ShopProgression(4, new Random(4));
        assertEquals(4, progression.size());
        assertNotNull(progression.getShop(1));
        assertNotNull(progression.getShop(2));
        assertEquals(25, progression.getShop(2).size());
    }

    @Test
    void rejectsInvalidLevelsAndRandomSources() {
        assertThrows(IllegalArgumentException.class,
            () -> new ShopProgression(0));
        assertThrows(IllegalArgumentException.class,
            () -> new ShopProgression(1, null));
        ShopProgression progression = new ShopProgression(1, new Random(1));
        org.junit.jupiter.api.Assertions.assertNull(progression.getShop(0));
        org.junit.jupiter.api.Assertions.assertNull(progression.getShop(2));
    }

    @Test
    void generatedStatsAndPricesScaleWithLevel() {
        ShopProgression progression = new ShopProgression(2, new Random(1));
        assertTrue(progression.getShop(1)
            .findItem("Level 1 Blade of Dawn").getPrice() >= 25);
        assertTrue(progression.getShop(2)
            .findItem("Level 2 Blade of Dawn").getAggro()
            > progression.getShop(1)
            .findItem("Level 1 Blade of Dawn").getAggro());
    }

    @Test
    void createsFiveUniqueOffersForEachEquipmentSlot() {
        ShopProgression progression = new ShopProgression(4, new Random(8));
        String[] prefixes = {"Helm", "Guard", "Greaves", "Boots", "Blade"};
        for (int level = 1; level <= 4; level++) {
            Shop shop = progression.getShop(level);
            for (String prefix : prefixes) {
                for (String element : new String[] {
                        "Dawn", "Stone", "Embers", "Gale", "Tides"}) {
                    assertNotNull(shop.findItem(
                        "Level " + level + " " + prefix + " of " + element));
                }
            }
            assertEquals(25, shop.size());
        }
    }
}
