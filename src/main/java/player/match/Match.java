package player.match;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import player.Player.AI;
import player.engine.GameEngine;
import player.engine.Winner;

/**
 *
 * Represents a single match between any two IAs
 *
 */
public final class Match implements Callable<MatchResult> {

    private final AI player;
    private final AI opponent;
    private final GameEngine gameEngine;

    public Match(
            Function<IntSupplier, Supplier<AI>> player,
            Function<IntSupplier, Supplier<AI>> opponent,
            Supplier<GameEngine> gameEngine) {

        this.gameEngine = gameEngine.get();
        this.player = player.apply(this.gameEngine::playerInput).get();
        this.opponent = opponent.apply(this.gameEngine::opponentInput).get();
    }

    @Override
    public MatchResult call() {

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
