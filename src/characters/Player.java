package characters;

public class Player extends GameCharacter {
    private static final int SLOT_COUNT = 5;
    private final Item[] inventory;
    private int gold;
    private int level;

    public Player(String name, int level) {
        this(name, level, 0, new Item[SLOT_COUNT]);
    }

    public Player(String name, int level, int gold, Item[] inventory) {
        super(name, level);
        if (gold < 0 || inventory == null || inventory.length != SLOT_COUNT) {
            throw new IllegalArgumentException("Player values are invalid");
        }
        this.level = level;
        this.gold = gold;
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

    public int getGold() {
        return gold;
    }

    public int getLevel() {
        return level;
    }

    public Item[] getInventory() {
        return inventory.clone();
    }

    public Item getItem(int slot) {
        return validSlot(slot) ? inventory[slot] : null;
    }

    public boolean addItem(Item item) {
        if (item == null || !validSlot(item.getType())
            || inventory[item.getType()] != null) {
            return false;
        }
        inventory[item.getType()] = item;
        return true;
    }

    public Item removeItem(int slot) {
        if (!validSlot(slot)) {
            return null;
        }
        Item removed = inventory[slot];
        inventory[slot] = null;
        return removed;
    }

    public boolean buy(Item item) {
        if (item == null || !validSlot(item.getType())
            || inventory[item.getType()] != null || item.getPrice() > gold) {
            return false;
        }
        inventory[item.getType()] = item;
        gold -= item.getPrice();
        return true;
    }

    public boolean sell(int slot) {
        if (!validSlot(slot) || inventory[slot] == null) {
            return false;
        }
        gold += inventory[slot].getPrice() / 2;
        inventory[slot] = null;
        return true;
    }

    public void awardGold(int amount) {
        if (amount < 0 || amount > Integer.MAX_VALUE - gold) {
            throw new IllegalArgumentException("Gold amount is invalid");
        }
        gold += amount;
    }

    public void advanceLevel() {
        if (level == Integer.MAX_VALUE) {
            throw new IllegalStateException("Maximum level reached");
        }
        level++;
    }

    public String catalogText() {
        return "Current Gold: " + gold + "\nLevel: " + level;
    }

    private boolean validSlot(int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }
}
