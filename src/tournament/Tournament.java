package tournament;

import characters.GameCharacter;
import characters.Player;
import characters.RandomNamePicker;
import linkedlist.CustomLinkedList;

public class Tournament {
    private static final RandomNamePicker nameGen = new RandomNamePicker();
    // TODO: Add 8-person bracket (should be double-elim until the grand finals)
    private final CustomLinkedList<GameCharacter> entrants;
    private final CustomLinkedList<GameCharacter> winners;
    private final CustomLinkedList<GameCharacter> losers;
    private final CustomLinkedList<GameCharacter> nextWinners;
    private final CustomLinkedList<GameCharacter> nextLosers;

    private int matchIndex;
    private int[] lossCounts;
    private int phase;
    private boolean finished;
    private GameCharacter champion;

    public Tournament(Player player) {
        if (player == null){
            throw new IllegalArgumentException("Player cannot be null");
        }
        matchIndex = 0;
        entrants = new CustomLinkedList<>();
        winners = new CustomLinkedList<>();
        losers = new CustomLinkedList<>();
        nextWinners = new CustomLinkedList<>();
        nextLosers = new CustomLinkedList<>();
        lossCounts = new int[8];
        phase = 0;
        finished = false;
        champion = null;
        addFighter(player);
    }

    public boolean fillBracket() {
        if (phase != 0 || entrants.size() != 8) {
            return false;
        }
        for (int i = 0; i < entrants.size(); i++){
            winners.add(entrants.get(i));
        }
        phase = 1;
        matchIndex = 0;
        return true;
    }
    public MatchResult playNextMatch(){
        GameCharacter fighter1;
    GameCharacter fighter2;

    switch (phase) {
        case 1:
            // Opening round: 8 fighters, 4 matches
            fighter1 = winners.get(matchIndex * 2);
            fighter2 = winners.get(matchIndex * 2 + 1);
            break;

        case 2:
            // Losers bracket round 1: 4 fighters, 2 matches
            fighter1 = losers.get(matchIndex * 2);
            fighter2 = losers.get(matchIndex * 2 + 1);
            break;

        case 3:
            // Winners bracket semifinials: 4 fighters, 2 matches
            fighter1 = winners.get(matchIndex * 2);
            fighter2 = winners.get(matchIndex * 2 + 1);
            break;

        case 4:
            // Additional bracket rounds
            // TODO: Determine fighter selection
            throw new UnsupportedOperationException(
                "Phase 4 not implemented");

        default:
            throw new IllegalStateException(
                "No active tournament matches");
    }
        TournamentRound round = new TournamentRound(fighter1, fighter2);
        GameCharacter winner = round.getWinner();
        GameCharacter loser;
        if (winner == fighter1){
            loser = fighter2;
        }
        else{
            loser = fighter1;
        }
        recordLoss(loser);
        switch (phase) {
            case 1:
                nextWinners.add(winner);
                nextLosers.add(loser);
                break;
            case 2:
                nextLosers.add(winner);
                break;
            case 3:
                nextWinners.add(winner);
                nextLosers.add(loser);
                break;
            default:
                throw new IllegalStateException("Cannot update brackets for phase "+phase);
        }
        MatchResult result = new MatchResult(fighter1, fighter2, winner, phase);
        matchIndex++;
        if ((phase == 1 && matchIndex == 4) || (phase == 2 && matchIndex == 2) ||
         (phase == 3 && matchIndex == 2) || (phase == 4 && matchIndex == 3)){
            advanceRound();
        }
        return result;
    }
    public boolean addFighter(GameCharacter fighter) {
        if (fighter == null || entrants.size() >=8) {
            return false;
        }
        for (int i = 0; i< entrants.size(); i++){
            if (entrants.get(i) == fighter){
                return false;
            }
        }
        entrants.add(fighter);
        return true;
    }
    private void advanceRound() {
        switch (phase) {
            case 1:
                winners.clear();
                losers.clear();

                for (int i = 0; i < nextWinners.size(); i++) {
                    winners.add(nextWinners.get(i));
                }

                for (int i = 0; i < nextLosers.size(); i++) {
                    losers.add(nextLosers.get(i));
                }
                break;

            case 2:
                // Only the losers bracket has competed
                losers.clear();

                for (int i = 0; i < nextLosers.size(); i++) {
                    losers.add(nextLosers.get(i));
                }
                break;

            case 3:
                // Preserve the two surviving losers-bracket fighters
                // and add the two losers from the winners semifinals
                winners.clear();

                for (int i = 0; i < nextWinners.size(); i++) {
                    winners.add(nextWinners.get(i));
                }

                for (int i = 0; i < nextLosers.size(); i++) {
                    losers.add(nextLosers.get(i));
                }
                break;

            default:
                throw new IllegalStateException(
                    "Tournament phase not implemented");
        }

        nextWinners.clear();
        nextLosers.clear();

        phase++;
        matchIndex = 0;
    }
    public void populateEntrants(Player player){
        if (player == null){
            throw new IllegalArgumentException();
        }
        while (entrants.size() < 8){
            entrants.add(generateCharacter(player));
        }
    }
    public GameCharacter generateCharacter(Player player) {
        GameCharacter character = new GameCharacter(nameGen.pickName(), player.getLevel());
        return character;
    }
    public int size() {
        return entrants.size();
    }
    public int winnersSize(){
        return winners.size();
    }
    public int losersSize(){
        return losers.size();
    }
    public int getLossCount(GameCharacter fighter){
        int index = findFighterIndex(fighter);
        if (index == -1){
            throw new IllegalArgumentException("Fighter is not registered");
        }
        return lossCounts[index];
    }
    public boolean isFinished(){
        return finished;
    }
    public GameCharacter getChampion(){
        if(!finished){
            throw new IllegalStateException("Tournament is not finished");
        }
        return champion;
    }
    private int findFighterIndex(GameCharacter fighter){
        for (int i = 0; i < entrants.size(); i++){
            if (entrants.get(i) == fighter){
                return i;
            }
        }
        return -1;
    }
    private void recordLoss(GameCharacter fighter){
        int index = findFighterIndex(fighter);
        if (index == -1){
            throw new IllegalArgumentException("Fighter is not registered");
        }
        lossCounts[index]++;
    }
}
