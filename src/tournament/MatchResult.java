package tournament;

import characters.GameCharacter;

/** Immutable result of one tournament match. */
public class MatchResult {

    private final GameCharacter fighter1;
    private final GameCharacter fighter2;
    private final GameCharacter winner;
    private final GameCharacter loser;
    private final int phase;

    /**
     * Creates a result for two fighters.
     *
     * @param fighter1 first participant
     * @param fighter2 second participant
     * @param winner   winning participant
     * @param phase    tournament phase in which the match occurred
     */
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
        this.phase = phase;
    }

    /** @return the winning fighter */
    public GameCharacter getWinner() {
        return winner;
    }

    /** @return the losing fighter */
    public GameCharacter getLoser() {
        return loser;
    }

    /** @return the first participant */
    public GameCharacter getFighter1() {
        return fighter1;
    }

    /** @return the second participant */
    public GameCharacter getFighter2() {
        return fighter2;
    }

    /** @return the tournament phase */
    public int getPhase() {
        return phase;
    }
}
