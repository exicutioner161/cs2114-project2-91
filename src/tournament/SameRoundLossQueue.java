package tournament;

import characters.GameCharacter;
import linkedlist.CustomLinkedList;

/**
 * Groups tournament losers by the round in which they were eliminated.
 */
public class SameRoundLossQueue {
    private final CustomLinkedList<CustomLinkedList<GameCharacter>> rounds =
        new CustomLinkedList<>();

    public void addLoss(int round, GameCharacter fighter) {
        if (round < 0 || fighter == null) {
            throw new IllegalArgumentException("Loss values are invalid");
        }
        while (rounds.size() <= round) {
            rounds.add(new CustomLinkedList<>());
        }
        rounds.get(round).add(fighter);
    }

    public CustomLinkedList<GameCharacter> getRound(int round) {
        if (round < 0 || round >= rounds.size()) {
            throw new IndexOutOfBoundsException("Loss round is invalid");
        }
        return rounds.get(round);
    }

    public int roundCount() {
        return rounds.size();
    }
}
