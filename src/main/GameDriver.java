package main;

import characters.GameCharacter;
import characters.Player;
import shop.Shop;
import tournament.Tournament;

public class GameDriver {
    public static void main(String[] args) {
        Player player = new Player("Player", 0, 100, new characters.Item[5]);
        Shop shop = new Shop();
        shop.buy(player, "Bastard Sword");

        Tournament tournament = new Tournament();
        tournament.addFighter(new GameCharacter("Opponent A", 0));
        tournament.addFighter(new GameCharacter("Opponent B", 0));

        GameCharacter winner = tournament.runMatch(player);
        System.out.println(player.catalogText());
        System.out.println("Match winner: " + winner.getName());
    }
}
