package shop;

import characters.Item;
import characters.Player;
import linkedlist.CustomLinkedList;

/** Catalog of equipment offers available for purchase. */
public class Shop {
    private final CustomLinkedList<Item> catalog;

    /** Creates a shop with the default starter catalog. */
    public Shop() {
        catalog = new CustomLinkedList<>();
        addItem(new Item("Iron Helmet", 2, 1, 1, 40, 0));
        addItem(new Item("Iron Chestplate", 2, 3, 1, 60, 1));
        addItem(new Item("Iron Leggings", 2, 2, 1, 50, 2));
        addItem(new Item("Iron Boots", 0, 2, 2, 40, 3));
        addItem(new Item("Silver Spear", 0, 3, 2, 50, 4));
        addItem(new Item("Bastard Sword", 3, 1, 1, 50, 4));
    }

    /**
     * Creates a shop containing the supplied offers in order.
     *
     * @param items offers to add
     * @throws IllegalArgumentException if the array is null
     */
    public Shop(Item[] items) {
        catalog = new CustomLinkedList<>();
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        for (Item item : items) {
            addItem(item);
        }
    }

    /** @return the number of offers in the catalog */
    public int size() {
        return catalog.size();
    }

    /**
     * Returns an offer by catalog index.
     *
     * @param index zero-based catalog index
     * @return the selected offer
     * @throws IndexOutOfBoundsException if index is outside the catalog
     */
    public Item getItem(int index) {
        if (index < 0 || index >= catalog.size()) {
            throw new IndexOutOfBoundsException("Shop item index is invalid");
        }
        return catalog.get(index);
    }

    /** @return a numbered display of every catalog offer */
    public String catalogText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < catalog.size(); i++) {
            Item item = catalog.get(i);
            text.append(i + 1).append(". ").append(item.getName())
                    .append(" | Slot: ").append(slotName(item.getType()))
                    .append(" | A/C/M: ").append(item.getAggro()).append("/")
                    .append(item.getControl()).append("/")
                    .append(item.getMidrange())
                    .append(" | Price: ").append(item.getPrice())
                    .append('\n');
        }
        return text.toString();
    }

    /**
     * Adds an offer to the end of the catalog.
     *
     * @param item offer to add
     * @return whether the offer was added
     */
    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        catalog.add(item);
        return true;
    }

    /**
     * Removes the first case-insensitive offer matching a name.
     *
     * @param name offer name
     * @return whether an offer was removed
     */
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

    /**
     * Finds the first case-insensitive offer matching a name.
     *
     * @param name offer name
     * @return the matching offer, or {@code null} if none exists
     */
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

    /**
     * Attempts to sell a named offer to a player.
     *
     * @param player purchasing player
     * @param name   offer name
     * @return whether the purchase succeeded
     */
    public boolean buy(Player player, String name) {
        if (player == null) {
            return false;
        }
        Item item = findItem(name);
        return item != null && player.buy(item);
    }

    /** Converts an equipment slot index to its display label. */
    private String slotName(int slot) {
        return switch (slot) {
            case 0 -> "Head";
            case 1 -> "Body";
            case 2 -> "Legs";
            case 3 -> "Feet";
            case 4 -> "Weapon";
            default -> "Unknown";
        };
    }
}
