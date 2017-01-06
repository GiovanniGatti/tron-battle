package player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GameRunner {

    private GameRunner() {
        // Main class
    }

    public static void main(String args[]) throws ExecutionException, InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(5);
        //
        // Game game = new Game(
        // playerInput -> () -> new SnailAI(new KnowledgeRepo(playerInput)),
        // opponentInput -> () -> new LongestSequenceAI(new Player.InputRepository(opponentInput)),
        // () -> new PvPGE(false, new TronLightCycle(0, new Spot(15, 10)), new TronLightCycle(1, new Spot(0, 0))),
        // pool);
        //
        // GameResult call = game.call();
        //
        // pool.shutdown();
        //
        // System.out.println(call);
    }
}
