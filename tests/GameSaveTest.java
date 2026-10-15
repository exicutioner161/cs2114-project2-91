import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import gamesaves.GameSave;

class GameSaveTest {
    @Test
    void savesToRequestedPath() throws Exception {
        Path path = Files.createTempFile("calculated-combat", ".txt");
        try {
            assertTrue(new GameSave().save(path));
            assertEquals("Calculated Combat save\n", Files.readString(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void rejectsNullPath() {
        assertThrows(IllegalArgumentException.class,
            () -> new GameSave().save(null));
    }

    @Test
    void defaultSaveWritesAndCanBeRemoved() throws Exception {
        Path path = Path.of("calculated-combat-save.txt");
        try {
            assertTrue(new GameSave().save());
            assertTrue(Files.exists(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
