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
        if (inventory[0] == null) {
            return false;
        }
        inventory[0] = null;
        return true;
    }

    public boolean removeChestplate() {
        if (inventory[1] == null) {
            return false;
        }
        inventory[1] = null;
        return true;
    }

    public boolean removeLeggings() {
        if (inventory[2] == null) {
            return false;
        }
        inventory[2] = null;
        return true;
    }

    public boolean removeBoots() {
        if (inventory[3] == null) {
            return false;
        }
        inventory[3] = null;
        return true;
    }
}
