package player.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Plays multiple matches between to AIs. It is useful when IAs or State supplier are not deterministic,
 * otherwise, a single match is enough
 */
public class Game implements Callable<Game.GameResult> {

    private static final int DEFAULT_NUMBER_OF_MATCHES = 5;

    private final Function<IntSupplier, Supplier<AI>> player;
    private final Function<IntSupplier, Supplier<AI>> opponent;
    private final Supplier<GameEngine> gameEngine;
    private final int numberOfMatches;
    private final ExecutorService executorService;

    public Game(
            Function<IntSupplier, Supplier<AI>> player,
            Function<IntSupplier, Supplier<AI>> opponent,
            Supplier<GameEngine> gameEngine,
            ExecutorService executorService) {

        this(player, opponent, gameEngine, executorService, DEFAULT_NUMBER_OF_MATCHES);
    }

    public Game(
            Function<IntSupplier, Supplier<AI>> player,
            Function<IntSupplier, Supplier<AI>> opponent,
            Supplier<GameEngine> gameEngine,
            ExecutorService executorService,
            int numberOfMatches) {

        this.player = player;
        this.opponent = opponent;
        this.gameEngine = gameEngine;
        this.numberOfMatches = numberOfMatches;
        this.executorService = executorService;
    }

    @Override
    public GameResult call() throws InterruptedException, ExecutionException {

        List<Callable<MatchResult>> matches = new ArrayList<>();

        IntStream.range(0, numberOfMatches)
                .forEach(i -> matches.add(new Match(player, opponent, gameEngine)));

        List<Future<MatchResult>> futures = executorService.invokeAll(matches);

        Iterator<Future<MatchResult>> it = futures.iterator();

        if (it.hasNext()) {

            Future<MatchResult> first = it.next();

            MatchResult matchResult = first.get();
            AI player = matchResult.getPlayer();
            AI opponent = matchResult.getOpponent();
            State initialState = matchResult.getGameEngineInitialState();

            GameResult gameResult = new GameResult(player, opponent, initialState);
            gameResult.addMatchResult(matchResult);

            while (it.hasNext()) {

                Future<MatchResult> next = it.next();
                MatchResult nextMatchResult = next.get();
                AI nextPlayer = nextMatchResult.getPlayer();
                AI nextOpponent = nextMatchResult.getOpponent();
                State nextInitialState = nextMatchResult.getGameEngineInitialState();

                Preconditions.checkArgument(
                        Objects.equals(player, nextPlayer),
                        "Illegal usage, players should always be the same, but found first=%s, player=%s",
                        player, nextPlayer);

                Preconditions.checkArgument(
                        Objects.equals(opponent, nextOpponent),
                        "Illegal usage, opponents should always be the same, but found first=%s, opponent=%s",
                        opponent, nextOpponent);

                Preconditions.checkArgument(
                        Objects.equals(initialState, nextInitialState),
                        "Illegal usage, game engines' initial states should always be the same, " +
                                "but found first=%s, initialState=%s",
                        initialState, nextInitialState);

                gameResult.addMatchResult(nextMatchResult);
            }

            return gameResult;
        }

        throw new IllegalStateException("At least one match should be player, numberOfMatches=" + numberOfMatches);
    }

    public static final class GameResult {

        // FIXME: break AI dependency
        private final AI player;
        private final AI opponent;
        private final State initialState;

        private int numberOfMatches;
        private double averagePlayerScore;
        private double averageOpponentScore;
        private double averageNumberOfRounds;
        private int playerWinCount;

        private GameResult(AI player, AI opponent, State initialState) {

            this.player = player;
            this.opponent = opponent;
            this.initialState = initialState;

            this.numberOfMatches = 0;
            this.averagePlayerScore = .0;
            this.averageOpponentScore = .0;
            this.averageNumberOfRounds = .0;
            this.playerWinCount = 0;
        }

        private void addMatchResult(MatchResult result) {

            averagePlayerScore += (result.getPlayerScore() - averagePlayerScore) / (1.0 + numberOfMatches);
            averageOpponentScore += (result.getOpponentScore() - averageOpponentScore) / (1.0 + numberOfMatches);
            averageNumberOfRounds += (result.getRounds() - averageNumberOfRounds) / (1.0 + numberOfMatches);

            if (result.getWinner().equals(Winner.PLAYER)) {
                playerWinCount++;
            }

            numberOfMatches++;
        }

        public double getAveragePlayerScore() {
            return averagePlayerScore;
        }

        public double getAverageOpponentScore() {
            return averageOpponentScore;
        }

        public double getAverageNumberOfRounds() {
            return averageNumberOfRounds;
        }

        public double getPlayerWinRate() {
            Preconditions.checkState(numberOfMatches > 0, "No match has been played");
            return (double) playerWinCount / numberOfMatches;
        }

        public long getNumberOfMatches() {
            return numberOfMatches;
        }

        public Winner getWinner() {
            return playerWinCount > (numberOfMatches - playerWinCount) ? Winner.PLAYER : Winner.OPPONENT;
        }

        public AI getPlayer() {
            return player;
        }

        public AI getOpponent() {
            return opponent;
        }

        public State getInitialState() {
            return initialState;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("winner", getWinner())
                    .add("averagePlayerScore", getAveragePlayerScore())
                    .add("averageOpponentScore", getAverageOpponentScore())
                    .add("averageNumberOfRounds", getAverageNumberOfRounds())
                    .add("playerWinRate", getPlayerWinRate())
                    .add("numberOfMatches", getNumberOfMatches())
                    .add("player", getPlayer())
                    .add("opponent", getOpponent())
                    .add("initialState", getInitialState())
                    .toString();
        }
    }
}
