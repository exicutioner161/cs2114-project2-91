package tournament;

import characters.GameCharacter;

public class TournamentRound {
    private GameCharacter fighter1;
    private GameCharacter fighter2;

    public TournamentRound(GameCharacter fighter1, GameCharacter fighter2) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
    }
}
