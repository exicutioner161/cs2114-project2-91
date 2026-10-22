package main;

import java.nio.file.Path;
import java.util.Scanner;
import characters.GameCharacter;
import characters.Item;
import characters.Player;
import gamesaves.GameSave;
import shop.Shop;
import shop.ShopProgression;
import tournament.AdaptiveOpponentFactory;
import tournament.BackgroundTournament;
import tournament.Tournament;
import tournament.MatchResult;
import tournament.TournamentQueue;
import linkedlist.CustomLinkedList;

/** Console entry point for the Calculated Combat game. */
public class GameDriver {
    private static final int MAX_LEVEL = 5;
    private static final int MATCH_REWARD = 15;
    private static final int TOURNAMENT_REWARD = 30;
    /** Starts an interactive game using standard input and output. */
    public static void main(String[] args) {
        System.out.println("Welcome to Calculated Combat!");
        try (Scanner input = new Scanner(System.in)) {
            run(input);
        }
    }

    /**
     * Runs the interactive menu with the supplied input source.
     *
     * @param input source of menu and confirmation lines
     * @throws IllegalArgumentException if input is null
     */
    public static void run(Scanner input) {
        runMenu(input);
    }

    /** Prints the top-level action menu. */
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

    /** Runs the menu loop and owns the current game state. */
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

    /** Reads and parses one top-level menu choice. */
    private static Integer readChoice(Scanner input, boolean firstChoice) {
        String rawChoice = input.nextLine().trim();
        if (firstChoice && rawChoice.isEmpty()) {
            return null;
        }
        return parseChoice(rawChoice);
    }

    /** Dispatches one valid menu choice before the next menu. */
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
        return state.running;
    }

    /** Displays player progress and tournament counters. */
    private static void showStatus(Player player) {
        System.out.println(player.catalogText() + "\nTournament Wins: "
                + player.getWins() + "/5"
                + "\nTournament Losses: " + player.getLosses() + "/10");
    }

    /** Opens the current-level category-based shop flow. */
    private static void buyGearFromShop(Scanner input, GameState state) {
        Shop shop = state.progression.getShop(state.player.getLevel());
        if (shop == null) {
            System.out.println("No shop is unlocked for this level.");
            return;
        }
        buyGear(input, shop, state.player);
    }

    /** Plays one match and processes its tournament consequences. */
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

    /** Awards the per-match reward when the player wins. */
    private static void awardMatchGold(MatchResult result, Player player) {
        if (result.getWinner() == player) {
            player.awardGold(MATCH_REWARD);
        }
    }

    /** Prints match participants, outcome, and the current bracket. */
    private static void printMatchResult(MatchResult result, Tournament tournament) {
        System.out.println("Match winner: " + result.getWinner());
        System.out.println("Match loser: " + result.getLoser());
        System.out.println(tournament);
    }

    /** Records the tournament champion and checks run termination. */
    private static void finishTournament(GameState state) {
        System.out.println("Tournament champion: " + state.tournament.getChampion());
        if (state.tournament.getChampion() == state.player) {
            recordTournamentWin(state);
        } else {
            state.player.incrementLosses();
            System.out.println("Tournament loss recorded.");
            state.queue.preserveAfterLoss();
            state.queue.repopulateAfterLoss(state.player, 14);
            state.tournament = createTournament(state.player, state.queue);
        }
        if (runIsComplete(state.player)) {
            System.out.println("Run complete.");
            state.running = false;
        }
    }

    /** Applies rewards and starts the next tournament after a win. */
    private static void recordTournamentWin(GameState state) {
        state.player.incrementWins();
        state.player.awardGold(TOURNAMENT_REWARD);
        if (state.player.getLevel() < MAX_LEVEL) {
            state.player.advanceLevel();
        }
        prepareStrongerQueue(state);
        state.tournament = createTournament(state.player, state.queue);
        System.out.println("Tournament win recorded.");
    }

    /** Seeds the next queue with stronger and stretch-goal opponents. */
    private static void prepareStrongerQueue(GameState state) {
        state.queue.repopulateAfterWin(state.player, 14);
        state.queue.add(new AdaptiveOpponentFactory().create(state.player,
            "Adaptive Opponent"));
        CustomLinkedList<GameCharacter> champions =
            new BackgroundTournament().simulatePool(state.player, 1);
        if (champions.size() > 0 && champions.get(0) != state.player) {
            state.queue.add(champions.get(0));
        }
    }

    /** Checks the configured five-win or ten-loss run limit. */
    private static boolean runIsComplete(Player player) {
        return player.getWins() >= 5 || player.getLosses() >= 10;
    }

    /** Writes the current player and tournament snapshot. */
    private static void saveProgress(GameState state) {
        try {
            state.saves.save("Player", state.player, state.tournament,
                    Path.of("calculated-combat-player.save"));
            System.out.println("Progress saved.");
        } catch (java.io.IOException exception) {
            System.out.println("Unable to save progress.");
        }
    }

    /** Loads saved player progress and an optional tournament snapshot. */
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

    /** Reads a slot and attempts to sell its equipped item. */
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
        private final ShopProgression progression = new ShopProgression(MAX_LEVEL);
        private final GameSave saves = new GameSave();
        private final TournamentQueue queue = new TournamentQueue();
        private Tournament tournament;
        private boolean running = true;

        private GameState() {
            queue.repopulateAfterLoss(player, 14);
            tournament = createTournament(player, queue);
        }
    }

    /** Parses a top-level choice and reports malformed numeric input. */
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

    /** Runs the equipment-category selection loop. */
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

    /** Prints the five equipment categories and return option. */
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

    /** Runs the numbered item selection loop for one equipment slot. */
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

    /** Prints offers for one category and returns the offer count. */
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
        System.out.println("0. Return to main menu");
        System.out.print("Choose an item: ");
        return choice - 1;
    }

    /** Resolves a category-relative choice to its catalog item. */
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

    /** Converts a slot index into its category label. */
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

    /** Parses a choice and enforces a category or item range. */
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

    /** Creates and fills a fresh eight-fighter tournament. */
    private static Tournament createTournament(Player player) {
        TournamentQueue queue = new TournamentQueue();
        queue.repopulateAfterLoss(player, 14);
        return createTournament(player, queue);
    }

    /** Creates a tournament from the next seven waiting opponents. */
    private static Tournament createTournament(Player player,
            TournamentQueue queue) {
        Tournament tournament = new Tournament(player);
        while (tournament.size() < 8 && queue.size() > 0) {
            tournament.addFighter(queue.removeNext());
        }
        if (tournament.size() < 8) {
            tournament.populateEntrants(player);
        }
        tournament.fillBracket();
        return tournament;
    }
}
