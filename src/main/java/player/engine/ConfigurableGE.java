package player.engine;

import static player.Player.AI;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import com.google.common.collect.ImmutableMap;

//FIXME: get rid of this class
public abstract class ConfigurableGE implements GameEngine {

    private Winner winner;
    private int rounds;

    private final Map<String, Object> conf;
    private final Queue<Integer> playerInput;
    private final Queue<Integer> opponentInput;

    protected ConfigurableGE(Map<String, Object> conf) {
        this.rounds = 0;
        this.winner = Winner.ON_GOING;
        this.conf = ImmutableMap.copyOf(conf);
        this.playerInput = new ArrayDeque<>();
        this.opponentInput = new ArrayDeque<>();
    }

    Map<String, Object> getConf() {
        return conf;
    }

    protected abstract Winner runRound(AI player, AI opponent);

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
    public void run(AI player, AI opponent) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigurableGE that = (ConfigurableGE) o;
        return Objects.equals(conf, that.conf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conf);
    }
}
