import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import characters.Item;
import characters.Player;
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

    @Test
    void savesAndLoadsNamedPlayerProgress() throws Exception {
        Path path = Files.createTempFile("calculated-combat-progress", ".txt");
        try {
            Player original = new Player("Hero|One\n", 2, 75, new Item[5]);
            original.addItem(new Item("Sword|One\n", 3, 1, 0, 40, 4));
            original.setWins(2);
            original.setLosses(3);
            GameSave save = new GameSave();
            assertTrue(save.save("Hero|One\n", original, path));
            Player restored = save.load("Hero|One\n", path);
            assertEquals(2, restored.getLevel());
            assertEquals(75, restored.getGold());
            assertEquals(2, restored.getWins());
            assertEquals(3, restored.getLosses());
            assertEquals("Sword|One\n", restored.getItem(4).getName());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void rejectsMissingOrMismatchedNamedProgress() throws Exception {
        Path path = Files.createTempFile("calculated-combat-invalid", ".txt");
        try {
            GameSave save = new GameSave();
            assertThrows(IllegalArgumentException.class,
                () -> save.save("", new Player("Hero", 0), path));
            Files.writeString(path, "name=Other\nlevel=0\ngold=1\n");
            assertThrows(IllegalArgumentException.class,
                () -> save.load("Hero", path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void rejectsDuplicateSaveSlots() throws Exception {
        Path path = Files.createTempFile("calculated-combat-duplicate", ".txt");
        try {
            Files.writeString(path, "name=SGVybw==\nlevel=0\ngold=1\n"
                + "item|4|U3dvcmQ=|1|1|1|10\n"
                + "item|4|U2hpZWxk|1|1|1|10\n");
            assertThrows(IllegalArgumentException.class,
                () -> new GameSave().load("Hero", path));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
