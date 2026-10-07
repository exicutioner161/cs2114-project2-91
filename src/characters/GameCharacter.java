package characters;

import java.util.Random;

public class GameCharacter {
    private static final double LEVEL_MULTIPLIER = 1.2;
    private static final double BASE_STAT_CEILING = 15.0;
    private int level;
    private double statCeiling;
    private double aggro;
    private double control;
    private double midrange;

    public GameCharacter(int level) {
        this.level = level;
        this.statCeiling = roundToTwoDecimals(BASE_STAT_CEILING * Math.pow(LEVEL_MULTIPLIER, level));
        distributeRandomStats();
    }

    private double roundToTwoDecimals(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

    private void distributeRandomStats() {
        Random rand = new Random();
        aggro = roundToTwoDecimals(rand.nextDouble(statCeiling));
        control = roundToTwoDecimals(rand.nextDouble(statCeiling - aggro));
        midrange = roundToTwoDecimals(statCeiling - aggro - control);
    }
}
