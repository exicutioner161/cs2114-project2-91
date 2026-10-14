package characters;

public class Player extends GameCharacter {
    private Item[] inventory;
    
    // Indices: 0 helmet, 1 chestplate, 2 leggings, 3 boots, 4 weapon
    private int gold;
    private int level;
    // Own gold, inventory, and equipment-based stats. subclass of GameCharacter

    public Player(String name, int level) {
        super(name, level);
        this.inventory = new Item[5];
        this.gold = 0;
        this.level = level;
        // need to add startign gear and have the player stats start at 0
    }

    public Player(String name, int level, int gold, Item[] inventory){
        super(name,level);
        this.level = level;
        this.gold = gold;
        this.inventory = new Item[5];
        for (int i=0; i < inventory.length; i++){
            if (inventory[i] != null){
                if(inventory[i].getType() != i){
                    throw new IllegalArgumentException("Invalid Type");
                }
                Item item = inventory[i];
                this.inventory[i] = new Item(item.getName(),item.getAggro(),
                item.getControl(),item.getMidrange(),item.getPrice(),item.getType());

            }
        }
    }
    // Player: Player(String name, int level, int gold,
    // String[5] inventory)

    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        int type = item.getType();
        if (type < 0 || type >= inventory.length){
            return false;
        }
        if (inventory[type] != null){
            return false;
        }
        inventory[type] = item;
        return true;
        
        }

    public Item removeItem(int type) {
        if (type < 0 || type >=inventory.length){
            return null;
        }
        Item removed = inventory[type];
        inventory[type] = null;
        return removed;
    }
    @Override
    public double getAggro(){
        double total = 0;
        for(Item item : inventory){
            if (item != null){
                total += item.getAggro();
            }
        }
        return total;
    }
    @Override
    public double getControl(){
        double total = 0;
        for(Item item : inventory){
            if (item != null){
                total += item.getControl();
            }
        }
        return total;
    }
    @Override
    public double getMidrange(){
        double total = 0;
        for(Item item : inventory){
            if (item != null){
                total += item.getMidrange();
            }
        }
        return total;
    }
    public int getGold(){
        return gold;
    }
    public Item[] getInventory(){
        return inventory.clone();
    }
    public Item getItem(int type){
        if (type < 0 || type >= inventory.length){
            return null;
        }
        return inventory[type];
    }
    public boolean buy(Item item){
        if (item == null){
            return false;
        }
        int type = item.getType();
        if(type < 0 || type >= inventory.length){
            return false;
        }
        if (inventory[type] != null){
            return false;
        }
        if (item.getPrice() < 0 || gold < item.getPrice()){
            return false;
        }
        inventory[type] = new Item(item.getName(),item.getAggro(),
        item.getControl(), item.getMidrange(), item.getPrice(), item.getType());
        gold -= item.getPrice();
        return true;
    }
    public boolean sell(int slot){
        if (slot < 0 || slot >= inventory.length){
            return false;
        }
        if (inventory[slot] == null){
            return false;
        }
        Item meep = inventory[slot];
        int price = meep.getPrice()/2;
        if (price < 0 || price > Integer.MAX_VALUE - gold){
            return false;
        }
        inventory[slot] = null;
        gold += price;
        return true;

    }
    public void awardGold(int amount){
        if (amount<0|| amount> Integer.MAX_VALUE - gold){
            throw new IllegalArgumentException("That's too much man");
        }
        gold+=amount;
    }
    public void advanceLevel(){
        if (level == Integer.MAX_VALUE) {
        throw new IllegalStateException("Genuinely How?");
    }
        level++;
    }
    public String catalogText(){
        String display = "--- Shop ---\n";
        display += "Current Gold: "+gold+"\n";
        return output;
    }
    public int getLevel(){
        return level;
    }
    @Override
    public String toString(){
        String display = "_____ PLAYER STATS _____\n";
        display+= "Name: "+getName()+"\n";
        display+= "Level: "+getLevel()+"\n";
        display+= "Gold: "+gold+"\n";
        display+= "\n___ Combat STATS ___\n";
        display+= "Aggro: "+getAggro()+"\n";
        display+= "Control: "+getControl()+"\n";
        display+= "Midrange: "+getMidrange()+"\n";
        display+= "\n___ Equipment ___\n";
        String[] slots = {"Helmet","Chestplate","Leggings","Boots","Weapon"};
        for (int i = 0; i < inventory.length; i++){
            display+= slots[i]+": ";
            if (inventory[i] == null){
                display+= "\n";
            }
            else{
                display+= inventory[i].getName()+"\n";
            }
        }
        return display;
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
