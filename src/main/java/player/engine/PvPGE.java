package player.engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import player.Player.AI;
import player.Player.Action;
import player.Player.Spot;

public class PvPGE extends ConfigurableGE {

    static final int GRID_X = 30;
    static final int GRID_Y = 20;

    private final boolean playerFirst;
    private final Set<Spot> spotHistory;

    private Spot playerStartSpot;
    private Spot opponentStartSpot;

    private Spot playerCurrentSpot;
    private Spot opponentCurrentSpot;

    private int playerScore;
    private int opponentScore;

    private final Supplier<Spot> spotGenerator;

    PvPGE(boolean playerFirst, Supplier<Spot> spotGenerator) {
        super(Collections.emptyMap());
        this.playerFirst = playerFirst;
        this.spotHistory = new HashSet<>();
        this.spotGenerator = spotGenerator;
        this.playerScore = 0;
        this.opponentScore = 0;
    }

    public PvPGE() {
        this(new Random());
    }

    private PvPGE(Random random) {
        this(random.nextBoolean(), () -> new Spot(random.nextInt(GRID_X), random.nextInt(GRID_Y)));
    }

    @Override
    public void start() {
        playerStartSpot = spotGenerator.get();
        playerCurrentSpot = playerStartSpot;
        spotHistory.add(playerStartSpot);

        opponentStartSpot = spotGenerator.get();
        opponentCurrentSpot = opponentStartSpot;
        spotHistory.add(opponentStartSpot);
    }

    @Override
    protected Winner runRound(AI player, AI opponent) {
        if (playerFirst) {
            Winner winner = playerRound(player);
            if (winner != Winner.ON_GOING) {
                return winner;
            }

            return opponentRound(opponent);
        } else {
            Winner winner = opponentRound(opponent);
            if (winner != Winner.ON_GOING) {
                return winner;
            }

            return playerRound(player);
        }
    }

    private Winner playerRound(AI player) {
        toPlayerInput(2, playerFirst ? 0 : 1);

        if (playerFirst) {
            toPlayerInput(playerStartSpot);
            toPlayerInput(playerCurrentSpot);

            toPlayerInput(opponentStartSpot);
            toPlayerInput(opponentCurrentSpot);
        } else {
            toPlayerInput(opponentStartSpot);
            toPlayerInput(opponentCurrentSpot);

            toPlayerInput(playerStartSpot);
            toPlayerInput(playerCurrentSpot);
        }

        player.updateRepository();

        Action playerAction = player.play()[0];
        playerCurrentSpot = playerCurrentSpot.next(playerAction.getType());

        if (spotHistory.contains(playerCurrentSpot) || !spotBelongsToGrid(playerCurrentSpot)) {
            return Winner.OPPONENT;
        }

        playerScore++;
        spotHistory.add(playerCurrentSpot);

        return Winner.ON_GOING;
    }

    private Winner opponentRound(AI opponent) {
        toOpponentInput(2, playerFirst ? 0 : 1);

        if (playerFirst) {
            toOpponentInput(playerStartSpot);
            toOpponentInput(playerCurrentSpot);

            toOpponentInput(opponentStartSpot);
            toOpponentInput(opponentCurrentSpot);
        } else {
            toOpponentInput(opponentStartSpot);
            toOpponentInput(opponentCurrentSpot);

            toOpponentInput(playerStartSpot);
            toOpponentInput(playerCurrentSpot);
        }

        opponent.updateRepository();

        Action opponentAction = opponent.play()[0];
        opponentCurrentSpot = opponentCurrentSpot.next(opponentAction.getType());

        if (spotHistory.contains(opponentCurrentSpot) || !spotBelongsToGrid(opponentCurrentSpot)) {
            return Winner.PLAYER;
        }

        opponentScore++;
        spotHistory.add(opponentCurrentSpot);

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

    private static boolean spotBelongsToGrid(Spot spot) {
        return !(spot.getX() < 0 || spot.getX() >= GRID_X) && !(spot.getY() < 0 || spot.getY() >= GRID_Y);
    }
}
