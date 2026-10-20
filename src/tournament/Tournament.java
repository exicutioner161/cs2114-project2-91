package tournament;

import characters.GameCharacter;
import characters.Player;
import characters.RandomNamePicker;
import linkedlist.CustomLinkedList;

public class Tournament {
    private static final RandomNamePicker nameGen = new RandomNamePicker();
    // TODO: Add 8-person bracket (should be double-elim until the grand finals)
    private final CustomLinkedList<GameCharacter> queue;
    private final CustomLinkedList<TournamentRound> bracket;

    public Tournament(Player player) {
        queue = new CustomLinkedList<>();
        bracket = new CustomLinkedList<>();
    }

    public boolean fillBracket(Player player) {
        if (player == null) {
            return false;
        }
        return true;
    }

    public boolean addFighter(GameCharacter fighter) {
        if (fighter == null) {
            return false;
        }
        queue.add(fighter);
        return true;
    }

    public GameCharacter generateCharacter(Player player) {
        GameCharacter character = new GameCharacter(nameGen.pickName(), player.getLevel());
        return character;
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
