package tournament;

import characters.GameCharacter;
import linkedlist.CustomLinkedList;

public class Tournament {
    private final CustomLinkedList<GameCharacter> queue;

    public Tournament() {
        queue = new CustomLinkedList<>();
    }

    public boolean addFighter(GameCharacter fighter) {
        if (fighter == null) {
            return false;
        }
        queue.add(fighter);
        return true;
    }

    public int size() {
        return queue.size();
    }

    public GameCharacter removeNextFighter() {
        return queue.isEmpty() ? null : queue.removeFirst();
    }

    public GameCharacter runMatch(GameCharacter player) {
        if (player == null || queue.isEmpty()) {
            return null;
        }
        GameCharacter opponent = queue.removeFirst();
        return new TournamentRound(player, opponent).getWinner();
    }
}
