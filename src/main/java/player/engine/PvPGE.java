package player.engine;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

import player.Player.AI;
import player.Player.Action;
import player.Player.BattleField;
import player.Player.Spot;
import player.TronGameEngine;

public final class PvPGE extends ConfigurableGE {

    private final BattleField initialState;

    private final TronGameEngine gameEngine;

    private final boolean playerFirst;
    private final Spot playerStartSpot;
    private final Spot opponentStartSpot;

    private int playerScore;
    private int opponentScore;

    public static PvPGE withFreshBattleField(boolean playerFirst, Spot playerStartSpot, Spot opponentStartSpot) {

        BattleField battleField = new BattleField();
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot);

        return new PvPGE(playerFirst, battleField, playerStartSpot, opponentStartSpot);
    }

    public PvPGE(
            boolean playerFirst,
            BattleField battleField,
            Spot playerStartSpot,
            Spot opponentStartSpot) {

        super(Collections.emptyMap());

        Preconditions.checkArgument(battleField.hasLightCycleStartingAt(playerStartSpot),
                "Player could not be found in grid at " + playerStartSpot);
        Preconditions.checkArgument(battleField.hasLightCycleStartingAt(opponentStartSpot),
                "Opponent could not be found in grid at " + opponentStartSpot);
        Preconditions.checkArgument(battleField.getStartSpots().size() == 2,
                "Found more players than expected in the battle field");

        this.playerFirst = playerFirst;
        this.initialState = new BattleField(battleField);
        this.gameEngine = new TronGameEngine(battleField);
        this.playerStartSpot = playerStartSpot;
        this.opponentStartSpot = opponentStartSpot;

        this.playerScore = 0;
        this.opponentScore = 0;
    }

    @Override
    public void start() {
        // ILB
    }

    @Override
    protected Winner runRound(AI player, AI opponent) {
        Spot firstStartSpot, secondStartSpot;
        AI first, second;
        Consumer<Spot> firstInput, secondInput;
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
        Action firstAction = first.play()[0];

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
        Action secondAction = second.play()[0];

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

    private void toPlayerInput(Spot... spots) {
        for (Spot spot : spots) {
            super.toPlayerInput(spot.getX(), spot.getY());
        }
    }

    private void toOpponentInput(Spot... spots) {
        for (Spot spot : spots) {
            super.toOpponentInput(spot.getX(), spot.getY());
        }
    }

    @Override
    public boolean equals(Object o) {
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
}
