package tournament;

import characters.GameCharacter;
import characters.Player;
import linkedlist.CustomLinkedList;

/**
 * Simulates a tournament used to populate an opponent pool.
 */
public class BackgroundTournament {
    /**
     * Simulates a complete background tournament.
     *
     * @param player player included in the tournament
     * @param opponentLevel level for generated opponents
     * @return the background tournament champion
     * @throws IllegalArgumentException if arguments are invalid
     */
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

    /**
     * Simulates multiple background tournaments.
     *
     * @param player player used for each tournament
     * @param tournaments number of tournaments
     * @return champions in simulation order
     * @throws IllegalArgumentException if arguments are invalid
     */
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
