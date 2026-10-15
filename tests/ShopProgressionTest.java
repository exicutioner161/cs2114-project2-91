import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import shop.ShopProgression;

class ShopProgressionTest {
    @Test
    void storesAndRetrievesLevelSpecificShops() {
        ShopProgression progression = new ShopProgression(2, new Random(4));
        assertEquals(3, progression.size());
        assertNotNull(progression.getShop(0));
        assertNotNull(progression.getShop(2));
        assertEquals(2, progression.getShop(2).size());
    }

    @Test
    void rejectsInvalidLevelsAndRandomSources() {
        assertThrows(IllegalArgumentException.class,
            () -> new ShopProgression(-1));
        assertThrows(IllegalArgumentException.class,
            () -> new ShopProgression(1, null));
        ShopProgression progression = new ShopProgression(1, new Random(1));
        org.junit.jupiter.api.Assertions.assertNull(progression.getShop(-1));
        org.junit.jupiter.api.Assertions.assertNull(progression.getShop(2));
    }

    @Test
    void generatedStatsAndPricesScaleWithLevel() {
        ShopProgression progression = new ShopProgression(2, new Random(1));
        assertEquals(50, progression.getShop(0).findItem("Level 0 Sword").getPrice());
        assertEquals(80, progression.getShop(2).findItem("Level 2 Sword").getPrice());
    }
}
