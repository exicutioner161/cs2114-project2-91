package main;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import characters.GameCharacter;
import characters.Player;
import gamesaves.GameSave;
import shop.Shop;
import shop.ShopProgression;
import tournament.Tournament;
import tournament.MatchResult;

public class GameDriver {
    public static void main(String[] args) {
        System.out.println("Welcome to the Game!");
        try (Scanner input = new Scanner(System.in)) {
            run(input);
        }
    }

    public static void run(Scanner input) {
        runMenu(input, System.out);
    }

    public static void run(InputStream input, PrintStream output) {
        if (input == null || output == null) {
            throw new IllegalArgumentException("Input and output are required");
        }
        runMenu(new Scanner(input), output);
    }

    private static void runMenu(Scanner input, PrintStream out) {
        int tournamentWins = 0;
        int tournamentLosses = 0;
        final int MAX_WINS = 5;
        final int MAX_LOSSES = 10;
        if (input == null || out == null) {
            throw new IllegalArgumentException("Input and output are required");
        }
        Player player = new Player("Player", 1, 100, new characters.Item[5]);
        ShopProgression progression = new ShopProgression(4);
        GameSave saves = new GameSave();
        Tournament tournament = createTournament(player);
        boolean running = true;

        while (running && input.hasNextLine()) {
            out.println("\n1. View status");
            out.println("2. Buy level gear");
            out.println("3. Fight");
            out.println("4. Exit");
            out.println("5. Save progress");
            out.println("6. Load progress");
            out.print("Choose an option: ");
            Integer choice = parseChoice(input.nextLine().trim(), out);
            if (choice == null) {
                continue;
            }
            switch (choice) {
                case 1:
                    out.println(player.catalogText());
                    out.println("Tournament Wins: " + tournamentWins + "/5");
                    out.println("Tournament Losses: " + tournamentLosses + "/10");
                    break;
                case 2:
                    Shop shop = progression.getShop(player.getLevel());
                    out.println(shop != null
                            && shop.buy(player, "Level " + player.getLevel() + " Sword")
                                    ? "Sword equipped."
                                    : "Unable to buy sword.");
                    break;
                case 3:
                    if (tournament.isFinished()) {
                        out.println("Tournament is complete.");
                        break;
                    }
                    MatchResult result = tournament.playNextMatch();
                    if (result.getWinner() == player) {
                        player.awardGold(5);
                    }
                    out.println("Match winner: " + result.getWinner());
                    out.println("Match loser: " + result.getLoser());
                    out.println(tournament);
                    if (tournament.isFinished()) {
                        out.println("Tournament champion: "
                                + tournament.getChampion());
                        if (tournament.getChampion() == player) {
                            player.incrementWins();
                            player.awardGold(10);
                            if (player.getLevel() < 5) {
                                player.advanceLevel();
                            }
                            tournament = createTournament(player);
                            out.println("Tournament win recorded.");
                        } else {
                            player.incrementLosses();
                            out.println("Tournament loss recorded.");
                        }
                        if (player.getWins() >= MAX_WINS
                                || player.getLosses() >= MAX_LOSSES) {
                            out.println("Run complete.");
                            running = false;
                        }
                    }
                    break;
                case 4:
                    running = false;
                    out.println("Goodbye.");
                    break;
                case 5:
                    try {
                        saves.save("Player", player,
                                Path.of("calculated-combat-player.save"));
                        out.println("Progress saved.");
                    } catch (java.io.IOException exception) {
                        out.println("Unable to save progress.");
                    }
                    break;
                case 6:
                    try {
                        player = saves.load("Player",
                                Path.of("calculated-combat-player.save"));
                        tournament = createTournament(player);
                        out.println("Progress loaded.");
                    } catch (java.io.IOException | IllegalArgumentException exception) {
                        out.println("Unable to load progress.");
                    }
                    break;
                default:
                    out.println("Invalid option. Enter a number from 1 to 6.");
                    break;
            }
        }
    }

    private static Integer parseChoice(String rawChoice, PrintStream output) {
        if (rawChoice.isEmpty()) {
            output.println("Invalid option. Enter a number from 1 to 6.");
            return null;
        }
        try {
            long parsed = Long.parseLong(rawChoice);
            if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE) {
                output.println("Invalid option. Enter a number from 1 to 6.");
                return null;
            }
            return (int) parsed;
        } catch (NumberFormatException exception) {
            output.println("Invalid option. Enter a number from 1 to 6.");
            return null;
        }
    }

    private static Tournament createTournament(Player player) {
        Tournament tournament = new Tournament(player);
        tournament.populateEntrants(player);
        tournament.fillBracket();
        return tournament;
    }
}
