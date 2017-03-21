package player.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.base.MoreObjects;

import player.engine.Contest.ContestResult;
import player.engine.Contest.Score;

@DisplayName("A contest")
class ContestTest implements WithAssertions {

    private ExecutorService gameExecutorService;
    private ExecutorService matchExecutorService;

    @BeforeEach
    void init() {
        gameExecutorService = Executors.newFixedThreadPool(2);
        matchExecutorService = Executors.newFixedThreadPool(3);
    }

    @Test
    @DisplayName("returns the classification of a battle between multiple ais on multiple game engines")
    void returnsClassificationBetweenMultipleAIsOnMultipleGameEngines() throws Exception {
        Function<IntSupplier, Supplier<AI>> firstAI =
                (t) -> () -> new AnyAI(1);

        Function<IntSupplier, Supplier<AI>> secondAI =
                (t) -> () -> new AnyAI(2);

        Function<IntSupplier, Supplier<AI>> thirdAI =
                (t) -> () -> new AnyAI(3);

        List<Function<IntSupplier, Supplier<AI>>> ais = Arrays.asList(firstAI, secondAI, thirdAI);

        List<Supplier<GameEngine>> gameEngines = Arrays.asList(
                () -> MockedGE.anyWithWinner(Winner.PLAYER),
                () -> MockedGE.anyWithWinner(Winner.OPPONENT),
                () -> MockedGE.anyWithWinner(Winner.PLAYER));

        Contest contest = new Contest(
                ais,
                gameEngines,
                gameExecutorService,
                matchExecutorService);

        ContestResult contestResult = contest.call();

        List<Score> classifications = contestResult.getClassification();

        assertThat(classifications).hasSize(3);

        Score first = classifications.get(0);
        Score second = classifications.get(1);
        Score third = classifications.get(2);

        assertThat(first.getAi()).isEqualTo(new AnyAI(1));
        assertThat(first.getVictoryCount()).isEqualTo(4);

        assertThat(second.getAi()).isEqualTo(new AnyAI(2));
        assertThat(second.getVictoryCount()).isEqualTo(3);

        assertThat(third.getAi()).isEqualTo(new AnyAI(3));
        assertThat(third.getVictoryCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("cannot run with a one single AI")
    void throwISEWhenSingleAIIsProvided() {
        Function<IntSupplier, Supplier<AI>> singleAI = (t) -> () -> new AnyAI(1);

        List<Function<IntSupplier, Supplier<AI>>> ais = Collections.singletonList(singleAI);

        List<Supplier<GameEngine>> gameEngine = Collections.singletonList(() -> MockedGE.anyWithWinner(Winner.PLAYER));

        Contest contest = new Contest(
                ais,
                gameEngine,
                gameExecutorService,
                matchExecutorService);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(contest::call)
                .withMessageContaining("Unable to play a contest with a single AI");
    }

    @Nested
    @DisplayName("that finished, returns a result with")
    class Statistics {

        @Test
        @DisplayName("the right average score")
        void averageScore() throws ExecutionException, InterruptedException {
            List<Function<IntSupplier, Supplier<AI>>> ais = Arrays.asList(
                    (t) -> () -> new AnyAI(1),
                    (t) -> () -> new AnyAI(2));

            List<Supplier<GameEngine>> gameEngines = Arrays.asList(
                    () -> MockedGE.anyWithPlayerScore(10),
                    () -> MockedGE.anyWithPlayerScore(20));

            Contest contest = new Contest(
                    ais,
                    gameEngines,
                    gameExecutorService,
                    matchExecutorService);

            ContestResult result = contest.call();

            Optional<Score> maybeScore =
                    result.getClassification()
                            .stream()
                            .filter(t -> t.getAi().equals(new AnyAI(1)))
                            .findFirst();

            assertThat(maybeScore).isPresent();

            Score score = maybeScore.get();

            assertThat(score.getAverageScore()).isEqualTo(15.0);
        }

        @Test
        @DisplayName("the right average number of rounds")
        void averageNumberOfRounds() throws ExecutionException, InterruptedException {
            List<Function<IntSupplier, Supplier<AI>>> ais = Arrays.asList(
                    (t) -> () -> new AnyAI(1),
                    (t) -> () -> new AnyAI(2));

            List<Supplier<GameEngine>> gameEngines = Arrays.asList(
                    () -> MockedGE.anyWithNumberOfRounds(10),
                    () -> MockedGE.anyWithNumberOfRounds(20));

            Contest contest = new Contest(
                    ais,
                    gameEngines,
                    gameExecutorService,
                    matchExecutorService);

            ContestResult result = contest.call();

            Optional<Score> maybeScore =
                    result.getClassification()
                            .stream()
                            .filter(t -> t.getAi().equals(new AnyAI(1)))
                            .findFirst();

            assertThat(maybeScore).isPresent();

            Score score = maybeScore.get();

            assertThat(score.getAverageNumberOfRounds()).isEqualTo(15.0);
        }

        @Test
        @DisplayName("the right average win rate")
        void averageWinRate() throws ExecutionException, InterruptedException {
            List<Function<IntSupplier, Supplier<AI>>> ais = Arrays.asList(
                    (t) -> () -> new AnyAI(1),
                    (t) -> () -> new AnyAI(2));

            List<Supplier<GameEngine>> gameEngines = Arrays.asList(
                    () -> MockedGE.anyWithWinner(Winner.PLAYER),
                    () -> MockedGE.anyWithWinner(Winner.PLAYER),
                    () -> MockedGE.anyWithWinner(Winner.OPPONENT));

            Contest contest = new Contest(
                    ais,
                    gameEngines,
                    gameExecutorService,
                    matchExecutorService);

            ContestResult result = contest.call();

            Optional<Score> maybeScore =
                    result.getClassification()
                            .stream()
                            .filter(t -> t.getAi().equals(new AnyAI(1)))
                            .findFirst();

            assertThat(maybeScore).isPresent();

            Score score = maybeScore.get();

            assertThat(score.getAverageWinRate()).isBetween(2.0 / 3 - 0.001, 2.0 / 3 + 0.001);
        }
    }

    private static class AnyAI implements AI {

        private final int id;

        public AnyAI(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AnyAI anyAI = (AnyAI) o;
            return id == anyAI.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .toString();
        }
    }
}