package tournament;

import java.util.Random;

import characters.GameCharacter;

public class TournamentRound {
    private final GameCharacter fighterOne;
    private final GameCharacter fighterTwo;
    private final Random random;

    public TournamentRound(GameCharacter fighterOne, GameCharacter fighterTwo) {
        this(fighterOne, fighterTwo, new Random());
    }

    public TournamentRound(GameCharacter fighterOne, GameCharacter fighterTwo,
        Random random) {
        if (fighterOne == null || fighterTwo == null || random == null) {
            throw new IllegalArgumentException("Fighters and random source are required");
        }
        this.fighterOne = fighterOne;
        this.fighterTwo = fighterTwo;
        this.random = random;
    }

    public double getFighterOneWinProbability() {
        double score = matchupScore(fighterOne, fighterTwo)
            - matchupScore(fighterTwo, fighterOne);
        return 1.0 / (1.0 + Math.exp(-score / 10.0));
    }

    public GameCharacter getWinner() {
        return random.nextDouble() < getFighterOneWinProbability()
            ? fighterOne : fighterTwo;
    }

    private double matchupScore(GameCharacter first, GameCharacter second) {
        return first.getAggro() - second.getControl()
            + first.getControl() - second.getMidrange()
            + first.getMidrange() - second.getAggro();
    }
}
