package player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import player.Player.BattleField;
import player.Player.LongestSequenceAI;
import player.Player.Spot;
import player.engine.PvPGE;
import player.game.Game;

public final class GameRunner {

    private GameRunner() {
        // Main class
    }

    public static void main(String args[]) throws ExecutionException, InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(5);

        Spot playerStartSpot = new Spot(15, 10);
        Spot opponentStartSpot = new Spot(0, 0);

        Game game = new Game(
                playerInput -> () -> new SnailAI(new KnowledgeRepo(playerInput)),
                opponentInput -> () -> new LongestSequenceAI(new Player.InputRepository(opponentInput)),
                () -> PvPGE.withFreshBattleField(false, playerStartSpot, opponentStartSpot),
                pool);

        Game.GameResult call = game.call();

        pool.shutdown();

        System.out.println(call);
    }
}
