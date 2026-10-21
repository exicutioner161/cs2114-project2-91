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
        System.out.println("Welcome to Calculated Combat!");
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
        if (input == null) {
            throw new IllegalArgumentException("Input required");
        }
        GameState state = new GameState();
        boolean running = true;
        boolean firstChoice = true;

        while (running && input.hasNextLine()) {
            printMenu();
            Integer choice = readChoice(input, firstChoice);
            firstChoice = false;
            if (choice == null) {
                continue;
            }
            running = handleChoice(input, state, choice);
        }
    }

    private static Integer readChoice(Scanner input, boolean firstChoice) {
        String rawChoice = input.nextLine().trim();
        if (firstChoice && rawChoice.isEmpty()) {
            return null;
        }
        return parseChoice(rawChoice);
    }

    private static boolean handleChoice(Scanner input, GameState state,
            int choice) {
        switch (choice) {
            case 1 -> showStatus(state.player);
            case 2 -> buyGearFromShop(input, state);
            case 3 -> playFight(input, state);
            case 4 -> {
                System.out.println("Goodbye.");
                return false;
            }
            case 5 -> saveProgress(state);
            case 6 -> loadProgress(state);
            case 7 -> sellGear(input, state.player);
            default -> System.out.println("Invalid option. Enter a number from 1 to 7.");
        }
        pauseAfterAction(input);
        return state.running;
    }

    private static void showStatus(Player player) {
        System.out.println(player.catalogText() + "\nTournament Wins: "
                + player.getWins() + "/5"
                + "\nTournament Losses: " + player.getLosses() + "/10");
    }

    private static void buyGearFromShop(Scanner input, GameState state) {
        Shop shop = state.progression.getShop(state.player.getLevel());
        if (shop == null) {
            System.out.println("No shop is unlocked for this level.");
            return;
        }
        buyGear(input, shop, state.player);
    }

    private static void playFight(Scanner input, GameState state) {
        if (state.tournament.isFinished()) {
            System.out.println("Tournament is complete.");
            return;
        }
        MatchResult result = state.tournament.playNextMatch();
        awardMatchGold(result, state.player);
        printMatchResult(result, state.tournament);
        if (state.tournament.isFinished()) {
            finishTournament(state);
        }
    }

    private static void awardMatchGold(MatchResult result, Player player) {
        if (result.getWinner() == player) {
            player.awardGold(5);
        }
    }

    private static void printMatchResult(MatchResult result, Tournament tournament) {
        System.out.println("Match winner: " + result.getWinner());
        System.out.println("Match loser: " + result.getLoser());
        System.out.println(tournament);
    }

    private static void finishTournament(GameState state) {
        System.out.println("Tournament champion: " + state.tournament.getChampion());
        if (state.tournament.getChampion() == state.player) {
            recordTournamentWin(state);
        } else {
            state.player.incrementLosses();
            System.out.println("Tournament loss recorded.");
        }
        if (runIsComplete(state.player)) {
            System.out.println("Run complete.");
            state.running = false;
        }
    }

    private static void recordTournamentWin(GameState state) {
        state.player.incrementWins();
        state.player.awardGold(10);
        if (state.player.getLevel() < 5) {
            state.player.advanceLevel();
        }
        state.tournament = createTournament(state.player);
        System.out.println("Tournament win recorded.");
    }

    private static boolean runIsComplete(Player player) {
        return player.getWins() >= 5 || player.getLosses() >= 10;
    }

    private static void saveProgress(GameState state) {
        try {
            state.saves.save("Player", state.player, state.tournament,
                    Path.of("calculated-combat-player.save"));
            System.out.println("Progress saved.");
        } catch (java.io.IOException exception) {
            System.out.println("Unable to save progress.");
        }
    }

    private static void loadProgress(GameState state) {
        try {
            GameSave.LoadedGame loaded = state.saves.loadGame("Player",
                    Path.of("calculated-combat-player.save"),
                    new java.util.Random());
            state.player = loaded.player();
            state.tournament = loaded.tournament() == null
                    ? createTournament(state.player)
                    : loaded.tournament();
            System.out.println("Progress loaded.");
        } catch (java.io.IOException | IllegalArgumentException exception) {
            System.out.println("Unable to load progress.");
        }
    }

    private static void sellGear(Scanner input, Player player) {
        System.out.print("Enter equipment slot (0-4) to sell: ");
        String rawSlot = input.hasNextLine() ? input.nextLine().trim() : "";
        try {
            int slot = Integer.parseInt(rawSlot);
            System.out.println(player.sell(slot) ? "Item sold." : "Unable to sell item.");
        } catch (NumberFormatException exception) {
            System.out.println("Invalid equipment slot.");
        }
    }

    private static class GameState {
        private Player player = new Player("Player", 1, 100, new Item[5]);
        private final ShopProgression progression = new ShopProgression(4);
        private final GameSave saves = new GameSave();
        private Tournament tournament = createTournament(player);
        private boolean running = true;
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
