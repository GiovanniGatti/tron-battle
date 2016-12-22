package player.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import player.Player.AI;
import player.Player.Action;
import player.Player.Spot;
import player.Player.TronLightCycle;
import player.TronGameEngine;

public class PvPGE extends ConfigurableGE {

    private final TronGameEngine gameEngine;
    private final Spot[] startSpots;

    private final boolean playerFirst;

    private int playerScore;
    private int opponentScore;

    PvPGE(boolean playerFirst, TronGameEngine gameEngine) {
        super(Collections.emptyMap());
        this.playerFirst = playerFirst;
        this.gameEngine = gameEngine;
        this.startSpots = new Spot[] { gameEngine.getStart(0), gameEngine.getStart(1) };
        this.playerScore = 0;
        this.opponentScore = 0;
    }

    public PvPGE(boolean playerFirst, TronLightCycle... lightCycles) {
        this(playerFirst, new TronGameEngine(lightCycles));
    }

    @Override
    public void start() {
        // ILB
    }

    @Override
    protected Winner runRound(AI player, AI opponent) {
        int firstId, secondId;
        AI first, second;
        Consumer<Spot> firstInput, secondInput;
        Consumer<Integer> metadataFirstInput, metadataSecondInput;

        if (playerFirst) {
            first = player;
            firstInput = this::toPlayerInput;
            metadataFirstInput = this::toPlayerInput;
            firstId = 0;

            second = opponent;
            secondInput = this::toOpponentInput;
            metadataSecondInput = this::toOpponentInput;
            secondId = 1;
        } else {
            first = opponent;
            firstInput = this::toOpponentInput;
            metadataFirstInput = this::toOpponentInput;
            firstId = 1;

            second = player;
            secondInput = this::toPlayerInput;
            metadataSecondInput = this::toPlayerInput;
            secondId = 0;
        }

        metadataFirstInput.accept(2);
        metadataFirstInput.accept(0);

        firstInput.accept(gameEngine.getStart(firstId));
        firstInput.accept(gameEngine.getCurrent(firstId));
        firstInput.accept(gameEngine.getStart(secondId));
        firstInput.accept(gameEngine.getCurrent(secondId));

        first.updateRepository();
        Action firstAction = first.play()[0];

        gameEngine.perform(firstId, firstAction);

        if (gameEngine.isDead(firstId)) {
            return playerFirst ? Winner.OPPONENT : Winner.PLAYER;
        }

        if (playerFirst) {
            playerScore++;
        } else {
            opponentScore++;
        }

        metadataSecondInput.accept(2);
        metadataSecondInput.accept(1);

        secondInput.accept(gameEngine.getStart(firstId));
        secondInput.accept(gameEngine.getCurrent(firstId));
        secondInput.accept(gameEngine.getStart(secondId));
        secondInput.accept(gameEngine.getCurrent(secondId));

        second.updateRepository();
        Action secondAction = second.play()[0];

        gameEngine.perform(secondId, secondAction);

        if (gameEngine.isDead(secondId)) {
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
                Arrays.equals(startSpots, pvPGE.startSpots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startSpots, playerFirst);
    }
    //
    // @Override
    // public String toString() {
    // return gameEngine.toString();
    // }
}
