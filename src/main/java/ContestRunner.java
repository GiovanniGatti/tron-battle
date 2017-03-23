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
        // scenario 1 - one in the center, the other on the top corner
        Player.Spot scenario11 = new Player.Spot(6, 2);
        Player.Spot scenario12 = new Player.Spot(15, 10);

        // scenario 2 - both on opposite corners
        Player.Spot scenario21 = new Player.Spot(6, 2);
        Player.Spot scenario22 = new Player.Spot(23, 17);

        // scenario 3 - both close to center
        Player.Spot scenario31 = new Player.Spot(10, 8);
        Player.Spot scenario32 = new Player.Spot(20, 12);

        // scenario 4 - both close to top corner
        Player.Spot scenario41 = new Player.Spot(6, 2);
        Player.Spot scenario42 = new Player.Spot(2, 6);

        return Arrays.asList(
                () -> PvPGE.withFreshBattleField(false, scenario11, scenario12),
                () -> PvPGE.withFreshBattleField(false, scenario12, scenario11),
                () -> PvPGE.withFreshBattleField(false, scenario22, scenario21),
                () -> PvPGE.withFreshBattleField(false, scenario21, scenario22),
                () -> PvPGE.withFreshBattleField(false, scenario32, scenario31),
                () -> PvPGE.withFreshBattleField(false, scenario31, scenario32),
                () -> PvPGE.withFreshBattleField(false, scenario42, scenario41),
                () -> PvPGE.withFreshBattleField(false, scenario41, scenario42));
    }
}
