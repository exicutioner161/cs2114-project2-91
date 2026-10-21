
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;

import characters.GameCharacter;
import characters.Player;
import characters.Item;
import tournament.Tournament;
import tournament.MatchResult;

class TournamentTest {

    private Player player;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        player = new Player(
            "Player", 1, 100, new Item[5]);

        tournament = new Tournament(player);
    }

    @Test
    void testPlayerAutomaticallyRegistered() {
        assertEquals(1, tournament.size());
    }

    @Test
    void testAddFighter() {
        GameCharacter opponent =
            new GameCharacter("Opponent", 1);

        assertTrue(tournament.addFighter(opponent));
        assertEquals(2, tournament.size());
    }

    @Test
    void testRejectNullFighter() {
        assertFalse(tournament.addFighter(null));
        assertEquals(1, tournament.size());
    }

    @Test
    void testRejectDuplicateFighter() {
        GameCharacter opponent =
            new GameCharacter("Opponent", 1);

        assertTrue(tournament.addFighter(opponent));
        assertFalse(tournament.addFighter(opponent));

        assertEquals(2, tournament.size());
    }

    @Test
    void testRejectNinthFighter() {
        for (int i = 0; i < 7; i++) {
            assertTrue(tournament.addFighter(
                new GameCharacter("Opponent " + i, 1)));
        }

        assertEquals(8, tournament.size());

        assertFalse(tournament.addFighter(
            new GameCharacter("Extra", 1)));

        assertEquals(8, tournament.size());
    }

    @Test
    void testFillBracketRequiresEightFighters() {
        assertFalse(tournament.fillBracket());

        assertEquals(1, tournament.size());
        assertEquals(0, tournament.winnersSize());
    }

    @Test
    void testFillBracket() {
        registerSevenOpponents();

        assertTrue(tournament.fillBracket());

        assertEquals(8, tournament.size());
        assertEquals(8, tournament.winnersSize());
        assertEquals(0, tournament.losersSize());
    }

    @Test
    void testCannotFillBracketTwice() {
        registerSevenOpponents();

        assertTrue(tournament.fillBracket());
        assertFalse(tournament.fillBracket());

        assertEquals(8, tournament.winnersSize());
    }

    @Test
    void testCannotAddFighterAfterTournamentStarts() {
        registerSevenOpponents();

        assertTrue(tournament.fillBracket());

        assertFalse(tournament.addFighter(
            new GameCharacter("Late Fighter", 1)));
    }

    @Test
    void testCannotPlayBeforeBracketIsFilled() {
        assertThrows(IllegalStateException.class,
            () -> tournament.playNextMatch());
    }

    @Test
    void testOpeningRound() {
        registerSevenOpponents();

        assertTrue(tournament.fillBracket());

        for (int i = 0; i < 4; i++) {
            MatchResult result =
                tournament.playNextMatch();

            assertNotNull(result);
            assertNotNull(result.getWinner());
            assertNotNull(result.getLoser());

            assertNotSame(
                result.getWinner(),
                result.getLoser());

            assertEquals(
                1,
                tournament.getLossCount(
                    result.getLoser()));
        }

        assertEquals(4, tournament.winnersSize());
        assertEquals(4, tournament.losersSize());
        assertEquals(8, tournament.size());
    }
    @Test
    void testLosersRound() {
        registerSevenOpponents();
        tournament.fillBracket();

        // Complete opening round
        for (int i = 0; i < 4; i++) {
            tournament.playNextMatch();
        }

        assertEquals(4, tournament.winnersSize());
        assertEquals(4, tournament.losersSize());

        // Complete first losers round
        for (int i = 0; i < 2; i++) {
            tournament.playNextMatch();
        }

        assertEquals(4, tournament.winnersSize());
        assertEquals(2, tournament.losersSize());
        assertEquals(6, tournament.winnersSize()+tournament.losersSize());
    }

    @Test
    void completesAllDoubleEliminationPhasesWithoutReset() {
        registerSevenOpponents();
        tournament.fillBracket();
        int matches = 0;
        while (!tournament.isFinished()) {
            MatchResult result = tournament.playNextMatch();
            assertNotSame(result.getWinner(), result.getLoser());
            assertTrue(tournament.getLossCount(result.getLoser()) <= 2,
                result.getLoser().getName() + " lost "
                    + tournament.getLossCount(result.getLoser()) + " times in phase "
                    + result.getPhase());
            matches++;
            assertTrue(matches <= 14);
        }
        assertEquals(14, matches);
        assertNotNull(tournament.getChampion());
    }

    @Test
    void rejectsPlayingAfterTournamentFinishes() {
        registerSevenOpponents();
        tournament.fillBracket();
        while (!tournament.isFinished()) {
            tournament.playNextMatch();
        }
        assertThrows(IllegalStateException.class,
            () -> tournament.playNextMatch());
    }

    @Test
    void toStringDisplaysAsciiBracketState() {
        registerSevenOpponents();
        String text = tournament.toString();
        assertTrue(text.contains("TOURNAMENT BRACKET"));
        assertTrue(text.contains("Winners Bracket"));
        assertTrue(text.contains("Losers Bracket"));
        assertTrue(text.contains("--+"));
        assertTrue(text.contains("Champion: pending"));
    }

    @Test
    void seededTournamentConstructorProducesRepeatableMatch() {
        GameCharacter first = new GameCharacter("First", 1, 5, 1, 1);
        GameCharacter second = new GameCharacter("Second", 1, 1, 5, 1);
        assertEquals(
            new tournament.TournamentRound(first, second, new Random(7))
                .getWinner().getName(),
            new tournament.TournamentRound(first, second, new Random(7))
                .getWinner().getName());
    }

    private void registerSevenOpponents() {
        for (int i = 0; i < 7; i++) {
            tournament.addFighter(
                new GameCharacter("Opponent " + i, 1));
        }
    }
}
