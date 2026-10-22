package tournament;

import characters.GameCharacter;
import linkedlist.CustomLinkedList;

/**
 * Groups tournament losers by the round in which they were eliminated.
 */
public class SameRoundLossQueue {
    private final CustomLinkedList<CustomLinkedList<GameCharacter>> rounds = new CustomLinkedList<>();

    /**
     * Adds a fighter to the queue for an elimination round.
     *
     * @param round   zero-based round number
     * @param fighter eliminated fighter
     * @throws IllegalArgumentException if round or fighter is invalid
     */
    public void addLoss(int round, GameCharacter fighter) {
        if (round < 0 || fighter == null) {
            throw new IllegalArgumentException("Loss values are invalid");
        }
        while (rounds.size() <= round) {
            rounds.add(new CustomLinkedList<>());
        }
        rounds.get(round).add(fighter);
    }

    /**
     * Returns the fighters eliminated in a round.
     *
     * @param round zero-based round number
     * @return fighters eliminated in that round
     * @throws IndexOutOfBoundsException if the round is unavailable
     */
    public CustomLinkedList<GameCharacter> getRound(int round) {
        if (round < 0 || round >= rounds.size()) {
            throw new IndexOutOfBoundsException("Loss round is invalid");
        }
        return rounds.get(round);
    }

    /** @return the number of tracked rounds */
    public int roundCount() {
        return rounds.size();
    }
}
