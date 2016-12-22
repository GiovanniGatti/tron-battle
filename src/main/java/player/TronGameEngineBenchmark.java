package player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import player.Player.ActionsType;
import player.Player.Spot;
import player.Player.StrictEngine;
import player.Player.TronLightCycle;

public class TronGameEngineBenchmark {

    @State(Scope.Thread)
    public static class TronLightCycleState {

        private final List<TronLightCycle> singleLightCycle;
        private final List<TronLightCycle> singleLightCycleWith10Spots;
        private final TronGameEngine simpleGameWith10SpotsHistory;
        private final TronGameEngine simpleGameWith50SpotsHistory;

        private final List<TronLightCycle> doubleLightCycleWith50Spots;
        private final TronGameEngine longSimulationWithSimpleStartUp;

        public TronLightCycleState() {
            this.singleLightCycle = Collections.singletonList(new TronLightCycle(0, new Spot(0, 0)));

            TronLightCycle lightCycleWith10Spots = new TronLightCycle(0, new Spot(0, 0));
            lightCycleWith10Spots.moveTo(new Spot(1, 0));
            lightCycleWith10Spots.moveTo(new Spot(1, 1));
            lightCycleWith10Spots.moveTo(new Spot(1, 2));
            lightCycleWith10Spots.moveTo(new Spot(1, 3));
            lightCycleWith10Spots.moveTo(new Spot(1, 4));
            lightCycleWith10Spots.moveTo(new Spot(1, 5));
            lightCycleWith10Spots.moveTo(new Spot(2, 5));
            lightCycleWith10Spots.moveTo(new Spot(3, 5));
            lightCycleWith10Spots.moveTo(new Spot(4, 5));

            this.singleLightCycleWith10Spots = Collections.singletonList(lightCycleWith10Spots);

            TronLightCycle anotherLightCycleWith10Spots = new TronLightCycle(1, new Spot(10, 10));
            anotherLightCycleWith10Spots.moveTo(new Spot(11, 10));
            anotherLightCycleWith10Spots.moveTo(new Spot(11, 11));
            anotherLightCycleWith10Spots.moveTo(new Spot(11, 12));
            anotherLightCycleWith10Spots.moveTo(new Spot(11, 13));
            anotherLightCycleWith10Spots.moveTo(new Spot(11, 14));
            anotherLightCycleWith10Spots.moveTo(new Spot(11, 15));
            anotherLightCycleWith10Spots.moveTo(new Spot(12, 15));
            anotherLightCycleWith10Spots.moveTo(new Spot(13, 15));
            anotherLightCycleWith10Spots.moveTo(new Spot(14, 15));

            this.simpleGameWith10SpotsHistory =
                    new TronGameEngine(Arrays.asList(lightCycleWith10Spots, anotherLightCycleWith10Spots));

            TronLightCycle lightCycleWith50Spots = new TronLightCycle(0, new Spot(2, 3));
            lightCycleWith50Spots.moveTo(new Spot(10, 9));
            lightCycleWith50Spots.moveTo(new Spot(6, 5));
            lightCycleWith50Spots.moveTo(new Spot(10, 10));
            lightCycleWith50Spots.moveTo(new Spot(6, 6));
            lightCycleWith50Spots.moveTo(new Spot(2, 3));
            lightCycleWith50Spots.moveTo(new Spot(2, 7));
            lightCycleWith50Spots.moveTo(new Spot(2, 8));
            lightCycleWith50Spots.moveTo(new Spot(6, 13));
            lightCycleWith50Spots.moveTo(new Spot(2, 12));
            lightCycleWith50Spots.moveTo(new Spot(11, 6));
            lightCycleWith50Spots.moveTo(new Spot(11, 7));
            lightCycleWith50Spots.moveTo(new Spot(7, 5));
            lightCycleWith50Spots.moveTo(new Spot(7, 6));
            lightCycleWith50Spots.moveTo(new Spot(3, 3));
            lightCycleWith50Spots.moveTo(new Spot(3, 4));
            lightCycleWith50Spots.moveTo(new Spot(3, 5));
            lightCycleWith50Spots.moveTo(new Spot(3, 6));
            lightCycleWith50Spots.moveTo(new Spot(7, 10));
            lightCycleWith50Spots.moveTo(new Spot(3, 7));
            lightCycleWith50Spots.moveTo(new Spot(7, 11));
            lightCycleWith50Spots.moveTo(new Spot(3, 8));
            lightCycleWith50Spots.moveTo(new Spot(7, 12));
            lightCycleWith50Spots.moveTo(new Spot(7, 13));
            lightCycleWith50Spots.moveTo(new Spot(3, 12));
            lightCycleWith50Spots.moveTo(new Spot(3, 13));
            lightCycleWith50Spots.moveTo(new Spot(8, 4));
            lightCycleWith50Spots.moveTo(new Spot(8, 5));
            lightCycleWith50Spots.moveTo(new Spot(8, 10));
            lightCycleWith50Spots.moveTo(new Spot(4, 13));
            lightCycleWith50Spots.moveTo(new Spot(0, 11));
            lightCycleWith50Spots.moveTo(new Spot(0, 12));
            lightCycleWith50Spots.moveTo(new Spot(9, 4));
            lightCycleWith50Spots.moveTo(new Spot(9, 5));
            lightCycleWith50Spots.moveTo(new Spot(9, 7));
            lightCycleWith50Spots.moveTo(new Spot(9, 8));
            lightCycleWith50Spots.moveTo(new Spot(9, 9));
            lightCycleWith50Spots.moveTo(new Spot(9, 10));
            lightCycleWith50Spots.moveTo(new Spot(1, 7));
            lightCycleWith50Spots.moveTo(new Spot(1, 8));
            lightCycleWith50Spots.moveTo(new Spot(1, 9));
            lightCycleWith50Spots.moveTo(new Spot(5, 13));
            lightCycleWith50Spots.moveTo(new Spot(1, 10));
            lightCycleWith50Spots.moveTo(new Spot(1, 11));
            lightCycleWith50Spots.moveTo(new Spot(1, 12));
            lightCycleWith50Spots.moveTo(new Spot(10, 5));
            lightCycleWith50Spots.moveTo(new Spot(10, 6));
            lightCycleWith50Spots.moveTo(new Spot(6, 2));
            lightCycleWith50Spots.moveTo(new Spot(10, 7));
            lightCycleWith50Spots.moveTo(new Spot(6, 3));
            lightCycleWith50Spots.moveTo(new Spot(6, 4));

            TronLightCycle anotherLightCycleWith50Spots = new TronLightCycle(1, new Spot(15, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 12));
            anotherLightCycleWith50Spots.moveTo(new Spot(15, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 12));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(16, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 12));
            anotherLightCycleWith50Spots.moveTo(new Spot(12, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 12));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(17, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 12));
            anotherLightCycleWith50Spots.moveTo(new Spot(13, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 6));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 7));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 8));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 12));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 9));
            anotherLightCycleWith50Spots.moveTo(new Spot(18, 13));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 10));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 11));
            anotherLightCycleWith50Spots.moveTo(new Spot(14, 12));

            this.doubleLightCycleWith50Spots = Arrays.asList(lightCycleWith50Spots, anotherLightCycleWith50Spots);

            this.simpleGameWith50SpotsHistory =
                    new TronGameEngine(Arrays.asList(lightCycleWith50Spots, anotherLightCycleWith50Spots));

            this.longSimulationWithSimpleStartUp =
                    new TronGameEngine(Arrays.asList(
                            new TronLightCycle(0, new Spot(2, 3)),
                            new TronLightCycle(1, new Spot(15, 10))));
        }

        public List<TronLightCycle> getSingleLightCycle() {
            return singleLightCycle;
        }

        public List<TronLightCycle> getSingleLightCycleWith10Spots() {
            return singleLightCycleWith10Spots;
        }

        public TronGameEngine getSimpleGameWith10SpotsHistory() {
            return simpleGameWith10SpotsHistory;
        }

        public TronGameEngine getSimpleGameWith50SpotsHistory() {
            return simpleGameWith50SpotsHistory;
        }

        public TronGameEngine getLongSimulationWithSimpleStartUp() {
            return longSimulationWithSimpleStartUp;
        }

        public List<TronLightCycle> getDoubleLightCycleWith50Spots() {
            return doubleLightCycleWith50Spots;
        }
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime, Mode.SingleShotTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void constructor(TronLightCycleState state) {
        new TronGameEngine(state.getSingleLightCycle());
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime, Mode.SingleShotTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void constructor_multiple_spots(TronLightCycleState state) {
        new TronGameEngine(state.getSingleLightCycleWith10Spots());
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime, Mode.SingleShotTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void constructor_50_spots(TronLightCycleState state) {
        new TronGameEngine(state.getDoubleLightCycleWith50Spots());
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void short_simulation(TronLightCycleState state) {

        TronGameEngine gameEngine = state.getSimpleGameWith10SpotsHistory();

        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 1, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void long_simulation(TronLightCycleState state) {
        TronGameEngine gameEngine = state.getSimpleGameWith10SpotsHistory();

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.UP);

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.UP);

        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 1, ActionsType.UP);

        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 1, ActionsType.UP);

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.RIGHT);

        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 1, ActionsType.RIGHT);

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.RIGHT);

        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 1, ActionsType.DOWN);

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.LEFT);

        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 1, ActionsType.DOWN);

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.LEFT);

        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 1, ActionsType.UP);
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void ge_simulation(TronLightCycleState state) {
        TronGameEngine gameEngine = state.getSimpleGameWith50SpotsHistory();

        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.UP);
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void ge_simulation_with_simple_startUp(TronLightCycleState state) {
        TronGameEngine gameEngine = state.getLongSimulationWithSimpleStartUp();

        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.UP);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.DOWN);
        gameEngine.perform(false, 0, ActionsType.RIGHT);
        gameEngine.perform(false, 0, ActionsType.LEFT);
        gameEngine.perform(false, 0, ActionsType.UP);
    }

    @Benchmark
    @BenchmarkMode({ Mode.AverageTime, Mode.SingleShotTime })
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void cloning() {
        // FIXME: TODO
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TronGameEngineBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(10)
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
