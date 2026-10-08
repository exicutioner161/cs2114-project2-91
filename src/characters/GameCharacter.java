package characters;

import java.util.Random;

public class GameCharacter {
    private static final double LEVEL_MULTIPLIER = 1.2;
    private static final double BASE_STAT_CEILING = 15.0;
    private final String name;
    private final int level;
    private final double statCeiling;
    private final double aggro;
    private final double control;
    private final double midrange;

    public GameCharacter(String name, int level) {
        this.name = name;
        this.level = level;
        this.statCeiling = roundToTwoDecimals(BASE_STAT_CEILING * Math.pow(LEVEL_MULTIPLIER, level));
        Random rand = new Random();
        aggro = roundToTwoDecimals(rand.nextDouble(statCeiling));
        control = roundToTwoDecimals(rand.nextDouble(statCeiling - aggro));
        midrange = roundToTwoDecimals(statCeiling - aggro - control);
    }

    private double roundToTwoDecimals(double x) {
        return Math.round(x * 100.0) / 100.0;
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

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
}
