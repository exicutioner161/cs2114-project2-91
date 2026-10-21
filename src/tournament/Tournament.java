package tournament;

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
    private int phase;
    private int matchIndex;
    private boolean finished;
    private GameCharacter champion;

    public Tournament(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        entrants = new CustomLinkedList<>();
        winners = new CustomLinkedList<>();
        losers = new CustomLinkedList<>();
        lossCounts = new int[MAX_ENTRANTS];
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

        GameCharacter winner = new TournamentRound(fighter1, fighter2).getWinner();
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
            losers.add(finalLoser);
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
}
