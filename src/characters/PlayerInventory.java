package characters;

public class PlayerInventory {
    private final String[] inventory;

    public PlayerInventory() {
        inventory = new String[5];
    }

    public boolean addHelmet(String name) {
        return addItem(name, 0);
    }

    public boolean addChestplate(String name) {
        return addItem(name, 1);
    }

    public boolean addLeggings(String name) {
        return addItem(name, 2);
    }

    public boolean addBoots(String name) {
        return addItem(name, 3);
    }

    public boolean addWeapon(String name) {
        return addItem(name, 4);
    }

    private boolean addItem(String name, int index) {
        if (name == null) {
            return false;
        }
        inventory[index] = name;
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
