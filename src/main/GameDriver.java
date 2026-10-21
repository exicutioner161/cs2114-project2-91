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
        boolean firstChoice = true;

        while (running && input.hasNextLine()) {
            printMenu();
            String rawChoice = input.nextLine().trim();
            if (firstChoice && rawChoice.isEmpty()) {
                firstChoice = false;
                continue;
            }
            firstChoice = false;
            Integer choice = parseChoice(rawChoice);
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
                    buyGear(input, shop, player);
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
                        saves.save("Player", player, tournament,
                                Path.of("calculated-combat-player.save"));
                        System.out.println("Progress saved.");
                    } catch (java.io.IOException exception) {
                        System.out.println("Unable to save progress.");
                    }
                }
                case 6 -> {
                    try {
                        GameSave.LoadedGame loaded = saves.loadGame("Player",
                                Path.of("calculated-combat-player.save"),
                                new java.util.Random());
                        player = loaded.player();
                        tournament = loaded.tournament() == null
                                ? createTournament(player) : loaded.tournament();
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
                    System.out.println("Invalid option. Enter a number from 1 to 7.");
                }
            }
            if (running) {
                pauseAfterAction(input);
            }
        }
    }

    private static Integer parseChoice(String rawChoice) {
        if (rawChoice.isEmpty()) {
            System.out.println("Invalid option. Enter a number from 1 to 7.");
            return null;
        }

        try {
            long parsed = Long.parseLong(rawChoice);
            if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE) {
                System.out.println("Invalid option. Enter a number from 1 to 7.");
                return null;
            }
            return (int) parsed;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid option. Enter a number from 1 to 7.");
            return null;
        }
    }

    private static void pauseAfterAction(Scanner input) {
        System.out.println("Press Enter to continue.");
        if (input.hasNextLine()) {
            input.nextLine();
        }
    }

    private static void buyGear(Scanner input, Shop shop, Player player) {
        while (input.hasNextLine()) {
            printCategoryMenu();
            Integer category = parseBoundedChoice(input.nextLine().trim(), 0, 5,
                    "category");
            if (category == null) {
                continue;
            }
            if (category == 0) {
                return;
            }
            buyCategoryItem(input, shop, player, category - 1);
            return;
        }
    }

    private static void printCategoryMenu() {
        System.out.println("\nChoose an equipment category:"
                + "\n1. Head"
                + "\n2. Body"
                + "\n3. Legs"
                + "\n4. Feet"
                + "\n5. Weapon"
                + "\n0. Return to main menu");
        System.out.print("Choose a category: ");
    }

    private static void buyCategoryItem(Scanner input, Shop shop, Player player,
            int slot) {
        while (input.hasNextLine()) {
            int itemCount = printCategoryItems(shop, slot);
            Integer itemChoice = parseBoundedChoice(input.nextLine().trim(), 0,
                    itemCount, "item");
            if (itemChoice == null) {
                continue;
            }
            if (itemChoice == 0) {
                return;
            }
            Item item = itemAtCategoryIndex(shop, slot, itemChoice - 1);
            System.out.println(player.buy(item)
                    ? "Item equipped."
                    : "Unable to buy item.");
            return;
        }
    }

    private static int printCategoryItems(Shop shop, int slot) {
        int choice = 1;
        System.out.println("\n" + categoryName(slot) + " items:");
        for (int i = 0; i < shop.size(); i++) {
            Item item = shop.getItem(i);
            if (item.getType() == slot) {
                System.out.println(choice++ + ". " + item.getName()
                        + " | A/C/M: " + item.getAggro() + "/"
                        + item.getControl() + "/" + item.getMidrange()
                        + " | Price: " + item.getPrice());
            }
        }
        System.out.println("0. Return to category menu");
        System.out.print("Choose an item: ");
        return choice - 1;
    }

    private static Item itemAtCategoryIndex(Shop shop, int slot, int categoryIndex) {
        int currentIndex = 0;
        for (int i = 0; i < shop.size(); i++) {
            Item item = shop.getItem(i);
            if (item.getType() == slot) {
                if (currentIndex == categoryIndex) {
                    return item;
                }
                currentIndex++;
            }
        }
        throw new IllegalArgumentException("Category item index is invalid");
    }

    private static String categoryName(int slot) {
        return switch (slot) {
            case 0 -> "Head";
            case 1 -> "Body";
            case 2 -> "Legs";
            case 3 -> "Feet";
            case 4 -> "Weapon";
            default -> "Unknown";
        };
    }

    private static Integer parseBoundedChoice(String rawChoice, int minimum,
            int maximum, String choiceType) {
        Integer choice = parseChoice(rawChoice);
        if (choice == null || choice < minimum || choice > maximum) {
            System.out.println("Invalid " + choiceType
                    + ". Please choose a valid option.");
            return null;
        }
        return choice;
    }

    private static Tournament createTournament(Player player) {
        Tournament tournament = new Tournament(player);
        tournament.populateEntrants(player);
        tournament.fillBracket();
        return tournament;
    }
}
