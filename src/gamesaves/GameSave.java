package gamesaves;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameSave {
    private static final String CONTENT = "Calculated Combat save\n";

    public boolean save(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path is required");
        }
        Files.writeString(path, CONTENT);
        return true;
    }

    public boolean save() throws IOException {
        return save(Path.of("calculated-combat-save.txt"));
    }
}
