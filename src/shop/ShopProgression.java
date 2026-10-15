package shop;

import java.util.Random;

import characters.Item;
import linkedlist.CustomLinkedList;

public class ShopProgression {
    private final CustomLinkedList<Shop> shops;

    public ShopProgression(int highestLevel, Random random) {
        if (highestLevel < 0 || random == null) {
            throw new IllegalArgumentException("Shop progression values are invalid");
        }
        shops = new CustomLinkedList<>();
        for (int level = 0; level <= highestLevel; level++) {
            shops.add(createShop(level, random));
        }
    }

    public ShopProgression(int highestLevel) {
        this(highestLevel, new Random());
    }

    public Shop getShop(int level) {
        if (level < 0 || level >= shops.size()) {
            return null;
        }
        return shops.get(level);
    }

    public int size() {
        return shops.size();
    }

    private Shop createShop(int level, Random random) {
        int bonus = level * 2 + random.nextInt(4);
        return new Shop(new Item[] {
            new Item("Level " + level + " Helmet", 2 + bonus, 1 + level,
                1 + level, 40 + level * 10, 0),
            new Item("Level " + level + " Sword", 3 + bonus, 1 + level,
                1, 50 + level * 15, 4)
        });
    }
}
