package tournament;

import characters.GameCharacter;

public class MatchResult {

    private final GameCharacter fighter1;
    private final GameCharacter fighter2;
    private final GameCharacter winner;
    private final GameCharacter loser;

    public MatchResult(GameCharacter fighter1, GameCharacter fighter2,
            GameCharacter winner, int phase) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.winner = winner;
        if (winner == fighter1) {
            loser = fighter2;
        } else {
            loser = fighter1;
        }
    }

    public GameCharacter getWinner() {
        return winner;
    }

    public GameCharacter getLoser() {
        return loser;
    }

    public GameCharacter getFighter1() {
        return fighter1;
    }

    public GameCharacter getFighter2() {
        return fighter2;
    }
}
