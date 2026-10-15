package gamesaves;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import characters.Item;
import characters.Player;

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

    public boolean save(String name, Player player, Path path) throws IOException {
        if (name == null || name.isBlank() || player == null || path == null) {
            throw new IllegalArgumentException("Save values are invalid");
        }
        StringBuilder content = new StringBuilder();
        content.append("name=").append(name).append('\n');
        content.append("level=").append(player.getLevel()).append('\n');
        content.append("gold=").append(player.getGold()).append('\n');
        for (int slot = 0; slot < player.getInventory().length; slot++) {
            Item item = player.getItem(slot);
            if (item != null) {
                content.append("item|").append(slot).append('|')
                    .append(item.getName()).append('|').append(item.getAggro())
                    .append('|').append(item.getControl()).append('|')
                    .append(item.getMidrange()).append('|').append(item.getPrice())
                    .append('\n');
            }
        }
        Files.writeString(path, content.toString());
        return true;
    }

    public Player load(String expectedName, Path path) throws IOException {
        if (expectedName == null || expectedName.isBlank() || path == null) {
            throw new IllegalArgumentException("Load values are invalid");
        }
        String[] lines = Files.readString(path).split("\\R");
        if (lines.length < 3 || !("name=" + expectedName).equals(lines[0])) {
            throw new IllegalArgumentException("Save name or format is invalid");
        }
        try {
            int level = Integer.parseInt(requiredValue(lines[1], "level"));
            int gold = Integer.parseInt(requiredValue(lines[2], "gold"));
            Item[] inventory = new Item[5];
            for (int i = 3; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    continue;
                }
                String[] fields = lines[i].split("\\|", -1);
                if (fields.length != 7 || !"item".equals(fields[0])) {
                    throw new IllegalArgumentException("Save item format is invalid");
                }
                int slot = Integer.parseInt(fields[1]);
                inventory[slot] = new Item(fields[2], Double.parseDouble(fields[3]),
                    Double.parseDouble(fields[4]), Double.parseDouble(fields[5]),
                    Integer.parseInt(fields[6]), slot);
            }
            return new Player(expectedName, level, gold, inventory);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("Save values are invalid", exception);
        }
    }

    private String requiredValue(String line, String key) {
        String prefix = key + "=";
        if (!line.startsWith(prefix)) {
            throw new IllegalArgumentException("Save format is invalid");
        }
        return line.substring(prefix.length());
    }
}
