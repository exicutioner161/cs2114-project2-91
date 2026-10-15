package main;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import characters.GameCharacter;
import characters.Player;
import shop.Shop;
import tournament.Tournament;

public class GameDriver {
    public static void main(String[] args) {
        run(System.in, System.out);
    }

    public static void run(InputStream input, PrintStream output) {
        if (input == null || output == null) {
            throw new IllegalArgumentException("Input and output are required");
        }
        Player player = new Player("Player", 0, 100, new characters.Item[5]);
        Shop shop = new Shop();
        Tournament tournament = new Tournament();
        tournament.addFighter(new GameCharacter("Opponent A", 0));
        tournament.addFighter(new GameCharacter("Opponent B", 0));
        Scanner scanner = new Scanner(input);
        boolean running = true;
        while (running && scanner.hasNextLine()) {
            output.println("\n1. View status");
            output.println("2. Buy sword");
            output.println("3. Fight");
            output.println("4. Exit");
            output.print("Choose an option: ");
            String rawChoice = scanner.nextLine().trim();
            Integer choice = parseChoice(rawChoice, output);
            if (choice == null) {
                continue;
            }
            switch (choice) {
                case 1:
                    output.println(player.catalogText());
                    break;
                case 2:
                    output.println(shop.buy(player, "Bastard Sword")
                        ? "Sword equipped."
                        : "Unable to buy sword.");
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
                default:
                    output.println("Invalid option. Enter a number from 1 to 4.");
            }
        }
    }

    private static Integer parseChoice(String rawChoice, PrintStream output) {
        if (rawChoice.isEmpty()) {
            output.println("Invalid option. Enter a number from 1 to 4.");
            return null;
        }
        try {
            long parsed = Long.parseLong(rawChoice);
            if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE) {
                output.println("Invalid option. Enter a number from 1 to 4.");
                return null;
            }
            return (int) parsed;
        } catch (NumberFormatException exception) {
            output.println("Invalid option. Enter a number from 1 to 4.");
            return null;
        }
    }
}
