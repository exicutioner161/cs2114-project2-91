package tournament;

import characters.GameCharacter;
import characters.Player;
import linkedlist.CustomLinkedList;

/**
 * Simulates a tournament used to populate an opponent pool.
 */
public class BackgroundTournament {
    public GameCharacter simulate(Player player, int opponentLevel) {
        if (player == null || opponentLevel < 0) {
            throw new IllegalArgumentException("Background tournament values are invalid");
        }
        Tournament tournament = new Tournament(player);
        for (int i = 0; i < 7; i++) {
            tournament.addFighter(new GameCharacter("Background " + i,
                opponentLevel));
        }
        tournament.fillBracket();
        while (!tournament.isFinished()) {
            tournament.playNextMatch();
        }
        return tournament.getChampion();
    }

    public CustomLinkedList<GameCharacter> simulatePool(Player player,
            int tournaments) {
        if (player == null || tournaments < 0) {
            throw new IllegalArgumentException("Background pool values are invalid");
        }
        CustomLinkedList<GameCharacter> winners = new CustomLinkedList<>();
        for (int i = 0; i < tournaments; i++) {
            winners.add(simulate(player, player.getLevel() + 1));
        }
        return winners;
    }
}
