import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntSupplier;


public class KnowledgeRepo extends Player.Repository {

    static final int GRID_X = 30;
    static final int GRID_Y = 20;

    private final List<Player.Spot> playerSpots;
    private final List<Player.Spot> opponentSpots;

    private int N;
    private int P;

    private Player.Spot playerStartingSpot;
    private Player.Spot playerCurrentSpot;

    KnowledgeRepo(IntSupplier inputSupplier) {
        super(inputSupplier);
        this.playerSpots = new ArrayList<>();
        this.opponentSpots = new ArrayList<>();
    }

    @Override
    public void update() {
        N = readInput(); // total number of players (2 to 4).
        P = readInput(); // your player number (0 to 3).

        for (int i = 0; i < N; i++) {

            int X0 = readInput(); // starting X coordinate of lightcycle (or -1)
            int Y0 = readInput(); // starting Y coordinate of lightcycle (or -1)

            int X1 = readInput(); // starting X coordinate of lightcycle (can be the same as X0 if you
            // play before this player)
            int Y1 = readInput(); // starting Y coordinate of lightcycle (can be the same as Y0 if you
            // play before this player)

            Player.Spot startSpot = new Player.Spot(X0, Y0);
            Player.Spot currentSpot = new Player.Spot(X1, Y1);

            if ((P == 0) == (i == 0)) {
                if (playerSpots.isEmpty() && !currentSpot.equals(startSpot)) {
                    playerSpots.add(startSpot);
                }
                playerSpots.add(currentSpot);

                playerStartingSpot = startSpot;
                playerCurrentSpot = currentSpot;
            } else {
                if (opponentSpots.isEmpty() && !currentSpot.equals(startSpot)) {
                    opponentSpots.add(startSpot);
                }
                opponentSpots.add(currentSpot);
            }
        }
    }

    public int getN() {
        return N;
    }

    public int getP() {
        return P;
    }

    public List<Player.Spot> getPlayerSpots() {
        return Collections.unmodifiableList(playerSpots);
    }

    public List<Player.Spot> getOpponentSpots() {
        return Collections.unmodifiableList(opponentSpots);
    }

    public List<Player.ActionsType> getPossibleActions() {
        return getPossibleActionsFor(playerCurrentSpot);
    }

    public List<Player.ActionsType> getPossibleActionsFor(Player.Spot spot) {
        List<Player.ActionsType> possibleActions = new ArrayList<>();
        possibleActions.addAll(Arrays.asList(Player.ActionsType.values()));

        if (spot.getX() - 1 < 0
                || playerSpots.contains(new Player.Spot(spot.getX() - 1, spot.getY()))
                || opponentSpots.contains(new Player.Spot(spot.getX() - 1, spot.getY()))) {
            possibleActions.remove(Player.ActionsType.LEFT);
        }

        if (spot.getX() + 1 > (GRID_X - 1)
                || playerSpots.contains(new Player.Spot(spot.getX() + 1, spot.getY()))
                || opponentSpots.contains(new Player.Spot(spot.getX() + 1, spot.getY()))) {
            possibleActions.remove(Player.ActionsType.RIGHT);
        }

        if (spot.getY() - 1 < 0
                || playerSpots.contains(new Player.Spot(spot.getX(), spot.getY() - 1))
                || opponentSpots.contains(new Player.Spot(spot.getX(), spot.getY() - 1))) {
            possibleActions.remove(Player.ActionsType.UP);
        }

        if (spot.getY() + 1 > (GRID_Y - 1)
                || playerSpots.contains(new Player.Spot(spot.getX(), spot.getY() + 1))
                || opponentSpots.contains(new Player.Spot(spot.getX(), spot.getY() + 1))) {
            possibleActions.remove(Player.ActionsType.DOWN);
        }

        return possibleActions;
    }

    public Player.Spot getPlayerStartingSpot() {
        return playerStartingSpot;
    }

    public Player.Spot getPlayerCurrentSpot() {
        return playerCurrentSpot;
    }
}