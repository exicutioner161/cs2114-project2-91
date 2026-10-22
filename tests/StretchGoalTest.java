import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import characters.Item;
import characters.Player;
import tournament.AdaptiveOpponentFactory;
import tournament.BackgroundTournament;
import tournament.SameRoundLossQueue;
import characters.GameCharacter;

class StretchGoalTest {
    @Test
    void adaptiveOpponentCountersPlayerStyles() {
        Player player = new Player("Player", 1, 0, new Item[5]);
        GameCharacter opponent = new AdaptiveOpponentFactory()
            .create(player, "Counter");
        assertEquals(player.getControl() + 1, opponent.getAggro());
        assertEquals(player.getMidrange() + 1, opponent.getControl());
        assertEquals(player.getAggro() + 1, opponent.getMidrange());
        assertThrows(IllegalArgumentException.class,
            () -> new AdaptiveOpponentFactory().create(null, "Counter"));
        assertThrows(IllegalArgumentException.class,
            () -> new AdaptiveOpponentFactory().create(player, ""));
    }

    @Test
    void backgroundTournamentProducesChampionPool() {
        Player player = new Player("Player", 1, 0, new Item[5]);
        BackgroundTournament background = new BackgroundTournament();
        assertTrue(background.simulate(player, 2) != null);
        assertEquals(2, background.simulatePool(player, 2).size());
        assertThrows(IllegalArgumentException.class,
            () -> background.simulate(null, 2));
        assertThrows(IllegalArgumentException.class,
            () -> background.simulatePool(player, -1));
    }

    @Test
    void groupsSameRoundLosses() {
        SameRoundLossQueue losses = new SameRoundLossQueue();
        GameCharacter fighter = new GameCharacter("Lost", 1);
        losses.addLoss(2, fighter);
        assertEquals(fighter, losses.getRound(2).getFirst());
        assertThrows(IndexOutOfBoundsException.class,
            () -> losses.getRound(4));
        assertThrows(IllegalArgumentException.class,
            () -> losses.addLoss(-1, fighter));
        assertThrows(IllegalArgumentException.class,
            () -> losses.addLoss(2, null));
    }
}
