package player.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

//FIXME: get rid of dependency
import player.engine.MockedGE.Builder;

public class MultipleRoundMockedGE implements GameEngine {

    private final Iterator<GameEngine> rounds;
    private final State initialState;
    private GameEngine currentState;
    private int runCount;

    public MultipleRoundMockedGE(Builder startState, Builder... rounds) {
        List<GameEngine> r = new ArrayList<>();

        for (Builder round : rounds) {
            r.add(round.build());
        }

        this.rounds = r.iterator();
        this.currentState = startState.build();
        this.initialState = currentState.getInitialState();
        this.runCount = 0;
    }

    @Override
    public void run(AI player, AI opponentActions) {
        runCount++;
        currentState = rounds.next();
    }

    @Override
    public Winner getWinner() {
        return currentState.getWinner();
    }

    @Override
    public int playerInput() {
        return currentState.playerInput();
    }

    @Override
    public int opponentInput() {
        return currentState.opponentInput();
    }

    @Override
    public int getPlayerScore() {
        return currentState.getPlayerScore();
    }

    @Override
    public int getOpponentScore() {
        return currentState.getOpponentScore();
    }

    @Override
    public int getNumberOfRounds() {
        return currentState.getNumberOfRounds();
    }

    @Override
    public State getInitialState() {
        return initialState;
    }

    public int getRunCount() {
        return runCount;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
