import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import characters.GameCharacter;
import tournament.Tournament;
import tournament.TournamentRound;

class TournamentTest {
    @Test
    void usesGenericQueueForFightersAndRunsMatch() {
        GameCharacter player = new GameCharacter("Player", 0, 10, 1, 1);
        GameCharacter opponent = new GameCharacter("Opponent", 0, 1, 1, 10);
        Tournament tournament = new Tournament();
        assertTrue(tournament.addFighter(opponent));
        assertEquals(1, tournament.size());
        assertNotNull(tournament.runMatch(player));
        assertEquals(0, tournament.size());
    }

    @Test
    void invalidAndEmptyQueueActionsAreSafe() {
        Tournament tournament = new Tournament();
        assertFalse(tournament.addFighter(null));
        assertNull(tournament.removeNextFighter());
        assertNull(tournament.runMatch(null));
        assertThrows(IllegalArgumentException.class,
            () -> new TournamentRound(null, new GameCharacter("A", 0)));
    }

    @Test
    void seededRoundHasBoundedProbabilityAndWinner() {
        GameCharacter first = new GameCharacter("First", 0, 8, 2, 1);
        GameCharacter second = new GameCharacter("Second", 0, 1, 2, 8);
        TournamentRound round = new TournamentRound(first, second, new Random(1));
        assertTrue(round.getFighterOneWinProbability() >= 0);
        assertTrue(round.getFighterOneWinProbability() <= 1);
        assertNotNull(round.getWinner());
    }

    @Test
    void removesNextFighterInQueueOrder() {
        GameCharacter first = new GameCharacter("First", 0);
        GameCharacter second = new GameCharacter("Second", 0);
        Tournament tournament = new Tournament();
        tournament.addFighter(first);
        tournament.addFighter(second);
        assertEquals(first, tournament.removeNextFighter());
        assertEquals(second, tournament.removeNextFighter());
    }
}
