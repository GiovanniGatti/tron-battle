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

        Player.Spot playerStartSpot = new Player.Spot(6, 2);
        Player.Spot opponentStartSpot = new Player.Spot(3, 17);

        Game game = new Game(
                playerInput -> () -> new AIMapper(
                        new Player.RelaxedLongestSequenceAI(new Player.InputRepository(playerInput))),
                opponentInput -> () -> new AIMapper(
                        new LongestSequenceAI(new Player.InputRepository(opponentInput))),
                () -> PvPGE.withFreshBattleField(false, playerStartSpot, opponentStartSpot),
                pool,
                20);

        Game.GameResult call = game.call();

        pool.shutdown();

        System.out.println(call);
    }
}
