package characters;

/** Immutable equipment offer with combat statistics and a slot assignment. */
public class Item {
    private final String name;
    private final double aggro;
    private final double control;
    private final double midrange;
    private final int price;
    private final int type;

    /**
     * Creates an equipment offer.
     *
     * @param name     display name
     * @param aggro    Aggro statistic
     * @param control  Control statistic
     * @param midrange Midrange statistic
     * @param price    purchase price
     * @param type     equipment slot from 0 through 4
     * @throws IllegalArgumentException if any value is invalid
     */
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

    /** Checks that a combat statistic is finite and nonnegative. */
    private static boolean validStat(double stat) {
        return !Double.isNaN(stat) && !Double.isInfinite(stat) && stat >= 0;
    }

    /** @return the item display name */
    public String getName() {
        return name;
    }

    /** @return the Aggro statistic */
    public double getAggro() {
        return aggro;
    }

    /** @return the Control statistic */
    public double getControl() {
        return control;
    }

    /** @return the Midrange statistic */
    public double getMidrange() {
        return midrange;
    }

    /** @return the purchase price */
    public int getPrice() {
        return price;
    }

    /** @return the equipment slot index */
    public int getType() {
        return type;
    }

    @Override
    /**
     * @return the name and stats of the item
     */
    public String toString() {
        return name + ", " + "A/C/M" + aggro + "/" + control + "/" + midrange;
    }
}
