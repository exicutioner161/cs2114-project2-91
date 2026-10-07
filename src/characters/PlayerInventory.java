package characters;

public class PlayerInventory {
    private final String[] inventory;

    public PlayerInventory() {
        inventory = new String[5];
    }

    public boolean addHelmet(String name) {
        if (name == null) {
            return false;
        }
        inventory[0] = name;
        return true;
    }

    public boolean addChestplate(String name) {
        if (name == null) {
            return false;
        }
        inventory[1] = name;
        return true;
    }

    public boolean addLeggings(String name) {
        if (name == null) {
            return false;
        }
        inventory[2] = name;
        return true;
    }

    public boolean addBoots(String name) {
        if (name == null) {
            return false;
        }
        inventory[3] = name;
        return true;
    }

    public boolean addWeapon(String name) {
        if (name == null) {
            return false;
        }
        inventory[4] = name;
        return true;
    }

    public boolean removeHelmet() {
        return removeItem(0);
    }

    public boolean removeChestplate() {
        return removeItem(1);
    }

    public boolean removeLeggings() {
        return removeItem(2);
    }

    public boolean removeBoots() {
        return removeItem(3);
    }

    public boolean removeWeapon() {
        return removeItem(4);
    }

    private boolean removeItem(int index) {
        if (inventory[index] == null) {
            return false;
        }
        inventory[index] = null;
        return true;
    }
}
