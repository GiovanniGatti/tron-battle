package player.engine;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class AbstractGE<T extends AI> implements GameEngine<T> {

    private Winner winner;
    private int rounds;

    private final Queue<Integer> playerInput;
    private final Queue<Integer> opponentInput;

    protected AbstractGE() {
        this.rounds = 0;
        this.winner = Winner.ON_GOING;
        this.playerInput = new ArrayDeque<>();
        this.opponentInput = new ArrayDeque<>();
    }

    protected abstract Winner runRound(T player, T opponent);

    protected void toPlayerInput(int... values) {
        for (int value : values) {
            playerInput.add(value);
        }
    }

    protected void toOpponentInput(int... values) {
        for (int value : values) {
            opponentInput.add(value);
        }
    }

    @Override
    public void run(T player, T opponent) {
        this.winner = runRound(player, opponent);
        rounds++;
    }

    @Override
    public int playerInput() {
        if (playerInput.isEmpty()) {
            throw new IllegalStateException("No inputs are available to player");
        }
        return playerInput.poll();
    }

    @Override
    public int opponentInput() {
        if (opponentInput.isEmpty()) {
            throw new IllegalStateException("No inputs are available to opponent");
        }
        return opponentInput.poll();
    }

    @Override
    public int getNumberOfRounds() {
        return rounds;
    }

    @Override
    public Winner getWinner() {
        return winner;
    }
}
