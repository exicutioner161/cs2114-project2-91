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

    public boolean add(GameCharacter fighter) {
        if (fighter == null || fighters.contains(fighter)) {
            return false;
        }
        fighters.add(fighter);
        return true;
    }

    public GameCharacter removeNext() {
        return fighters.isEmpty() ? null : fighters.removeFirst();
    }

    public int size() {
        return fighters.size();
    }

    public GameCharacter get(int index) {
        if (index < 0 || index >= fighters.size()) {
            throw new IndexOutOfBoundsException("Queue index is invalid");
        }
        return fighters.get(index);
    }

    public void preserveAfterLoss() {
        // Waiting fighters remain in their original order after elimination.
    }

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
