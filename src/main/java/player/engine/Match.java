package player.engine;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 *
 * Represents a single match between any two IAs
 *
 */
public final class Match implements Callable<MatchResult> {

    private final Function<IntSupplier, Supplier<AI>> playerSupplier;
    private final Function<IntSupplier, Supplier<AI>> opponentSupplier;
    private final Supplier<GameEngine> gameEngineSupplier;

    public Match(
            Function<IntSupplier, Supplier<AI>> player,
            Function<IntSupplier, Supplier<AI>> opponent,
            Supplier<GameEngine> gameEngine) {

        this.playerSupplier = player;
        this.opponentSupplier = opponent;
        this.gameEngineSupplier = gameEngine;
    }

    @Override
    public MatchResult call() {

        GameEngine gameEngine = gameEngineSupplier.get();
        AI player = playerSupplier.apply(gameEngine::playerInput).get();
        AI opponent = opponentSupplier.apply(gameEngine::opponentInput).get();

        do {
            gameEngine.run(player, opponent);
        } while (gameEngine.getWinner() == Winner.ON_GOING);

        return new MatchResult(
                player,
                opponent,
                gameEngine.getInitialState(),
                gameEngine.getPlayerScore(),
                gameEngine.getOpponentScore(),
                gameEngine.getNumberOfRounds(),
                gameEngine.getWinner());
    }
}
