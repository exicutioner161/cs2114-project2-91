package shop;

import java.util.Random;

import characters.Item;
import linkedlist.CustomLinkedList;

public class ShopProgression {
    private static final int FIRST_LEVEL = 1;
    private static final int ITEMS_PER_SLOT = 5;
    private static final String[][] ITEM_NAMES = {
        {"Helm of Dawn", "Helm of Stone", "Helm of Embers",
            "Helm of Gale", "Helm of Tides"},
        {"Guard of Dawn", "Guard of Stone", "Guard of Embers",
            "Guard of Gale", "Guard of Tides"},
        {"Greaves of Dawn", "Greaves of Stone", "Greaves of Embers",
            "Greaves of Gale", "Greaves of Tides"},
        {"Boots of Dawn", "Boots of Stone", "Boots of Embers",
            "Boots of Gale", "Boots of Tides"},
        {"Blade of Dawn", "Blade of Stone", "Blade of Embers",
            "Blade of Gale", "Blade of Tides"}
    };

    private final CustomLinkedList<Shop> shops;

    public ShopProgression(int levelCount, Random random) {
        if (levelCount < FIRST_LEVEL || random == null) {
            throw new IllegalArgumentException("Shop progression values are invalid");
        }
        shops = new CustomLinkedList<>();
        for (int level = FIRST_LEVEL; level <= levelCount; level++) {
            shops.add(createShop(level, random));
        }
    }

    public ShopProgression(int highestLevel) {
        this(highestLevel, new Random());
    }

    public Shop getShop(int level) {
        if (level < FIRST_LEVEL || level > shops.size()) {
            return null;
        }
        return shops.get(level - FIRST_LEVEL);
    }

    public int size() {
        return shops.size();
    }

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
