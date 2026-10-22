package gamesaves;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import characters.Item;
import characters.Player;
import tournament.Tournament;

/** Reads and writes named player and optional tournament save files. */
public class GameSave {
    private static final String CONTENT = "Calculated Combat save\n";

    /**
     * Writes the default introductory save format.
     *
     * @param path destination path
     * @return {@code true} after writing
     * @throws IOException if writing fails
     * @throws IllegalArgumentException if path is null
     */
    public boolean save(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path is required");
        }
        Files.writeString(path, CONTENT);
        return true;
    }

    /** @return {@code true} after writing the default save path */
    public boolean save() throws IOException {
        return save(Path.of("calculated-combat-save.txt"));
    }

    /**
     * Saves player progress without a tournament snapshot.
     *
     * @param name save/player name
     * @param player player progress
     * @param path destination path
     * @return {@code true} after writing
     * @throws IOException if writing fails
     */
    public boolean save(String name, Player player, Path path) throws IOException {
        return save(name, player, null, path);
    }

    /**
     * Saves player progress and an optional active tournament.
     *
     * @param name save/player name
     * @param player player progress
     * @param tournament optional tournament snapshot
     * @param path destination path
     * @return {@code true} after writing
     * @throws IOException if writing fails
     * @throws IllegalArgumentException if values are invalid
     */
    public boolean save(String name, Player player, Tournament tournament,
            Path path) throws IOException {
        validateSaveValues(name, player, path);
        StringBuilder content = playerHeader(name, player);
        if (tournament != null) {
            appendTournament(content, tournament);
        }
        appendItems(content, player);
        Files.writeString(path, content.toString());
        return true;
    }

    /**
     * Loads named player progress.
     *
     * @param expectedName expected save name
     * @param path save path
     * @return restored player
     * @throws IOException if reading fails
     * @throws IllegalArgumentException if validation fails
     */
    public Player load(String expectedName, Path path) throws IOException {
        validateLoadValues(expectedName, path);
        String[] lines = Files.readString(path).split("\\R");
        validateHeader(lines, expectedName);
        try {
            return parsePlayer(lines, expectedName);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Save values are invalid", exception);
        }
    }

    private void validateSaveValues(String name, Player player, Path path) {
        if (name == null || name.isBlank() || player == null || path == null) {
            throw new IllegalArgumentException("Save values are invalid");
        }
    }

    private StringBuilder playerHeader(String name, Player player) {
        return new StringBuilder()
            .append("name=").append(encode(name)).append('\n')
            .append("level=").append(player.getLevel()).append('\n')
            .append("gold=").append(player.getGold()).append('\n')
            .append("wins=").append(player.getWins()).append('\n')
            .append("losses=").append(player.getLosses()).append('\n');
    }

    private void appendTournament(StringBuilder content, Tournament tournament) {
        content.append("tournament=").append(encode(tournament.saveState()))
            .append('\n');
    }

    private void appendItems(StringBuilder content, Player player) {
        for (int slot = 0; slot < player.getInventory().length; slot++) {
            Item item = player.getItem(slot);
            if (item != null) {
                appendItem(content, slot, item);
            }
        }
    }

    private void appendItem(StringBuilder content, int slot, Item item) {
        content.append("item|").append(slot).append('|').append(encode(item.getName()))
            .append('|').append(item.getAggro()).append('|').append(item.getControl())
            .append('|').append(item.getMidrange()).append('|').append(item.getPrice())
            .append('\n');
    }

    private void validateLoadValues(String expectedName, Path path) {
        if (expectedName == null || expectedName.isBlank() || path == null) {
            throw new IllegalArgumentException("Load values are invalid");
        }
    }

    private void validateHeader(String[] lines, String expectedName) {
        if (lines.length < 3 || !("name=" + encode(expectedName)).equals(lines[0])) {
            throw new IllegalArgumentException("Save name or format is invalid");
        }
    }

    private Player parsePlayer(String[] lines, String expectedName) {
        int level = Integer.parseInt(requiredValue(lines[1], "level"));
        int gold = Integer.parseInt(requiredValue(lines[2], "gold"));
        int wins = optionalCounter(lines, 3, "wins");
        int losses = optionalCounter(lines, 4, "losses");
        if (wins < 0 || losses < 0) {
            throw new IllegalArgumentException("Save counters are invalid");
        }
        Item[] inventory = parseItems(lines, 3 + counterLines(lines));
        Player player = new Player(expectedName, level, gold, inventory);
        player.setWins(wins);
        player.setLosses(losses);
        return player;
    }

    private int optionalCounter(String[] lines, int index, String key) {
        return index < lines.length && lines[index].startsWith(key + "=")
            ? Integer.parseInt(requiredValue(lines[index], key)) : 0;
    }

    private int counterLines(String[] lines) {
        int count = 0;
        if (lines.length > 3 && lines[3].startsWith("wins=")) {
            count++;
        }
        if (lines.length > 3 + count && lines[3 + count].startsWith("losses=")) {
            count++;
        }
        return count;
    }

    private Item[] parseItems(String[] lines, int start) {
        Item[] inventory = new Item[5];
        boolean[] occupied = new boolean[inventory.length];
        for (int i = start; i < lines.length; i++) {
            if (!lines[i].isBlank() && !lines[i].startsWith("tournament=")) {
                parseItemLine(lines[i], inventory, occupied);
            }
        }
        return inventory;
    }

    private void parseItemLine(String line, Item[] inventory, boolean[] occupied) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 7 || !"item".equals(fields[0])) {
            throw new IllegalArgumentException("Save item format is invalid");
        }
        int slot = Integer.parseInt(fields[1]);
        if (slot < 0 || slot >= inventory.length || occupied[slot]) {
            throw new IllegalArgumentException("Save slot is invalid");
        }
        occupied[slot] = true;
        inventory[slot] = new Item(decode(fields[2]), Double.parseDouble(fields[3]),
            Double.parseDouble(fields[4]), Double.parseDouble(fields[5]),
            Integer.parseInt(fields[6]), slot);
    }

    /**
     * Loads player progress and an optional active tournament.
     *
     * @param expectedName expected save name
     * @param path save path
     * @param random source for future tournament matches
     * @return restored game components
     * @throws IOException if reading fails
     * @throws IllegalArgumentException if validation fails
     */
    public LoadedGame loadGame(String expectedName, Path path, Random random)
            throws IOException {
        if (random == null) {
            throw new IllegalArgumentException("Random source is required");
        }
        Player player = load(expectedName, path);
        String[] lines = Files.readString(path).split("\\R");
        String encodedState = null;
        for (String line : lines) {
            if (line.startsWith("tournament=")) {
                encodedState = line.substring("tournament=".length());
                break;
            }
        }
        if (encodedState == null) {
            return new LoadedGame(player, null);
        }
        return new LoadedGame(player,
            Tournament.restoreState(player, decode(encodedState), random));
    }

    /** Restored player and optional tournament snapshot. */
    public record LoadedGame(Player player, Tournament tournament) {
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
