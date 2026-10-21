package tournament;

import characters.GameCharacter;
import characters.Player;

/**
 * Creates an opponent whose style distribution counters the player's build.
 */
public class AdaptiveOpponentFactory {
    public GameCharacter create(Player player, String name) {
        if (player == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Opponent values are invalid");
        }
        return new GameCharacter(name, player.getLevel(),
            player.getControl() + 1, player.getMidrange() + 1,
            player.getAggro() + 1);
    }
}
