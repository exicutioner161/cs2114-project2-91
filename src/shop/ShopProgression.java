package shop;

import java.util.Random;

import characters.Item;
import linkedlist.CustomLinkedList;

/** Generates and stores level-specific equipment catalogs. */
public class ShopProgression {
    private static final int FIRST_LEVEL = 1;
    private static final int ITEMS_PER_SLOT = 5;
    private static final String[][] ITEM_NAMES = {
            { "Helm of Dawn", "Helm of Stone", "Helm of Embers",
                    "Helm of Gale", "Helm of Tides" },
            { "Guard of Dawn", "Guard of Stone", "Guard of Embers",
                    "Guard of Gale", "Guard of Tides" },
            { "Greaves of Dawn", "Greaves of Stone", "Greaves of Embers",
                    "Greaves of Gale", "Greaves of Tides" },
            { "Boots of Dawn", "Boots of Stone", "Boots of Embers",
                    "Boots of Gale", "Boots of Tides" },
            { "Blade of Dawn", "Blade of Stone", "Blade of Embers",
                    "Blade of Gale", "Blade of Tides" }
    };

    private final CustomLinkedList<Shop> shops;

    /**
     * Creates shops from level one through the requested highest level.
     *
     * @param levelCount highest generated level
     * @param random     source for generated stats and prices
     * @throws IllegalArgumentException if arguments are invalid
     */
    public ShopProgression(int levelCount, Random random) {
        if (levelCount < FIRST_LEVEL || random == null) {
            throw new IllegalArgumentException("Shop progression values are invalid");
        }
        shops = new CustomLinkedList<>();
        for (int level = FIRST_LEVEL; level <= levelCount; level++) {
            shops.add(createShop(level, random));
        }
    }

    /**
     * Creates level shops using a new random source.
     *
     * @param highestLevel highest generated level
     */
    public ShopProgression(int highestLevel) {
        this(highestLevel, new Random());
    }

    /**
     * Returns the shop unlocked at a level.
     *
     * @param level requested player level
     * @return the level shop, or {@code null} if it is unavailable
     */
    public Shop getShop(int level) {
        if (level < FIRST_LEVEL || level > shops.size()) {
            return null;
        }
        return shops.get(level - FIRST_LEVEL);
    }

    /** @return the number of generated shops */
    public int size() {
        return shops.size();
    }

    /** Generates all offers for one level-specific shop. */
    private Shop createShop(int level, Random random) {
        Item[] items = new Item[ITEM_NAMES.length * ITEMS_PER_SLOT];
        int itemIndex = 0;
        for (int slot = 0; slot < ITEM_NAMES.length; slot++) {
            for (int nameIndex = 0; nameIndex < ITEMS_PER_SLOT; nameIndex++) {
                int strength = level * 3 + random.nextInt(4);
                int price = level * 25 + 25 + random.nextInt(16);
                items[itemIndex++] = new Item(
                        "Level " + level + " " + ITEM_NAMES[slot][nameIndex],
                        strength + random.nextInt(4),
                        strength + random.nextInt(4),
                        strength + random.nextInt(4),
                        price,
                        slot);
            }
        }
        return new Shop(items);
    }
}
