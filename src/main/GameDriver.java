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
        if (input == null) {
            throw new IllegalArgumentException("Input is required");
        }
        runMenu(new Scanner(input), output);
    }

    private static void runMenu(Scanner input, PrintStream output) {
        if (input == null || output == null) {
            throw new IllegalArgumentException("Input and output are required");
        }
        Player player = new Player("Player", 0, 100, new characters.Item[5]);
        ShopProgression progression = new ShopProgression(3);
        GameSave saves = new GameSave();
        Tournament tournament = new Tournament();
        tournament.addFighter(new GameCharacter("Opponent A", 0));
        tournament.addFighter(new GameCharacter("Opponent B", 0));
        boolean running = true;
        while (running && input.hasNextLine()) {
            output.println("\n1. View status");
            output.println("2. Buy level gear");
            output.println("3. Fight");
            output.println("4. Exit");
            output.println("5. Save progress");
            output.println("6. Load progress");
            output.print("Choose an option: ");
            Integer choice = parseChoice(input.nextLine().trim(), output);
            if (choice == null) {
                continue;
            }
            switch (choice) {
                case 1:
                    output.println(player.catalogText());
                    break;
                case 2:
                    Shop shop = progression.getShop(player.getLevel());
                    output.println(shop != null
                        && shop.buy(player, "Level " + player.getLevel() + " Sword")
                        ? "Sword equipped." : "Unable to buy sword.");
                    break;
                case 3:
                    GameCharacter winner = tournament.runMatch(player);
                    output.println(winner == null ? "No opponent is waiting."
                        : "Match winner: " + winner.getName());
                    break;
                case 4:
                    running = false;
                    output.println("Goodbye.");
                    break;
                case 5:
                    try {
                        saves.save("Player", player,
                            Path.of("calculated-combat-player.save"));
                        output.println("Progress saved.");
                    } catch (java.io.IOException exception) {
                        output.println("Unable to save progress.");
                    }
                    break;
                case 6:
                    try {
                        player = saves.load("Player",
                            Path.of("calculated-combat-player.save"));
                        output.println("Progress loaded.");
                    } catch (java.io.IOException | IllegalArgumentException exception) {
                        output.println("Unable to load progress.");
                    }
                    break;
                default:
                    output.println("Invalid option. Enter a number from 1 to 6.");
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
}
