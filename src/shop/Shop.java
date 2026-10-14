package shop;
import characters.Item;
import characters.Player;
public class Shop {

    private Item[] catalog;
    public Shop(){
        catalog = new Item[0];
        addItem(new Item("Iron Helmet",2,1,1,40,0));
        addItem(new Item("Iron Chestplate",2,3,1,60,1));
        addItem(new Item("Iron Leggings",2,2,1,50,2));
        addItem(new Item("Iron Boots",0,2,2,40,3));
        addItem(new Item("Silver Spear",0,3,2,50,4));
        addItem(new Item("Throwing Dagger",2,0,3,50,4));
        addItem(new Item("Shield",1,4,0,50,4));
        addItem(new Item("Bastard Sword",3,1,1,50,4));
    }
    public Shop(Item[] items){
        catalog = new Item[0];
        if (items != null){
            for (Item item : items){
                addItem(item);
            }
        }
    } 
    public Item[] getCatalog(){
        return catalog.clone();
    }
    public boolean addItem(Item item){
        if (item == null || item.getName() == name){
            return false;
        }
        for (Item product : catalog){
            if (product.getName().equalsIgnoreCase(item.getName())){
                return false;
            }
        }
        Item[] selection = new Item[catalog.length + 1];
        for (int i = 0; i < catalog.length; i++){
            selection[i] = catalog[i];
        }
        selection[catalog.length] = item;
        catalog = selection;
        return true;
    }
    public boolean removeItem(String name){
        if (name == null){
            return false;
        }
        int index = 0;
        for (int i = 0; i < catalog.length; i++){
            if(catalog[i].getName().equalsIgnoreCase(name)){
                index = i;
                break;
            }
            if (index == 0){
                return false;
            }
            Item[] selection = new Item[catalog.length-1];
            i = 0;
            for (int j = 0; j < catalog.length; j++){
                if (j != index){
                    selection[i] = catalog[j];
                    i++;
                }
            }
            catalog = selection;
        }
        return true;
    }
    public Item findItem(String name){
        if (name == null){
            return null;
        }
        for (Item item : catalog){
            if (item.getName().equalsIgnoreCase(name)){
                return item;
            }
        }
        return null;
    }
    public boolean sell(Player player, String name){
        if (player == null){
            return false;
        }
        Item item = findItem(name);
        if (item == null){
            return false;
        }
        return player.buy(item);
    }

}
