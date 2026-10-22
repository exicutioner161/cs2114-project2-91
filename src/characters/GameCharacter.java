package characters;

import java.util.Random;

/** A tournament fighter with a level and three fighting-style statistics. */
public class GameCharacter {
    private static final double LEVEL_MULTIPLIER = 1.2;
    private static final double BASE_STAT_CEILING = 15.0;
    private String name;
    private int level;
    private double aggro;
    private double control;
    private double midrange;

    /**
     * Creates a fighter with deterministic statistics derived from name and level.
     *
     * @param name fighter name
     * @param level fighter level
     * @throws IllegalArgumentException if name or level is invalid
     */
    public GameCharacter(String name, int level) {
        if (name == null || name.isBlank() || level < 0) {
            throw new IllegalArgumentException("Name and level are invalid");
        }
        this.name = name;
        this.level = level;
        randomizeStats();
    }

    /**
     * Creates a fighter with explicit statistics.
     *
     * @param name fighter name
     * @param level fighter level
     * @param aggro Aggro statistic
     * @param control Control statistic
     * @param midrange Midrange statistic
     * @throws IllegalArgumentException if any value is invalid
     */
    public GameCharacter(String name, int level, double aggro, double control,
            double midrange) {
        if (name == null || name.isBlank() || level < 0
                || !validStat(aggro) || !validStat(control) || !validStat(midrange)) {
            throw new IllegalArgumentException("Character values are invalid");
        }
        this.name = name;
        this.level = level;
        this.aggro = aggro;
        this.control = control;
        this.midrange = midrange;
    }

    private void randomizeStats() {
        double ceiling = BASE_STAT_CEILING * Math.pow(LEVEL_MULTIPLIER, level);
        Random random = new Random(name.hashCode() * 31L + level);
        double first = random.nextDouble();
        double second = random.nextDouble();
        double third = random.nextDouble();
        double total = first + second + third;
        aggro = first * ceiling / total;
        control = second * ceiling / total;
        midrange = third * ceiling / total;
    }

    private static boolean validStat(double stat) {
        return !Double.isNaN(stat) && !Double.isInfinite(stat) && stat >= 0;
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

    /** @return the fighter level */
    public int getLevel() {
        return level;
    }

    /** Advances the fighter by one level. */
    public void incrementLevel() {
        level++;
    }

    /** @return the fighter name */
    public String getName() {
        return name;
    }

    /** @return a readable summary of the fighter and combat statistics */
    @Override
    public String toString() {
        return String.format("%s (Lv.%d, A:%.1f C:%.1f M:%.1f)",
            name, level, aggro, control, midrange);
    }
}
