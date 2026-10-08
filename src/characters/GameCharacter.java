package characters;

import java.util.Random;

public class GameCharacter {
    private static final double LEVEL_MULTIPLIER = 1.2;
    private static final double BASE_STAT_CEILING = 15.0;
    private final int level;
    private final double statCeiling;
    private final double aggro;
    private final double control;
    private final double midrange;

    public GameCharacter(int level) {
        this.level = level;
        this.statCeiling = BASE_STAT_CEILING * Math.pow(LEVEL_MULTIPLIER, level);
        Random rand = new Random();
        double randomNum1 = rand.nextDouble(statCeiling);
        double randomNum2 = rand.nextDouble(statCeiling);
        double randomNum3 = rand.nextDouble(statCeiling);
        double total = randomNum1 + randomNum2 + randomNum3;
        aggro = randomNum1 * statCeiling / total;
        control = randomNum2 * statCeiling / total;
        midrange = randomNum3 * statCeiling / total;
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
}
