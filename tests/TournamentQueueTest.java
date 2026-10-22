import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import characters.GameCharacter;
import characters.Item;
import characters.Player;
import tournament.TournamentQueue;

class TournamentQueueTest {
    @Test
    void preservesOrderAfterLoss() {
        TournamentQueue queue = new TournamentQueue();
        GameCharacter first = new GameCharacter("First", 1);
        GameCharacter second = new GameCharacter("Second", 1);
        assertTrue(queue.add(first));
        assertTrue(queue.add(second));
        queue.preserveAfterLoss();
        assertEquals(first, queue.removeNext());
        assertEquals(second, queue.removeNext());
    }

    @Test
    void repopulatesStrongerOpponentsAfterWin() {
        TournamentQueue queue = new TournamentQueue();
        Player player = new Player("Player", 1, 0, new Item[5]);
        queue.repopulateAfterWin(player, 2);
        assertEquals(2, queue.size());
        assertEquals(2, queue.get(0).getLevel());
    }

    @Test
    void refillsAfterLossWithoutDiscardingWaitingFighters() {
        TournamentQueue queue = new TournamentQueue();
        Player player = new Player("Player", 1, 0, new Item[5]);
        GameCharacter waiting = new GameCharacter("Waiting", 1);
        queue.add(waiting);
        queue.repopulateAfterLoss(player, 3);
        assertEquals(3, queue.size());
        assertEquals(waiting, queue.removeNext());
    }

    @Test
    void rejectsInvalidQueueActions() {
        TournamentQueue queue = new TournamentQueue();
        assertFalse(queue.add(null));
        assertEquals(null, queue.removeNext());
        assertThrows(IllegalArgumentException.class,
            () -> queue.repopulateAfterWin(null, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> queue.get(0));
        assertThrows(IllegalArgumentException.class,
            () -> queue.repopulateAfterLoss(null, 1));
        assertThrows(IllegalArgumentException.class,
            () -> queue.repopulateAfterLoss(new Player("Player", 1), -1));
    }
}
