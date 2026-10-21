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
    private int phase;
    private int matchIndex;
    private boolean finished;
    private GameCharacter champion;

    public Tournament(Player player) {
        this(player, new Random());
    }

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
        lossCounts = new int[MAX_ENTRANTS];
        this.random = random;
        addFighter(player);
    }

    public boolean addFighter(GameCharacter fighter) {
        if (fighter == null || phase != 0 || entrants.size() >= MAX_ENTRANTS
                || containsIdentity(entrants, fighter)) {
            return false;
        }
        entrants.add(fighter);
        return true;
    }

    public boolean fillBracket() {
        if (phase != 0 || entrants.size() != MAX_ENTRANTS) {
            return false;
        }
        copy(winners, entrants);
        phase = 1;
        matchIndex = 0;
        return true;
    }

    public MatchResult playNextMatch() {
        if (phase == 0) {
            throw new IllegalStateException("Bracket is not filled");
        }
        if (finished) {
            throw new IllegalStateException("Tournament is finished");
        }
        GameCharacter fighter1;
        GameCharacter fighter2;
        if (phase == 1 || phase == 3) {
            fighter1 = winners.get(matchIndex * 2);
            fighter2 = winners.get(matchIndex * 2 + 1);
        } else if (phase == 2 || phase == 4) {
            fighter1 = losers.get(matchIndex * 2);
            fighter2 = losers.get(matchIndex * 2 + 1);
        } else if (phase == 5) {
            fighter1 = winners.get(0);
            fighter2 = winners.get(1);
        } else if (phase == 6) {
            fighter1 = losers.get(0);
            fighter2 = losers.get(1);
        } else if (phase == 7) {
            fighter1 = losers.get(0);
            fighter2 = losers.get(1);
        } else if (phase == 8) {
            fighter1 = winners.get(0);
            fighter2 = losers.get(0);
        } else {
            throw new IllegalStateException("Invalid tournament phase");
        }

        GameCharacter winner = new TournamentRound(fighter1, fighter2, random)
            .getWinner();
        GameCharacter loser = winner == fighter1 ? fighter2 : fighter1;
        recordLoss(loser);
        if (phase == 1 || phase == 3) {
            phaseWinners.add(winner);
            phaseLosers.add(loser);
        } else if (phase == 2 || phase == 4) {
            phaseWinners.add(winner);
        }
        MatchResult result = new MatchResult(fighter1, fighter2, winner, phase);
        matchIndex++;
        if (phaseMatchCount() == matchIndex) {
            advancePhase(winner, loser);
        }
        return result;
    }

    private void advancePhase(GameCharacter finalWinner,
            GameCharacter finalLoser) {
        if (phase == 1) {
            transitionWinnersAndLosers(openingWinners(), openingLosers());
        } else if (phase == 2) {
            losers.clear();
            addAll(losers, phaseWinners);
            clearPhaseData();
            phase = 3;
            matchIndex = 0;
        } else if (phase == 3) {
            CustomLinkedList<GameCharacter> nextLosers =
                merge(phaseLosers, losers);
            transitionWinnersAndLosers(phaseWinners, nextLosers);
        } else if (phase == 4) {
            losers.clear();
            addAll(losers, phaseWinners);
            clearPhaseData();
            phase = 5;
            matchIndex = 0;
        } else if (phase == 5) {
            winners.clear();
            winners.add(finalWinner);
            losers.add(finalLoser);
            matchIndex = 0;
            phase = 6;
            clearPhaseData();
        } else if (phase == 6) {
            GameCharacter survivor = finalWinner;
            GameCharacter remainingLoser = losers.get(2);
            losers.clear();
            losers.add(survivor);
            losers.add(remainingLoser);
            matchIndex = 0;
            phase = 7;
        } else if (phase == 7) {
            GameCharacter winnersChampion = winners.get(0);
            winners.clear();
            winners.add(winnersChampion);
            losers.clear();
            losers.add(finalWinner);
            matchIndex = 0;
            phase = 8;
        } else {
            champion = finalWinner;
            finished = true;
            matchIndex = 0;
        }
    }

    private CustomLinkedList<GameCharacter> phaseWinners =
        new CustomLinkedList<>();
    private CustomLinkedList<GameCharacter> phaseLosers =
        new CustomLinkedList<>();

    private CustomLinkedList<GameCharacter> openingWinners() {
        return phaseWinners;
    }

    private CustomLinkedList<GameCharacter> openingLosers() {
        return phaseLosers;
    }

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

    private void clearPhaseData() {
        phaseWinners.clear();
        phaseLosers.clear();
    }

    private CustomLinkedList<GameCharacter> merge(
            CustomLinkedList<GameCharacter> first,
            CustomLinkedList<GameCharacter> second) {
        CustomLinkedList<GameCharacter> merged = new CustomLinkedList<>();
        addAll(merged, first);
        addAll(merged, second);
        return merged;
    }

    private int phaseMatchCount() {
        return phase == 1 ? 4 : phase == 2 || phase == 3 || phase == 4
                ? 2 : 1;
    }

    private void recordLoss(GameCharacter fighter) {
        int index = findFighterIndex(fighter);
        if (index < 0) {
            throw new IllegalArgumentException("Fighter is not registered");
        }
        lossCounts[index]++;
    }

    private int findFighterIndex(GameCharacter fighter) {
        for (int i = 0; i < entrants.size(); i++) {
            if (entrants.get(i) == fighter) {
                return i;
            }
        }
        return -1;
    }

    private boolean containsIdentity(CustomLinkedList<GameCharacter> list,
            GameCharacter fighter) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == fighter) {
                return true;
            }
        }
        return false;
    }

    private void copy(CustomLinkedList<GameCharacter> target,
            CustomLinkedList<GameCharacter> source) {
        target.clear();
        addAll(target, source);
    }

    private void addAll(CustomLinkedList<GameCharacter> target,
            CustomLinkedList<GameCharacter> source) {
        for (int i = 0; i < source.size(); i++) {
            GameCharacter fighter = source.get(i);
            if (!containsIdentity(target, fighter)) {
                target.add(fighter);
            }
        }
    }

    public void populateEntrants(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        while (entrants.size() < MAX_ENTRANTS) {
            addFighter(generateCharacter(player));
        }
    }

    public GameCharacter generateCharacter(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return new GameCharacter(NAME_GENERATOR.pickName(), player.getLevel());
    }

    public int size() {
        return entrants.size();
    }

    public int winnersSize() {
        return winners.size();
    }

    public int losersSize() {
        return losers.size();
    }

    public int getLossCount(GameCharacter fighter) {
        int index = findFighterIndex(fighter);
        if (index < 0) {
            throw new IllegalArgumentException("Fighter is not registered");
        }
        return lossCounts[index];
    }

    public boolean isFinished() {
        return finished;
    }

    public GameCharacter getChampion() {
        if (!finished) {
            throw new IllegalStateException("Tournament is not finished");
        }
        return champion;
    }

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

    public static Tournament restoreState(Player player, String state,
            Random random) {
        if (player == null || state == null || random == null) {
            throw new IllegalArgumentException("Tournament state is invalid");
        }
        String[] lines = state.split("\\R");
        try {
            int phase = Integer.parseInt(value(lines, "phase"));
            int matchIndex = Integer.parseInt(value(lines, "matchIndex"));
            boolean finished = Boolean.parseBoolean(value(lines, "finished"));
            int championIndex = Integer.parseInt(value(lines, "champion"));
            int[] losses = parseLosses(value(lines, "losses"));
            GameCharacter[] fighters = parseFighters(lines, player);
            Tournament tournament = new Tournament(player, random);
            tournament.addRestoredFighters(fighters);
            tournament.phase = phase;
            tournament.matchIndex = matchIndex;
            tournament.finished = finished;
            tournament.champion = fighterAt(fighters, championIndex);
            copyLosses(tournament.lossCounts, losses);
            tournament.restoreReferences(lines, fighters);
            validateState(tournament);
            return tournament;
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Tournament state is invalid",
                exception);
        }
    }

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

    private int indexOf(GameCharacter fighter) {
        if (fighter == null) {
            return -1;
        }
        return findFighterIndex(fighter);
    }

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

    private void restoreReferences(String[] lines, GameCharacter[] fighters) {
        restoreList(winners, value(lines, "winners"), fighters);
        restoreList(losers, value(lines, "losers"), fighters);
        restoreList(phaseWinners, value(lines, "phaseWinners"), fighters);
        restoreList(phaseLosers, value(lines, "phaseLosers"), fighters);
    }

    private void restoreList(CustomLinkedList<GameCharacter> target,
            String indexes, GameCharacter[] fighters) {
        if (indexes.isBlank()) {
            return;
        }
        for (String index : indexes.split(",")) {
            target.add(fighterAt(fighters, Integer.parseInt(index)));
        }
    }

    private static GameCharacter[] parseFighters(String[] lines, Player player) {
        CustomLinkedList<GameCharacter> fighters = new CustomLinkedList<>();
        for (String line : lines) {
            if (!line.startsWith("fighter=")) {
                continue;
            }
            String[] fields = line.substring(8).split("\\|", -1);
            if (fields.length != 6 || Integer.parseInt(fields[0]) != fighters.size()) {
                throw new IllegalArgumentException("Fighter record is invalid");
            }
            GameCharacter fighter = fighters.isEmpty() ? player
                : new GameCharacter(decode(fields[1]), Integer.parseInt(fields[2]),
                    Double.parseDouble(fields[3]), Double.parseDouble(fields[4]),
                    Double.parseDouble(fields[5]));
            fighters.add(fighter);
        }
        GameCharacter[] result = new GameCharacter[fighters.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = fighters.get(i);
        }
        return result;
    }

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

    private static void copyLosses(int[] target, int[] source) {
        System.arraycopy(source, 0, target, 0, target.length);
    }

    private static GameCharacter fighterAt(GameCharacter[] fighters, int index) {
        if (index < -1 || index >= fighters.length) {
            throw new IllegalArgumentException("Fighter reference is invalid");
        }
        return index == -1 ? null : fighters[index];
    }

    private static String value(String[] lines, String key) {
        String prefix = key + "=";
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return line.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("Missing tournament field");
    }

    private static void validateState(Tournament tournament) {
        if (tournament.entrants.size() != MAX_ENTRANTS
                || tournament.phase < 0 || tournament.phase > 8
                || tournament.matchIndex < 0
                || (!tournament.finished && tournament.champion != null)
                || (tournament.finished && tournament.champion == null)) {
            throw new IllegalArgumentException("Tournament state is invalid");
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(
            value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

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

    private String fighterName(CustomLinkedList<GameCharacter> fighters,
            int index) {
        return index < fighters.size() ? fighters.get(index).getName() : "pending";
    }

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
