package characters;

/** The user's fighter, including gold, equipment, and tournament progress. */
public class Player extends GameCharacter {
    private static final int MAX_LEVEL = 5;
    private static final int SLOT_COUNT = 5;
    private final Item[] inventory;
    private int gold;
    private int wins;
    private int losses;

    /**
     * Creates a player with no gold or equipment.
     *
     * @param name player name
     * @param level starting level
     */
    public Player(String name, int level) {
        this(name, level, 0, new Item[SLOT_COUNT]);
    }

    /**
     * Creates a player with saved progress.
     *
     * @param name player name
     * @param level player level
     * @param gold starting gold
     * @param inventory five-slot equipment array
     * @throws IllegalArgumentException if values or slot assignments are invalid
     */
    public Player(String name, int level, int gold, Item[] inventory) {
        super(name, level);
        if (gold < 0 || inventory == null || inventory.length != SLOT_COUNT) {
            throw new IllegalArgumentException("Player values are invalid");
        }
        this.gold = gold;
        this.wins = 0;
        this.losses = 0;
        this.inventory = new Item[SLOT_COUNT];
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                if (inventory[i].getType() != i) {
                    throw new IllegalArgumentException("Item does not match slot");
                }
                this.inventory[i] = inventory[i];
            }
        }
    }

    @Override
    public double getAggro() {
        return statTotal(0);
    }

    @Override
    public double getControl() {
        return statTotal(1);
    }

    @Override
    public double getMidrange() {
        return statTotal(2);
    }

    /** Totals one combat statistic across equipped items. */
    private double statTotal(int stat) {
        double total = 0;
        for (Item item : inventory) {
            if (item != null) {
                total += stat == 0 ? item.getAggro()
                        : stat == 1 ? item.getControl() : item.getMidrange();
            }
        }
        return total;
    }

    /** @return current gold */
    public int getGold() {
        return gold;
    }

    /** @return a copy of the equipment slots */
    public Item[] getInventory() {
        return inventory.clone();
    }

    /**
     * Returns equipment in a slot.
     *
     * @param slot zero-based equipment slot
     * @return equipped item, or {@code null}
     */
    public Item getItem(int slot) {
        return validSlot(slot) ? inventory[slot] : null;
    }

    /**
     * Adds equipment without charging gold.
     *
     * @param item item to equip
     * @return whether the item was added
     */
    public boolean addItem(Item item) {
        if (item == null || !validSlot(item.getType())
                || inventory[item.getType()] != null) {
            return false;
        }
        inventory[item.getType()] = item;
        return true;
    }

    /**
     * Removes equipment from a slot.
     *
     * @param slot zero-based equipment slot
     * @return removed item, or {@code null}
     */
    public Item removeItem(int slot) {
        if (!validSlot(slot)) {
            return null;
        }
        Item removed = inventory[slot];
        inventory[slot] = null;
        return removed;
    }

    /**
     * Purchases and equips an item when the slot and balance permit it.
     *
     * @param item item to purchase
     * @return whether the purchase succeeded
     */
    public boolean buy(Item item) {
        if (item == null || !validSlot(item.getType())
                || inventory[item.getType()] != null || item.getPrice() > gold) {
            return false;
        }
        inventory[item.getType()] = item;
        gold -= item.getPrice();
        return true;
    }

    /**
     * Sells equipped gear for half its listed price.
     *
     * @param slot zero-based equipment slot
     * @return whether an item was sold
     */
    public boolean sell(int slot) {
        if (!validSlot(slot) || inventory[slot] == null) {
            return false;
        }
        gold += inventory[slot].getPrice() * 3 / 4;
        inventory[slot] = null;
        return true;
    }

    /**
     * Adds gold to the player's balance.
     *
     * @param amount nonnegative gold amount
     * @throws IllegalArgumentException if amount is negative
     */
    public void awardGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold award cannot be negative");
        }
        gold += amount;
    }

    /**
     * Advances the player one level.
     *
     * @throws IllegalStateException if already at the maximum level
     */
    public void advanceLevel() {
        if (getLevel() == MAX_LEVEL) {
            throw new IllegalStateException("Maximum level reached");
        }
        incrementLevel();
    }

    /** @return a display of current gold and level */
    public String catalogText() {
        return "Current Gold: " + gold + "\nLevel: " + getLevel();
    }

    /** Checks whether a slot index belongs to the five-slot inventory. */
    private boolean validSlot(int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    /** @return total tournament wins */
    public int getWins() {
        return wins;
    }

    /**
     * Sets total tournament wins.
     *
     * @param wins nonnegative win count
     * @throws IllegalArgumentException if wins is negative
     */
    public void setWins(int wins) {
        if (wins < 0) {
            throw new IllegalArgumentException("Wins cannot be negative");
        }
        this.wins = wins;
    }

    /** Increments the tournament win count. */
    public void incrementWins() {
        wins++;
    }

    /** @return total tournament losses */
    public int getLosses() {
        return losses;
    }

    /**
     * Sets total tournament losses.
     *
     * @param losses nonnegative loss count
     * @throws IllegalArgumentException if losses is negative
     */
    public void setLosses(int losses) {
        if (losses < 0) {
            throw new IllegalArgumentException("Losses cannot be negative");
        }
        this.losses = losses;
    }

    /** Increments the tournament loss count. */
    public void incrementLosses() {
        losses++;
    }

    /** @return a readable summary of progress and equipment */
    @Override
    public String toString() {
        StringBuilder equipment = new StringBuilder();
        for (int slot = 0; slot < inventory.length; slot++) {
            if (inventory[slot] != null) {
                if (equipment.length() > 0) {
                    equipment.append(", ");
                }
                equipment.append(inventory[slot].getName());
            }
        }
        if (equipment.length() == 0) {
            equipment.append("none");
        }
        return String.format("%s | Gold: %d | Wins: %d | Losses: %d | Gear: %s",
                super.toString(), gold, wins, losses, equipment);
    }
}
