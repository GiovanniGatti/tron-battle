import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

import player.engine.AbstractGE;
import player.engine.State;
import player.engine.Winner;

public final class PvPGE extends AbstractGE<AIMapper> {

    private final State initialState;

    private final TronGameEngine gameEngine;

    private final boolean playerFirst;
    private final Player.Spot playerStartSpot;
    private final Player.Spot opponentStartSpot;

    private int playerScore;
    private int opponentScore;

    public static PvPGE withFreshBattleField(boolean playerFirst, Player.Spot playerStartSpot,
            Player.Spot opponentStartSpot) {

        Player.BattleField battleField = new Player.BattleField();
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot);

        return new PvPGE(playerFirst, battleField, playerStartSpot, opponentStartSpot);
    }

    public PvPGE(
            boolean playerFirst,
            Player.BattleField battleField,
            Player.Spot playerStartSpot,
            Player.Spot opponentStartSpot) {

        Preconditions.checkArgument(battleField.hasLightCycleStartingAt(playerStartSpot),
                "Player could not be found in grid at " + playerStartSpot);
        Preconditions.checkArgument(battleField.hasLightCycleStartingAt(opponentStartSpot),
                "Opponent could not be found in grid at " + opponentStartSpot);
        Preconditions.checkArgument(battleField.getStartSpots().size() == 2,
                "Found more players than expected in the battle field");

        this.playerFirst = playerFirst;
        this.initialState = new InitialStateSnapshot(battleField);
        this.gameEngine = new TronGameEngine(battleField);
        this.playerStartSpot = playerStartSpot;
        this.opponentStartSpot = opponentStartSpot;

        this.playerScore = 0;
        this.opponentScore = 0;
    }

    @Override
    protected Winner runRound(AIMapper player, AIMapper opponent) {
        Player.Spot firstStartSpot, secondStartSpot;
        AIMapper first, second;
        Consumer<Player.Spot> firstInput, secondInput;
        Consumer<Integer> metadataFirstInput, metadataSecondInput;

        if (playerFirst) {
            first = player;
            firstInput = this::toPlayerInput;
            metadataFirstInput = this::toPlayerInput;
            firstStartSpot = playerStartSpot;

            second = opponent;
            secondInput = this::toOpponentInput;
            metadataSecondInput = this::toOpponentInput;
            secondStartSpot = opponentStartSpot;
        } else {
            first = opponent;
            firstInput = this::toOpponentInput;
            metadataFirstInput = this::toOpponentInput;
            firstStartSpot = opponentStartSpot;

            second = player;
            secondInput = this::toPlayerInput;
            metadataSecondInput = this::toPlayerInput;
            secondStartSpot = playerStartSpot;
        }

        metadataFirstInput.accept(2);
        metadataFirstInput.accept(0);

        firstInput.accept(firstStartSpot);
        firstInput.accept(gameEngine.getCurrent(firstStartSpot));
        firstInput.accept(secondStartSpot);
        firstInput.accept(gameEngine.getCurrent(secondStartSpot));

        first.updateRepository();
        Player.Action firstAction = first.play()[0];

        gameEngine.perform(firstStartSpot, firstAction.getType());

        if (gameEngine.isDead(firstStartSpot)) {
            return playerFirst ? Winner.OPPONENT : Winner.PLAYER;
        }

        if (playerFirst) {
            playerScore++;
        } else {
            opponentScore++;
        }

        metadataSecondInput.accept(2);
        metadataSecondInput.accept(1);

        secondInput.accept(firstStartSpot);
        secondInput.accept(gameEngine.getCurrent(firstStartSpot));
        secondInput.accept(secondStartSpot);
        secondInput.accept(gameEngine.getCurrent(secondStartSpot));

        second.updateRepository();
        Player.Action secondAction = second.play()[0];

        gameEngine.perform(secondStartSpot, secondAction.getType());

        if (gameEngine.isDead(secondStartSpot)) {
            return playerFirst ? Winner.PLAYER : Winner.OPPONENT;
        }

        if (playerFirst) {
            opponentScore++;
        } else {
            playerScore++;
        }

        return Winner.ON_GOING;
    }

    @Override
    public int getPlayerScore() {
        return playerScore;
    }

    @Override
    public int getOpponentScore() {
        return opponentScore;
    }

    @Override
    public State getInitialState() {
        return initialState;
    }

    private void toPlayerInput(Player.Spot... spots) {
        for (Player.Spot spot : spots) {
            super.toPlayerInput(spot.getX(), spot.getY());
        }
    }

    private void toOpponentInput(Player.Spot... spots) {
        for (Player.Spot spot : spots) {
            super.toOpponentInput(spot.getX(), spot.getY());
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        PvPGE pvPGE = (PvPGE) o;
        return playerFirst == pvPGE.playerFirst &&
                Objects.equals(initialState, pvPGE.initialState) &&
                Objects.equals(playerStartSpot, pvPGE.playerStartSpot) &&
                Objects.equals(opponentStartSpot, pvPGE.opponentStartSpot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), initialState, playerFirst, playerStartSpot, opponentStartSpot);
    }

    @Immutable
    private static class InitialStateSnapshot implements State {

        private final Player.BattleField battleField;

        public InitialStateSnapshot(Player.BattleField battleField) {
            this.battleField = new Player.BattleField(battleField);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            InitialStateSnapshot that = (InitialStateSnapshot) o;
            return Objects.equals(battleField, that.battleField);
        }

        @Override
        public int hashCode() {
            return Objects.hash(battleField);
        }
    }
}
