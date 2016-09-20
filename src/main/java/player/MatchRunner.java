package player;

import player.engine.PvPGE;
import player.match.Match;
import player.match.Match.MatchResult;

public final class MatchRunner {

    private MatchRunner() {
        // Main class
    }

    public static void main(String args[]) {
        Match match = new Match(
                opponentInput -> () -> new RandomAI(new Player.KnowledgeRepo(opponentInput)),
                playerInput -> () -> new Player.SnailAI(new Player.KnowledgeRepo(playerInput)),
                () -> new PvPGE());

        MatchResult call = match.call();

        System.out.println(call);
    }
}
