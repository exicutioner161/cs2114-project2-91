package main;

import java.nio.file.Path;
import java.util.Scanner;

import characters.Item;
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
        runMenu(input);
    }

    private static void printMenu() {
        String menu = "\n1. View Status"
                + "\n2. Buy gear"
                + "\n3. Fight"
                + "\n4. Exit"
                + "\n5. Save progress"
                + "\n6. Load progress"
                + "\n7. Sell gear"
                + "\nChoose an option: ";
        System.out.println(menu);
    }

    private static void runMenu(Scanner input) {
        final int MAX_WINS = 5;
        final int MAX_LOSSES = 10;
        if (input == null) {
            throw new IllegalArgumentException("Input required");
        }
        Player player = new Player("Player", 1, 100, new Item[5]);
        ShopProgression progression = new ShopProgression(4);
        GameSave saves = new GameSave();
        Tournament tournament = createTournament(player);
        boolean running = true;

        while (running && input.hasNextLine()) {
            printMenu();
            Integer choice = parseChoice(input.nextLine().trim());
            if (choice == null) {
                continue;
            }
            switch (choice) {
                case 1 ->
                    System.out.println(player.catalogText() + "\nTournament Wins: "
                            + player.getWins() + "/5"
                            + "\nTournament Losses: " + player.getLosses() + "/10");
                case 2 -> {
                    Shop shop = progression.getShop(player.getLevel());
                    if (shop == null) {
                        System.out.println("No shop is unlocked for this level.");
                        break;
                    }
                    System.out.println(shop.catalogText());
                    System.out.print("Enter the exact item name to buy: ");
                    String itemName = input.hasNextLine() ? input.nextLine().trim() : "";
                    System.out.println(shop.buy(player, itemName)
                            ? "Item equipped."
                            : "Unable to buy item.");
                }
                case 3 -> {
                    if (tournament.isFinished()) {
                        System.out.println("Tournament is complete.");
                    } else {
                        MatchResult result = tournament.playNextMatch();
                        if (result.getWinner() == player) {
                            player.awardGold(5);
                        }
                        System.out.println("Match winner: " + result.getWinner());
                        System.out.println("Match loser: " + result.getLoser());
                        System.out.println(tournament);
                        if (tournament.isFinished()) {
                            System.out.println("Tournament champion: "
                                    + tournament.getChampion());
                            if (tournament.getChampion() == player) {
                                player.incrementWins();
                                player.awardGold(10);
                                if (player.getLevel() < 5) {
                                    player.advanceLevel();
                                }
                                tournament = createTournament(player);
                                System.out.println("Tournament win recorded.");
                            } else {
                                player.incrementLosses();
                                System.out.println("Tournament loss recorded.");
                            }
                            if (player.getWins() >= MAX_WINS
                                    || player.getLosses() >= MAX_LOSSES) {
                                System.out.println("Run complete.");
                                running = false;
                            }
                        }
                    }
                }
                case 4 -> {
                    running = false;
                    System.out.println("Goodbye.");
                }
                case 5 -> {
                    try {
                        saves.save("Player", player,
                                Path.of("calculated-combat-player.save"));
                        System.out.println("Progress saved.");
                    } catch (java.io.IOException exception) {
                        System.out.println("Unable to save progress.");
                    }
                }
                case 6 -> {
                    try {
                        player = saves.load("Player",
                                Path.of("calculated-combat-player.save"));
                        tournament = createTournament(player);
                        System.out.println("Progress loaded.");
                    } catch (java.io.IOException | IllegalArgumentException exception) {
                        System.out.println("Unable to load progress.");
                    }
                }
                case 7 -> {
                    System.out.print("Enter equipment slot (0-4) to sell: ");
                    String rawSlot = input.hasNextLine() ? input.nextLine().trim() : "";
                    try {
                        int slot = Integer.parseInt(rawSlot);
                        System.out.println(player.sell(slot)
                                ? "Item sold."
                                : "Unable to sell item.");
                    } catch (NumberFormatException exception) {
                        System.out.println("Invalid equipment slot.");
                    }
                }
                default -> {
                    System.out.println("Invalid option. Enter a number from 1 to 6.");
                }
            }
        }
    }

    private static Integer parseChoice(String rawChoice) {
        if (rawChoice.isEmpty()) {
            System.out.println("Invalid option. Enter a number from 1 to 6.");
            return null;
        }
        try {
            long parsed = Long.parseLong(rawChoice);
            if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE) {
                System.out.println("Invalid option. Enter a number from 1 to 6.");
                return null;
            }
            return (int) parsed;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid option. Enter a number from 1 to 6.");
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
