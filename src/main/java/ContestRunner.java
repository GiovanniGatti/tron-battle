import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import player.engine.AI;
import player.engine.Contest;
import player.engine.GameEngine;

public final class ContestRunner {

    private ContestRunner() {
        // Main class
    }

    public static void main(String args[]) throws ExecutionException, InterruptedException {

        ExecutorService gamePool = Executors.newFixedThreadPool(2);
        ExecutorService matchPool = Executors.newFixedThreadPool(4);

        Contest contest = new Contest(
                generateAis(),
                generateGameEngines(),
                gamePool,
                matchPool,
                10);

        Contest.ContestResult call = contest.call();

        gamePool.shutdown();
        matchPool.shutdown();

        System.out.println(call);
    }

    private static List<Function<IntSupplier, Supplier<AI>>> generateAis() {
        return Arrays.asList(
                playerInput -> () -> new AIMapper(
                        new Player.RelaxedLongestSequenceAI(new Player.InputRepository(playerInput))),
                playerInput -> () -> new AIMapper(
                        new LongestSequenceAI(new Player.InputRepository(playerInput))),
                playerInput -> () -> new AIMapper(
                        new SnailAI(new KnowledgeRepo(playerInput))),
                playerInput -> () -> new AIMapper(
                        new RandomAI(new KnowledgeRepo(playerInput))));
    }

    private static List<Supplier<GameEngine>> generateGameEngines() {
        Player.Spot playerStartSpot1 = new Player.Spot(6, 2);
        Player.Spot opponentStartSpot1 = new Player.Spot(3, 17);

        Player.Spot playerStartSpot2 = new Player.Spot(10, 1);
        Player.Spot opponentStartSpot2 = new Player.Spot(28, 0);

        Player.Spot playerStartSpot3 = new Player.Spot(26, 5);
        Player.Spot opponentStartSpot3 = new Player.Spot(17, 12);

        return Arrays.asList(
                () -> PvPGE.withFreshBattleField(false, playerStartSpot1, opponentStartSpot1),
                () -> PvPGE.withFreshBattleField(false, playerStartSpot2, opponentStartSpot2),
                () -> PvPGE.withFreshBattleField(false, playerStartSpot3, opponentStartSpot3));
    }
}
