package tournament;

import java.util.Random;

import characters.GameCharacter;

public class TournamentRound {
    private static final double ONE_HUNDRED = 100.0;
    private static final double BASE_WIN_PERCENT = 50.0;
    private static final double HIGH_ADVANTAGE_INCREMENT = 7.0;
    private static final double MED_ADVANTAGE_INCREMENT = 3.5;
    private static final double BASE_ADVANTAGE_INCREMENT = 2.0;
    private Random random;
    private double fighterOneWinPercent;
    private GameCharacter fighter1;
    private GameCharacter fighter2;
    private GameCharacter winner;

    public TournamentRound(GameCharacter fighter1, GameCharacter fighter2) {
        if (fighter1 == null || fighter2 == null || fighter1 == fighter2){
            throw new IllegalArgumentException("Invalid fighters");
        }
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.fighterOneWinPercent = BASE_WIN_PERCENT;
        this.random = new Random();
    }

    public GameCharacter getWinner() {
        if (winner != null){
            return winner;
        }
        double fOneAggVsCont = calculateFighterAdvantage(fighter1.getAggro(), fighter2.getControl());
        double fOneContVsMidr = calculateFighterAdvantage(fighter1.getControl(), fighter2.getMidrange());
        double fOneMidrVsAgg = calculateFighterAdvantage(fighter1.getMidrange(), fighter2.getAggro());
        double fTwoAggVsCont = calculateFighterAdvantage(fighter2.getAggro(), fighter1.getControl());
        double fTwoContVsMidr = calculateFighterAdvantage(fighter2.getControl(), fighter1.getMidrange());
        double fTwoMidrVsAgg = calculateFighterAdvantage(fighter2.getMidrange(), fighter1.getAggro());
        double totalFighterOneAdv = fOneAggVsCont + fOneContVsMidr + fOneMidrVsAgg - fTwoAggVsCont - fTwoContVsMidr
                - fTwoMidrVsAgg;
        fighterOneWinPercent += totalFighterOneAdv;
        fighterOneWinPercent = Math.max(0,Math.min(100, fighterOneWinPercent));
        if (fighterOneWinPercent < 0) {
            fighterOneWinPercent = 0;
        }
        if (random.nextDouble() * ONE_HUNDRED < fighterOneWinPercent) {
            winner = fighter1;
        } else {
            winner = fighter2;
        }
        return winner;
    }

    private double calculateFighterAdvantage(double fighterOneStat, double fighterTwoStat) {
        double fighterOneAdv = 0;
        if (fighterOneStat > fighterTwoStat) {
            return getHighAdv(fighterOneStat, fighterTwoStat);
        } else if (fighterOneStat == fighterTwoStat) {
            return getMedAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, 0);
        } else {
            return getBaseAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, 0);
        }
    }

    private double getHighAdv(double fighterOneStat, double fighterTwoStat) {
        double fighterOneAdv = 0;
        double statDiff = fighterOneStat - fighterTwoStat;
        fighterOneAdv += statDiff*HIGH_ADVANTAGE_INCREMENT;
        
        return getMedAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, statDiff);
    }

    private double getMedAdv(double fighterOneStat, double fighterTwoStat, double fighterOneAdv, double statDiff) {
        fighterOneAdv += MED_ADVANTAGE_INCREMENT;
        return getBaseAdv(fighterOneStat, fighterTwoStat, fighterOneAdv, statDiff);
    }

    private double getBaseAdv(double fighterOneStat, double fighterTwoStat, double fighterOneAdv, double statDiff) {
        for (int i = 0; i < fighterOneStat - fighterTwoStat; i++) {
            fighterOneAdv += BASE_ADVANTAGE_INCREMENT;
        }
        return fighterOneAdv;
    }
}