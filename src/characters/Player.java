package characters;

public class Player extends GameCharacter {
    private Item[] inventory;
    // Indices: 0 helmet, 1 chestplate, 2 leggings, 3 boots, 4 weapon
    private int gold;
    private int level;
    // Own gold, inventory, and equipment-based stats. subclass of GameCharacter

    public Player(int level) {
        super(1);
        Item[] inventory = new Item[5];
        gold = 0;
        level = 0;
        // need to add startign gear and have the player stats start at 0
    }

    // public Player(String name, int level, int gold, Item[] inventory){
    // super.name = name;
    // super()
    // this.level = level;
    // this.gold = gold;
    // this.inventory = inventory;
    // }
    // Player: Player(String name, int level, int gold,
    // String[5] inventory)

    public boolean addItem(Item item) {
        if (inventory[item.getType()] != null) {
            return false;
        } else {
            inventory[item.getType()] = item;

            return true;
        }
    }

    public Item removeItem(int type) {
        return inventory[type];
    }

    // public String toString() {
    // String output = "";
    // // TODO: output += super.getName();
    // // needs to have .getName();
    // // TODO: the items should use toString method for item when avialable
    // output += "\nGold: " + gold;
    // output += "\nLevel: " + level;
    // output += "\nAggro: " + super.getAggro();
    // output += "\nControl: " + super.getControl();
    // output += "\nMidrange: " + super.getMidrange();
    // output += "\nHelmet: " + inventory[0].getName();
    // output += "\nChestplate: " + inventory[1].getName();
    // output += "\nLeggings: " + inventory[2].getName();
    // output += "\nBoots: " + inventory[3].getName();
    // output += "\nWeapon: " + inventory[4].getName();
    // return output;
    // }

    // public Item getItem(int type) {
    // return inventory[type];
    // }

    // Validate and copy equipment. set name, level, gold, and equipment-derived
    // stats
    // O(c)
    // Player: int getGold()
    // Player getInventory()
    // Return gold or a defensive copy of the five-slot inventory
    // O(1)
    // Player: boolean buy(String name)
    // boolean sell(int slot)
    // Buy into an empty matching slot, or sell equipped gear for half price rounded
    // down. recalculate stats. Invalid transactions return false
    // O(n)
    // Player: void awardGold(int amount)
    // void advanceLevel()
    // Add nonnegative gold, or increase level without direct stat gains. Reject
    // invalid/overflowing changes
    // O(1)
    // Player: String catalogText()
    // Format catalog offers, prices, and unlock levels
    // O(n)

}
