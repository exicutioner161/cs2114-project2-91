package main;

import java.util.Scanner;

import characters.GameCharacter;
import characters.Player;
import shop.Shop;
import tournament.Tournament;

public class GameDriver {
    public static void main(String[] args) {
        System.out.println("Welcome to the Game!\nPress any key to start.");
        try (Scanner input = new Scanner(System.in)) {
            run(input);
        }
    }

    public static void run(Scanner input) {
        Player player = new Player("Player", 0, 100, new characters.Item[5]);
        Shop shop = new Shop();
        Tournament tournament = new Tournament();
        tournament.addFighter(new GameCharacter("Opponent A", 0));
        tournament.addFighter(new GameCharacter("Opponent B", 0));
        boolean running = true;
        while (running && input.hasNextLine()) {
            System.out.println("\n1. View status");
            System.out.println("2. Buy sword");
            System.out.println("3. Fight");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String rawChoice = input.nextLine().trim();
            Integer choice = parseChoice(rawChoice);
            if (choice == null) {
                continue;
            }
            switch (choice) {
                case 1 ->
                    System.out.println(player.catalogText());
                case 2 -> {
                    System.out.println(shop.buy(player, "Bastard Sword")
                            ? "Sword equipped."
                            : "Unable to buy sword.");
                }

                case 3 -> {
                    GameCharacter winner = tournament.runMatch(player);
                    System.out.println(winner == null ? "No opponent is waiting."
                            : "Match winner: " + winner.getName());
                }

                case 4 -> {
                    running = false;
                    System.out.println("Goodbye.");
                }

                default ->
                    System.out.println("Invalid option. Enter a number from 1 to 4.");
            }
        }
    }

    private static Integer parseChoice(String rawChoice) {
        if (rawChoice.isEmpty()) {
            System.out.println("Invalid option. Enter a number from 1 to 4.");
            return null;
        }
        try {
            long parsed = Long.parseLong(rawChoice);
            if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE) {
                System.out.println("Invalid option. Enter a number from 1 to 4.");
                return null;
            }
            return (int) parsed;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid option. Enter a number from 1 to 4.");
            return null;
        }
    }
}
