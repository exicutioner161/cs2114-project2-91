package tournament;

import java.util.Random;

import characters.GameCharacter;

/** Resolves one match using style advantages and a random roll. */
public class TournamentRound {
    private static final double ONE_HUNDRED = 100.0;
    private static final double BASE_WIN_PERCENT = 50.0;
    private static final double HIGH_ADVANTAGE_INCREMENT = 7.0;
    private static final double MED_ADVANTAGE_INCREMENT = 3.5;
    private static final double BASE_ADVANTAGE_INCREMENT = 2.0;
    private Random random;
    private double fighterOneWinPercent;
    private GameCharacter fighter1;
    private GameCharacter fighter2;
    private GameCharacter winner;

    /**
     * Creates a match with a default random source.
     *
     * @param fighter1 first fighter
     * @param fighter2 second fighter
     */
    public TournamentRound(GameCharacter fighter1, GameCharacter fighter2) {
        this(fighter1, fighter2, new Random());
    }

    /**
     * Creates a match with an injectable random source.
     *
     * @param fighter1 first fighter
     * @param fighter2 second fighter
     * @param random   source for the result
     * @throws IllegalArgumentException if a fighter or random source is invalid
     */
    public TournamentRound(GameCharacter fighter1, GameCharacter fighter2,
            Random random) {
        if (fighter1 == null || fighter2 == null || fighter1 == fighter2) {
            throw new IllegalArgumentException("Invalid fighters");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random source is required");
        }
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.fighterOneWinPercent = BASE_WIN_PERCENT;
        this.random = random;
    }

    /**
     * Resolves and returns the winner, caching the result.
     *
     * @return winning fighter
     */
    public GameCharacter getWinner() {
        if (winner != null) {
            return winner;
        }
        fighterOneWinPercent = calculateWinPercent();
        winner = random.nextDouble() * ONE_HUNDRED < fighterOneWinPercent
                ? fighter1
                : fighter2;
        return winner;
    }

    /** Combines both fighters' style advantages into a bounded probability. */
    private double calculateWinPercent() {
        double advantage = fighterAdvantage(fighter1, fighter2)
                - fighterAdvantage(fighter2, fighter1);
        return Math.max(0, Math.min(100, BASE_WIN_PERCENT + advantage));
    }

    /** Calculates the three counter-style advantages for one fighter. */
    private double fighterAdvantage(GameCharacter fighter,
            GameCharacter opponent) {
        return calculateFighterAdvantage(fighter.getAggro(), opponent.getControl())
                + calculateFighterAdvantage(fighter.getControl(), opponent.getMidrange())
                + calculateFighterAdvantage(fighter.getMidrange(), opponent.getAggro());
    }

    /** Converts a pair of stats into a one-sided advantage score. */
    private double calculateFighterAdvantage(double fighterOneStat, double fighterTwoStat) {
        double fighterOneAdv = 0;
        if (fighterOneStat > fighterTwoStat) {
            return getHighAdv(fighterOneStat, fighterTwoStat);
        } else if (fighterOneStat == fighterTwoStat) {
            return getMedAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, 0);
        } else {
            return getBaseAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, 0);
        }
    }

    /** Calculates the large advantage contribution for a higher stat. */
    private double getHighAdv(double fighterOneStat, double fighterTwoStat) {
        double fighterOneAdv = 0;
        double statDiff = fighterOneStat - fighterTwoStat;
        fighterOneAdv += statDiff * HIGH_ADVANTAGE_INCREMENT;

        return getMedAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, statDiff);
    }

    /** Adds the medium contribution shared by an advantage comparison. */
    private double getMedAdv(double fighterOneStat, double fighterTwoStat, double fighterOneAdv, double statDiff) {
        fighterOneAdv += MED_ADVANTAGE_INCREMENT;
        return getBaseAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, statDiff);
    }

    /** Adds the base contribution for each point of stat difference. */
    private double getBaseAdv(double fighterOneStat, double fighterTwoStat, double fighterOneAdv, double statDiff) {
        for (int i = 0; i < fighterOneStat - fighterTwoStat; i++) {
            fighterOneAdv += BASE_ADVANTAGE_INCREMENT;
        }
        return fighterOneAdv;
    }
}