import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import characters.GameCharacter;

class GameCharacterTest {
    @Test
    void constructorsAndGettersExposeCharacter() {
        GameCharacter character = new GameCharacter("Hero", 2, 1, 2, 3);
        assertEquals("Hero", character.getName());
        assertEquals(2, character.getLevel());
        assertEquals(1, character.getAggro());
        assertEquals(2, character.getControl());
        assertEquals(3, character.getMidrange());
        assertEquals(15, new GameCharacter("Generated", 0).getAggro()
            + new GameCharacter("Generated", 0).getControl()
            + new GameCharacter("Generated", 0).getMidrange(), 0.0001);
    }

    @Test
    void constructorsRejectBadInput() {
        assertThrows(IllegalArgumentException.class,
            () -> new GameCharacter("", 0));
        assertThrows(IllegalArgumentException.class,
            () -> new GameCharacter("Hero", -1));
        assertThrows(IllegalArgumentException.class,
            () -> new GameCharacter("Hero", 0, -1, 1, 1));
    }
}
