package player.match;

import com.google.common.base.MoreObjects;

import player.Player.AI;
import player.engine.State;
import player.engine.Winner;

public final class MatchResult {

    private final AI player;
    private final AI opponent;
    private final State gameEngineInitialState;
    private final int playerScore;
    private final int opponentScore;
    private final int rounds;
    private final Winner winner;

    MatchResult(
            AI player,
            AI opponent,
            State gameEngineInitialState,
            int playerScore,
            int opponentScore,
            int rounds,
            Winner winner) {

        this.player = player;
        this.opponent = opponent;
        this.gameEngineInitialState = gameEngineInitialState;

        this.playerScore = playerScore;
        this.opponentScore = opponentScore;
        this.rounds = rounds;
        this.winner = winner;
    }

    public AI getPlayer() {
        return player;
    }

    public AI getOpponent() {
        return opponent;
    }

    public State getGameEngineInitialState() {
        return gameEngineInitialState;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public int getRounds() {
        return rounds;
    }

    public Winner getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("winner", getWinner())
                .add("playerScore", getPlayerScore())
                .add("opponentScore", getPlayerScore())
                .add("rounds", getRounds())
                .add("player", getPlayer())
                .add("opponent", getOpponent())
                .add("gameEngineInitialState", getGameEngineInitialState())
                .toString();
    }
}
