package characters;

public class Item {
    private final String name;
    private final double aggro;
    private final double control;
    private final double midrange;
    private final int price;
    private final int type;

    public Item(String name, double aggro, double control, double midrange,
        int price, int type) {
        if (name == null || name.isBlank() || price < 0 || type < 0 || type > 4
            || !validStat(aggro) || !validStat(control) || !validStat(midrange)) {
            throw new IllegalArgumentException("Item values are invalid");
        }
        this.name = name;
        this.aggro = aggro;
        this.control = control;
        this.midrange = midrange;
        this.price = price;
        this.type = type;
    }

    private static boolean validStat(double stat) {
        return !Double.isNaN(stat) && !Double.isInfinite(stat) && stat >= 0;
    }

    public String getName() {
        return name;
    }

    public double getAggro() {
        return aggro;
    }

    public double getControl() {
        return control;
    }

    public double getMidrange() {
        return midrange;
    }

    public int getPrice() {
        return price;
    }

    public int getType() {
        return type;
    }
}
