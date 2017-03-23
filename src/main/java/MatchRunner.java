import player.engine.Match;
import player.engine.MatchResult;

public final class MatchRunner {

    private MatchRunner() {
        // Main class
    }

    public static void main(String args[]) {

        Player.Spot playerStartSpot = new Player.Spot(6, 2);
        Player.Spot opponentStartSpot = new Player.Spot(3, 17);

        Match match = new Match(
                playerInput -> () -> new AIMapper(new SnailAI(new KnowledgeRepo(playerInput))),
                opponentInput -> () -> new AIMapper(new Player.FibonacciLongestSequenceAI(new Player.InputRepository(opponentInput))),
                () -> PvPGE.withFreshBattleField(false, playerStartSpot, opponentStartSpot));

        MatchResult call = match.call();

        System.out.println(call);
    }
}
