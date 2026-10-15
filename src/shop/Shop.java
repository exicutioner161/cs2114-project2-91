package shop;

import characters.Item;
import characters.Player;
import linkedlist.CustomLinkedList;

public class Shop {
    private final CustomLinkedList<Item> catalog;

    public Shop() {
        catalog = new CustomLinkedList<>();
        addItem(new Item("Iron Helmet", 2, 1, 1, 40, 0));
        addItem(new Item("Iron Chestplate", 2, 3, 1, 60, 1));
        addItem(new Item("Iron Leggings", 2, 2, 1, 50, 2));
        addItem(new Item("Iron Boots", 0, 2, 2, 40, 3));
        addItem(new Item("Silver Spear", 0, 3, 2, 50, 4));
        addItem(new Item("Bastard Sword", 3, 1, 1, 50, 4));
    }

    public Shop(Item[] items) {
        catalog = new CustomLinkedList<>();
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        for (Item item : items) {
            addItem(item);
        }
    }

    public int size() {
        return catalog.size();
    }

    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        catalog.add(item);
        return true;
    }

    public boolean removeItem(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        for (int i = 0; i < catalog.size(); i++) {
            if (catalog.get(i).getName().equalsIgnoreCase(name)) {
                catalog.remove(i);
                return true;
            }
        }
        return false;
    }

    public Item findItem(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        for (int i = 0; i < catalog.size(); i++) {
            Item item = catalog.get(i);
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public boolean buy(Player player, String name) {
        if (player == null) {
            return false;
        }
        Item item = findItem(name);
        return item != null && player.buy(item);
    }
}
