package characters;

public class Item {
    private String name;
    private double aggro;
    private double control;
    private double midrange;
    private int price;
    private int type;

    public Item(String name, double aggro, double control, double midrange, int price, int type) {
        this.name = name;
        this.aggro = aggro;
        this.control = control;
        this.midrange = midrange;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAggro() {
        return aggro;
    }

    public void setAggro(double aggro) {
        this.aggro = aggro;
    }

    public double getControl() {
        return control;
    }

    public void setControl(double control) {
        this.control = control;
    }

    public double getMidrange() {
        return midrange;
    }

    public void setMidrange(double midrange) {
        this.midrange = midrange;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getType(){
        return type;
    }
    
    public void setType(int type){
        this.type=type;
    }
}
