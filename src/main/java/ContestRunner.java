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
                15);

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
        Player.Spot spot1 = new Player.Spot(6, 2);
        Player.Spot spot2 = new Player.Spot(3, 17);

        return Arrays.asList(
                () -> PvPGE.withFreshBattleField(false, spot1, spot2),
                () -> PvPGE.withFreshBattleField(false, spot2, spot1));
    }
}
