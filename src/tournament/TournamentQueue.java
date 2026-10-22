package tournament;

import characters.GameCharacter;
import characters.Player;
import linkedlist.CustomLinkedList;

/**
 * Holds opponents waiting for the next tournament.
 */
public class TournamentQueue {
    private final CustomLinkedList<GameCharacter> fighters =
        new CustomLinkedList<>();

    /**
     * Adds a waiting fighter if it is not already queued.
     *
     * @param fighter fighter to queue
     * @return whether the fighter was added
     */
    public boolean add(GameCharacter fighter) {
        if (fighter == null || fighters.contains(fighter)) {
            return false;
        }
        fighters.add(fighter);
        return true;
    }

    /** @return and removes the next waiting fighter, or {@code null} if empty */
    public GameCharacter removeNext() {
        return fighters.isEmpty() ? null : fighters.removeFirst();
    }

    /** @return the number of waiting fighters */
    public int size() {
        return fighters.size();
    }

    /**
     * Returns a waiting fighter by position.
     *
     * @param index zero-based queue index
     * @return the waiting fighter
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public GameCharacter get(int index) {
        if (index < 0 || index >= fighters.size()) {
            throw new IndexOutOfBoundsException("Queue index is invalid");
        }
        return fighters.get(index);
    }

    /** Preserves waiting-fighter order after a tournament loss. */
    public void preserveAfterLoss() {
        // Waiting fighters remain in their original order after elimination.
    }

    /**
     * Refills a loss queue without discarding opponents already waiting.
     *
     * @param player player whose level determines generated opponents
     * @param count desired queue size
     * @throws IllegalArgumentException if arguments are invalid
     */
    public void repopulateAfterLoss(Player player, int count) {
        if (player == null || count < fighters.size()) {
            throw new IllegalArgumentException("Queue values are invalid");
        }
        int next = fighters.size();
        while (fighters.size() < count) {
            fighters.add(new GameCharacter("Queued Opponent " + next,
                player.getLevel()));
            next++;
        }
    }

    /**
     * Replaces the queue with stronger opponents for the next level.
     *
     * @param player player whose level determines opponent strength
     * @param count number of opponents to create
     * @throws IllegalArgumentException if arguments are invalid
     */
    public void repopulateAfterWin(Player player, int count) {
        if (player == null || count < 0) {
            throw new IllegalArgumentException("Queue values are invalid");
        }
        fighters.clear();
        for (int i = 0; i < count; i++) {
            fighters.add(new GameCharacter("Stronger Opponent " + i,
                player.getLevel() + 1));
        }
    }
}
