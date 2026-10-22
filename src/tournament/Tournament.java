package tournament;

import java.util.Random;
import java.util.Base64;

import characters.GameCharacter;
import characters.Player;
import characters.RandomNamePicker;
import linkedlist.CustomLinkedList;

/**
 * Runs one eight-fighter, double-elimination tournament one match at a time.
 */
public class Tournament {
    private static final int MAX_ENTRANTS = 8;
    private static final int MAX_LOSSES = 2;
    private static final RandomNamePicker NAME_GENERATOR = new RandomNamePicker();

    private final CustomLinkedList<GameCharacter> entrants;
    private final CustomLinkedList<GameCharacter> winners;
    private final CustomLinkedList<GameCharacter> losers;
    private final int[] lossCounts;
    private final Random random;
    private final SameRoundLossQueue sameRoundLosses;
    private int phase;
    private int matchIndex;
    private boolean finished;
    private GameCharacter champion;

    /**
     * Creates a tournament with a default random source.
     *
     * @param player player entrant
     * @throws IllegalArgumentException if player is null
     */
    public Tournament(Player player) {
        this(player, new Random());
    }

    /**
     * Creates a tournament with an injectable random source.
     *
     * @param player player entrant
     * @param random source for match outcomes
     * @throws IllegalArgumentException if either argument is null
     */
    public Tournament(Player player, Random random) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random source is required");
        }
        entrants = new CustomLinkedList<>();
        winners = new CustomLinkedList<>();
        losers = new CustomLinkedList<>();
        sameRoundLosses = new SameRoundLossQueue();
        lossCounts = new int[MAX_ENTRANTS];
        this.random = random;
        addFighter(player);
    }

    /**
     * Registers a fighter before bracket play begins.
     *
     * @param fighter fighter to register
     * @return whether registration succeeded
     */
    public boolean addFighter(GameCharacter fighter) {
        if (fighter == null || phase != 0 || entrants.size() >= MAX_ENTRANTS
                || containsIdentity(entrants, fighter)) {
            return false;
        }
        entrants.add(fighter);
        return true;
    }

    /**
     * Starts the bracket after exactly eight fighters are registered.
     *
     * @return whether the bracket was filled
     */
    public boolean fillBracket() {
        if (phase != 0 || entrants.size() != MAX_ENTRANTS) {
            return false;
        }
        copy(winners, entrants);
        phase = 1;
        matchIndex = 0;
        return true;
    }

    /**
     * Simulates the next match and advances bracket state as needed.
     *
     * @return the completed match result
     * @throws IllegalStateException if the bracket is not playable
     */
    public MatchResult playNextMatch() {
        validatePlayable();
        GameCharacter[] fighters = currentFighters();
        GameCharacter fighter1 = fighters[0];
        GameCharacter fighter2 = fighters[1];
        GameCharacter winner = new TournamentRound(fighter1, fighter2, random)
                .getWinner();
        GameCharacter loser = winner == fighter1 ? fighter2 : fighter1;
        recordLoss(loser);
        recordPhaseResult(winner, loser);
        MatchResult result = new MatchResult(fighter1, fighter2, winner, phase);
        matchIndex++;
        if (phaseMatchCount() == matchIndex) {
            advancePhase(winner, loser);
        }
        return result;
    }

    /** Ensures the bracket can accept another match. */
    private void validatePlayable() {
        if (phase == 0) {
            throw new IllegalStateException("Bracket is not filled");
        }
        if (finished) {
            throw new IllegalStateException("Tournament is finished");
        }
    }

    /** Selects the two fighters scheduled for the current phase and match. */
    private GameCharacter[] currentFighters() {
        if (phase == 1 || phase == 3) {
            return pair(winners, matchIndex);
        }
        if (phase == 2 || phase == 4) {
            return pair(losers, matchIndex);
        }
        if (phase == 5) {
            return new GameCharacter[] { winners.get(0), winners.get(1) };
        }
        if (phase == 6 || phase == 7) {
            return new GameCharacter[] { losers.get(0), losers.get(1) };
        }
        if (phase == 8) {
            return new GameCharacter[] { winners.get(0), losers.get(0) };
        }
        throw new IllegalStateException("Invalid tournament phase");
    }

    /** Returns an adjacent pair from a bracket list. */
    private GameCharacter[] pair(CustomLinkedList<GameCharacter> fighters,
            int index) {
        return new GameCharacter[] { fighters.get(index * 2),
                fighters.get(index * 2 + 1) };
    }

    /** Stores a match result in the phase-local transition lists. */
    private void recordPhaseResult(GameCharacter winner, GameCharacter loser) {
        if (phase == 1 || phase == 3) {
            phaseWinners.add(winner);
            phaseLosers.add(loser);
        } else if (phase == 2 || phase == 4) {
            phaseWinners.add(winner);
        }
    }

    /** Dispatches the completed phase to its next bracket transition. */
    private void advancePhase(GameCharacter finalWinner,
            GameCharacter finalLoser) {
        switch (phase) {
            case 1 -> transitionWinnersAndLosers(openingWinners(), openingLosers());
            case 2 -> advanceToWinnersSemifinals();
            case 3 -> transitionWinnersAndLosers(phaseWinners, merge(phaseLosers, losers));
            case 4 -> advanceToWinnersFinal();
            case 5 -> advanceToLosersRound(finalWinner, finalLoser);
            case 6 -> advanceToLosersFinal(finalWinner);
            case 7 -> advanceToGrandFinal(finalWinner);
            default -> finish(finalWinner);
        }
    }

    /** Moves opening losers into the first losers round. */
    private void advanceToWinnersSemifinals() {
        losers.clear();
        addAll(losers, phaseWinners);
        clearPhaseData();
        phase = 3;
        matchIndex = 0;
    }

    /** Moves the semifinal losers into the next losers round. */
    private void advanceToWinnersFinal() {
        losers.clear();
        addAll(losers, phaseWinners);
        clearPhaseData();
        phase = 5;
        matchIndex = 0;
    }

    /** Seeds the first losers final after the winners final. */
    private void advanceToLosersRound(GameCharacter winner, GameCharacter loser) {
        winners.clear();
        winners.add(winner);
        losers.add(loser);
        matchIndex = 0;
        phase = 6;
        clearPhaseData();
    }

    /** Keeps the surviving losers-bracket fighters for the final. */
    private void advanceToLosersFinal(GameCharacter survivor) {
        GameCharacter remainingLoser = losers.get(2);
        losers.clear();
        losers.add(survivor);
        losers.add(remainingLoser);
        matchIndex = 0;
        phase = 7;
    }

    /** Places the losers champion into the single grand final. */
    private void advanceToGrandFinal(GameCharacter finalWinner) {
        GameCharacter winnersChampion = winners.get(0);
        winners.clear();
        winners.add(winnersChampion);
        losers.clear();
        losers.add(finalWinner);
        matchIndex = 0;
        phase = 8;
    }

    /** Records the final winner and closes the tournament. */
    private void finish(GameCharacter finalWinner) {
        champion = finalWinner;
        finished = true;
        matchIndex = 0;
    }

    private CustomLinkedList<GameCharacter> phaseWinners = new CustomLinkedList<>();
    private CustomLinkedList<GameCharacter> phaseLosers = new CustomLinkedList<>();

    /** Returns the winners collected during the opening round. */
    private CustomLinkedList<GameCharacter> openingWinners() {
        return phaseWinners;
    }

    /** Returns the losers collected during the opening round. */
    private CustomLinkedList<GameCharacter> openingLosers() {
        return phaseLosers;
    }

    /** Replaces active bracket lists and advances the phase number. */
    private void transitionWinnersAndLosers(
            CustomLinkedList<GameCharacter> nextWinners,
            CustomLinkedList<GameCharacter> nextLosers) {
        winners.clear();
        losers.clear();
        addAll(winners, nextWinners);
        addAll(losers, nextLosers);
        clearPhaseData();
        phase++;
        matchIndex = 0;
    }

    /** Clears temporary result lists after a phase transition. */
    private void clearPhaseData() {
        phaseWinners.clear();
        phaseLosers.clear();
    }

    /** Combines two fighter lists without duplicate identities. */
    private CustomLinkedList<GameCharacter> merge(
            CustomLinkedList<GameCharacter> first,
            CustomLinkedList<GameCharacter> second) {
        CustomLinkedList<GameCharacter> merged = new CustomLinkedList<>();
        addAll(merged, first);
        addAll(merged, second);
        return merged;
    }

    /** Returns the number of matches required by the current phase. */
    private int phaseMatchCount() {
        return phase == 1 ? 4
                : phase == 2 || phase == 3 || phase == 4
                        ? 2
                        : 1;
    }

    /** Increments the registered loss count for a fighter. */
    private void recordLoss(GameCharacter fighter) {
        int index = findFighterIndex(fighter);
        if (index < 0) {
            throw new IllegalArgumentException("Fighter is not registered");
        }
        lossCounts[index]++;
        sameRoundLosses.addLoss(phase, fighter);
    }

    /** Finds a registered fighter by object identity. */
    private int findFighterIndex(GameCharacter fighter) {
        for (int i = 0; i < entrants.size(); i++) {
            if (entrants.get(i) == fighter) {
                return i;
            }
        }
        return -1;
    }

    /** Checks whether a list already contains the exact fighter object. */
    private boolean containsIdentity(CustomLinkedList<GameCharacter> list,
            GameCharacter fighter) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == fighter) {
                return true;
            }
        }
        return false;
    }

    /** Replaces one bracket list with another list's unique fighters. */
    private void copy(CustomLinkedList<GameCharacter> target,
            CustomLinkedList<GameCharacter> source) {
        target.clear();
        addAll(target, source);
    }

    /** Appends source fighters that are not already present by identity. */
    private void addAll(CustomLinkedList<GameCharacter> target,
            CustomLinkedList<GameCharacter> source) {
        for (int i = 0; i < source.size(); i++) {
            GameCharacter fighter = source.get(i);
            if (!containsIdentity(target, fighter)) {
                target.add(fighter);
            }
        }
    }

    /**
     * Fills remaining entrant slots with generated opponents.
     *
     * @param player player whose level controls generated opponents
     * @throws IllegalArgumentException if player is null
     */
    public void populateEntrants(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        while (entrants.size() < MAX_ENTRANTS) {
            addFighter(generateCharacter(player));
        }
    }

    /**
     * Generates one opponent at the player's level.
     *
     * @param player player whose level is copied
     * @return generated opponent
     * @throws IllegalArgumentException if player is null
     */
    public GameCharacter generateCharacter(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return new GameCharacter(NAME_GENERATOR.pickName(), player.getLevel());
    }

    /** @return the number of registered entrants */
    public int size() {
        return entrants.size();
    }

    /** @return the number of fighters currently in the winners bracket */
    public int winnersSize() {
        return winners.size();
    }

    /** @return the number of fighters currently in the losers bracket */
    public int losersSize() {
        return losers.size();
    }

    /**
     * Returns the number of recorded losses for a fighter.
     *
     * @param fighter registered fighter
     * @return loss count
     * @throws IllegalArgumentException if fighter is not registered
     */
    public int getLossCount(GameCharacter fighter) {
        int index = findFighterIndex(fighter);
        if (index < 0) {
            throw new IllegalArgumentException("Fighter is not registered");
        }
        return lossCounts[index];
    }

    /** @return whether the tournament has a champion */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Returns the champion after completion.
     *
     * @return tournament champion
     * @throws IllegalStateException if the tournament is incomplete
     */
    public GameCharacter getChampion() {
        if (!finished) {
            throw new IllegalStateException("Tournament is not finished");
        }
        return champion;
    }

    /** @return a validated text snapshot of the current bracket state */
    public String saveState() {
        StringBuilder state = new StringBuilder();
        state.append("phase=").append(phase).append('\n');
        state.append("matchIndex=").append(matchIndex).append('\n');
        state.append("finished=").append(finished).append('\n');
        state.append("champion=").append(indexOf(champion)).append('\n');
        state.append("losses=");
        for (int i = 0; i < lossCounts.length; i++) {
            if (i > 0) {
                state.append(',');
            }
            state.append(lossCounts[i]);
        }
        state.append('\n');
        appendFighters(state);
        appendReferences(state, "winners", winners);
        appendReferences(state, "losers", losers);
        appendReferences(state, "phaseWinners", phaseWinners);
        appendReferences(state, "phaseLosers", phaseLosers);
        return state.toString();
    }

    /**
     * Restores a tournament from a snapshot.
     *
     * @param player player entrant represented by the snapshot
     * @param state  serialized tournament state
     * @param random source for future matches
     * @return restored tournament
     * @throws IllegalArgumentException if the snapshot is malformed
     */
    public static Tournament restoreState(Player player, String state,
            Random random) {
        if (player == null || state == null || random == null) {
            throw new IllegalArgumentException("Tournament state is invalid");
        }
        String[] lines = state.split("\\R");
        try {
            return restoreParsedState(lines, player, random);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Tournament state is invalid",
                    exception);
        }
    }

    /** Builds a tournament from already decoded snapshot fields. */
    private static Tournament restoreParsedState(String[] lines, Player player,
            Random random) {
        Tournament tournament = new Tournament(player, random);
        GameCharacter[] fighters = parseFighters(lines, player);
        tournament.addRestoredFighters(fighters);
        tournament.phase = Integer.parseInt(value(lines, "phase"));
        tournament.matchIndex = Integer.parseInt(value(lines, "matchIndex"));
        tournament.finished = Boolean.parseBoolean(value(lines, "finished"));
        tournament.champion = fighterAt(fighters,
                Integer.parseInt(value(lines, "champion")));
        copyLosses(tournament.lossCounts, parseLosses(value(lines, "losses")));
        tournament.restoreReferences(lines, fighters);
        validateState(tournament);
        return tournament;
    }

    /** Serializes all registered fighters into the snapshot. */
    private void appendFighters(StringBuilder state) {
        for (int i = 0; i < entrants.size(); i++) {
            GameCharacter fighter = entrants.get(i);
            state.append("fighter=").append(i).append('|')
                    .append(encode(fighter.getName())).append('|')
                    .append(fighter.getLevel()).append('|')
                    .append(fighter.getAggro()).append('|')
                    .append(fighter.getControl()).append('|')
                    .append(fighter.getMidrange()).append('\n');
        }
    }

    /** Serializes a bracket list as entrant indexes. */
    private void appendReferences(StringBuilder state, String key,
            CustomLinkedList<GameCharacter> fighters) {
        state.append(key).append('=');
        for (int i = 0; i < fighters.size(); i++) {
            if (i > 0) {
                state.append(',');
            }
            state.append(indexOf(fighters.get(i)));
        }
        state.append('\n');
    }

    /** Converts a fighter identity into its entrant index. */
    private int indexOf(GameCharacter fighter) {
        if (fighter == null) {
            return -1;
        }
        return findFighterIndex(fighter);
    }

    /** Registers fighters parsed from a saved snapshot. */
    private void addRestoredFighters(GameCharacter[] fighters) {
        if (fighters.length == 0 || fighters[0] == null) {
            throw new IllegalArgumentException("Missing player entrant");
        }
        for (int i = 1; i < fighters.length; i++) {
            if (!addFighter(fighters[i])) {
                throw new IllegalArgumentException("Duplicate entrant");
            }
        }
    }

    /** Restores all bracket lists from serialized entrant indexes. */
    private void restoreReferences(String[] lines, GameCharacter[] fighters) {
        restoreList(winners, value(lines, "winners"), fighters);
        restoreList(losers, value(lines, "losers"), fighters);
        restoreList(phaseWinners, value(lines, "phaseWinners"), fighters);
        restoreList(phaseLosers, value(lines, "phaseLosers"), fighters);
    }

    /** Restores one bracket list from comma-separated fighter indexes. */
    private void restoreList(CustomLinkedList<GameCharacter> target,
            String indexes, GameCharacter[] fighters) {
        if (indexes.isBlank()) {
            return;
        }
        for (String index : indexes.split(",")) {
            target.add(fighterAt(fighters, Integer.parseInt(index)));
        }
    }

    /** Parses serialized fighter records in entrant order. */
    private static GameCharacter[] parseFighters(String[] lines, Player player) {
        CustomLinkedList<GameCharacter> fighters = new CustomLinkedList<>();
        for (String line : lines) {
            addParsedFighter(fighters, line, player);
        }
        return toArray(fighters);
    }

    /** Adds one parsed fighter record when a line contains one. */
    private static void addParsedFighter(CustomLinkedList<GameCharacter> fighters,
            String line, Player player) {
        if (!line.startsWith("fighter=")) {
            return;
        }
        String[] fields = line.substring(8).split("\\|", -1);
        validateFighterFields(fields, fighters.size());
        fighters.add(createFighter(fields, fighters.isEmpty(), player));
    }

    /** Validates the field count and sequential entrant index. */
    private static void validateFighterFields(String[] fields, int index) {
        if (fields.length != 6 || Integer.parseInt(fields[0]) != index) {
            throw new IllegalArgumentException("Fighter record is invalid");
        }
    }

    /** Creates either the saved player or a restored opponent. */
    private static GameCharacter createFighter(String[] fields, boolean player,
            Player savedPlayer) {
        return player ? savedPlayer
                : new GameCharacter(decode(fields[1]),
                        Integer.parseInt(fields[2]), Double.parseDouble(fields[3]),
                        Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));
    }

    /** Converts parsed fighter storage into an array for indexed references. */
    private static GameCharacter[] toArray(CustomLinkedList<GameCharacter> fighters) {
        GameCharacter[] result = new GameCharacter[fighters.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = fighters.get(i);
        }
        return result;
    }

    /** Parses and validates the eight serialized loss counters. */
    private static int[] parseLosses(String value) {
        String[] values = value.split(",", -1);
        if (values.length != MAX_ENTRANTS) {
            throw new IllegalArgumentException("Loss records are invalid");
        }
        int[] result = new int[MAX_ENTRANTS];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(values[i]);
            if (result[i] < 0 || result[i] > MAX_LOSSES) {
                throw new IllegalArgumentException("Loss records are invalid");
            }
        }
        return result;
    }

    /** Copies validated loss counters into tournament state. */
    private static void copyLosses(int[] target, int[] source) {
        System.arraycopy(source, 0, target, 0, target.length);
    }

    /** Resolves a serialized fighter index, allowing -1 for no fighter. */
    private static GameCharacter fighterAt(GameCharacter[] fighters, int index) {
        if (index < -1 || index >= fighters.length) {
            throw new IllegalArgumentException("Fighter reference is invalid");
        }
        return index == -1 ? null : fighters[index];
    }

    /** Retrieves a required key-value field from snapshot lines. */
    private static String value(String[] lines, String key) {
        String prefix = key + "=";
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return line.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("Missing tournament field");
    }

    /** Validates core consistency of restored tournament state. */
    private static void validateState(Tournament tournament) {
        if (tournament.entrants.size() != MAX_ENTRANTS
                || tournament.phase < 0 || tournament.phase > 8
                || tournament.matchIndex < 0
                || (!tournament.finished && tournament.champion != null)
                || (tournament.finished && tournament.champion == null)) {
            throw new IllegalArgumentException("Tournament state is invalid");
        }
    }

    /** Encodes fighter names for snapshot storage. */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(
                value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /** Decodes fighter names from snapshot storage. */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value),
                java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        StringBuilder bracket = new StringBuilder();
        bracket.append("=== TOURNAMENT BRACKET ===\n");
        bracket.append("Phase: ").append(phaseName()).append('\n');
        bracket.append("Winners Bracket\n");
        bracket.append("  [").append(fighterName(winners, 0)).append("]--+\n");
        bracket.append("                         +-- ")
                .append(fighterName(winners, 1)).append('\n');
        bracket.append("  [").append(fighterName(winners, 2)).append("]--+\n");
        bracket.append("                         +-- ")
                .append(fighterName(winners, 3)).append('\n');
        bracket.append("Losers Bracket\n");
        for (int i = 0; i < losers.size(); i++) {
            bracket.append("  [").append(losers.get(i)).append("]\n");
        }
        bracket.append(finished ? "Champion: " + champion : "Champion: pending");
        return bracket.toString();
    }

    /** Returns a display name or pending marker for a bracket position. */
    private String fighterName(CustomLinkedList<GameCharacter> fighters,
            int index) {
        return index < fighters.size() ? fighters.get(index).getName() : "pending";
    }

    /** Converts the internal phase number into a display label. */
    private String phaseName() {
        if (finished) {
            return "Complete";
        }
        return switch (phase) {
            case 0 -> "Registration";
            case 1 -> "Opening winners round";
            case 2 -> "Opening losers round";
            case 3 -> "Winners semifinals";
            case 4 -> "Losers semifinals";
            case 5 -> "Winners final";
            case 6, 7 -> "Losers final";
            case 8 -> "Grand final";
            default -> "Unknown";
        };
    }
}
