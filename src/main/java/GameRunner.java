import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import player.engine.Game;

public final class GameRunner {

    private GameRunner() {
        // Main class
    }

    public static void main(String args[]) throws ExecutionException, InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(5);

        Player.Spot playerStartSpot = new Player.Spot(15, 10);
        Player.Spot opponentStartSpot = new Player.Spot(0, 0);

        Game game = new Game(
                playerInput -> () -> new AIMapper(
                        new SnailAI(new KnowledgeRepo(playerInput))),
                opponentInput -> () -> new AIMapper(
                        new Player.LongestSequenceAI(new Player.InputRepository(opponentInput))),
                () -> PvPGE.withFreshBattleField(false, playerStartSpot, opponentStartSpot),
                pool);

        Game.GameResult call = game.call();

        pool.shutdown();

        System.out.println(call);
    }
}
