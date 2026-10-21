package gamesaves;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        content.append("name=").append(encode(name)).append('\n');
        content.append("level=").append(player.getLevel()).append('\n');
        content.append("gold=").append(player.getGold()).append('\n');
        content.append("wins=").append(player.getWins()).append('\n');
        content.append("losses=").append(player.getLosses()).append('\n');
        for (int slot = 0; slot < player.getInventory().length; slot++) {
            Item item = player.getItem(slot);
            if (item != null) {
                content.append("item|").append(slot).append('|')
                    .append(encode(item.getName())).append('|').append(item.getAggro())
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
        if (lines.length < 3 || !("name=" + encode(expectedName)).equals(lines[0])) {
            throw new IllegalArgumentException("Save name or format is invalid");
        }
        try {
            int level = Integer.parseInt(requiredValue(lines[1], "level"));
            int gold = Integer.parseInt(requiredValue(lines[2], "gold"));
            int lineIndex = 3;
            int wins = 0;
            int losses = 0;
            if (lineIndex < lines.length && lines[lineIndex].startsWith("wins=")) {
                wins = Integer.parseInt(requiredValue(lines[lineIndex], "wins"));
                lineIndex++;
            }
            if (lineIndex < lines.length && lines[lineIndex].startsWith("losses=")) {
                losses = Integer.parseInt(requiredValue(lines[lineIndex], "losses"));
                lineIndex++;
            }
            if (wins < 0 || losses < 0) {
                throw new IllegalArgumentException("Save counters are invalid");
            }
            Item[] inventory = new Item[5];
            boolean[] occupiedSlots = new boolean[inventory.length];
            for (int i = lineIndex; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    continue;
                }
                String[] fields = lines[i].split("\\|", -1);
                if (fields.length != 7 || !"item".equals(fields[0])) {
                    throw new IllegalArgumentException("Save item format is invalid");
                }
                int slot = Integer.parseInt(fields[1]);
                if (slot < 0 || slot >= inventory.length || occupiedSlots[slot]) {
                    throw new IllegalArgumentException("Save slot is invalid");
                }
                occupiedSlots[slot] = true;
                inventory[slot] = new Item(decode(fields[2]), Double.parseDouble(fields[3]),
                    Double.parseDouble(fields[4]), Double.parseDouble(fields[5]),
                    Integer.parseInt(fields[6]), slot);
            }
            Player player = new Player(expectedName, level, gold, inventory);
            player.setWins(wins);
            player.setLosses(losses);
            return player;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Save values are invalid", exception);
        }
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private String requiredValue(String line, String key) {
        String prefix = key + "=";
        if (!line.startsWith(prefix)) {
            throw new IllegalArgumentException("Save format is invalid");
        }
        return line.substring(prefix.length());
    }
}
