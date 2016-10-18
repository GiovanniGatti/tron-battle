package player;

import player.Player.InputRepository;
import player.Player.LongestSequenceAI;
import player.Player.Spot;
import player.Player.TronLightCycle;
import player.engine.PvPGE;
import player.match.Match;
import player.match.Match.MatchResult;

public final class MatchRunner {

    private MatchRunner() {
        // Main class
    }

    public static void main(String args[]) {
        Match match = new Match(
                playerInput -> () -> new SnailAI(new KnowledgeRepo(playerInput)),
                opponentInput -> () -> new LongestSequenceAI(new InputRepository(opponentInput)),
                () -> new PvPGE(false, new TronLightCycle(0, new Spot(0, 0)), new TronLightCycle(1, new Spot(15, 10))));

        MatchResult call = match.call();

        System.out.println(call);
    }
}
