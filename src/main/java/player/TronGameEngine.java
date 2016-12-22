package player;

import player.Player.BattleFieldSnapshot;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class TronGameEngine {

    private static final int MAX_X = 30;
    private static final int MAX_Y = 20;

    // TODO: shouldn't depend on playerId no more
    private final boolean[] dead;

    private final Player.TronLightCycle[] lightCycles;

    // mutable internal state
    private final boolean[][] grid;
    private final Map<Player.Spot, Boolean> isDead;
    private final BattleFieldSnapshot battleField;
    private final Map<Player.Spot, Player.Spot> currentSpots;
    private final Map<Player.Spot, Set<Player.Spot>> visitedSpots;

    // TODO: strict or relaxed should be more a property class, rather than a method parameter.
    public TronGameEngine(BattleFieldSnapshot battleField) {

        this.battleField = battleField;
        this.grid = new boolean[MAX_Y][MAX_X];

        Set<Player.Spot> startSpots = battleField.getStartSpots();
        this.currentSpots = new HashMap<>(startSpots.size());
        this.isDead = new HashMap<>(startSpots.size());

        for (Player.Spot spot : startSpots) {
            isDead.put(spot, false);
            currentSpots.put(spot, battleField.getCurrentSpot(spot));
        }
    }

    public void perform(boolean strict, Player.Spot startSpot, Player.ActionsType action) {

        if (!battleField.playerOnGrid(startSpot) ||
                !isDead.containsKey(startSpot) ||
                !currentSpots.containsKey(startSpot)) {
            throw new IllegalStateException("Unknown user starting at " + startSpot);
        }

        if (strict && isDead.get(startSpot)) {
            return;
        }

        Player.Spot current = currentSpots.get(startSpot);
        Player.Spot next = current.next(action);

        if (next.getX() >= 0 && next.getX() < MAX_X && next.getY() >= 0 && next.getY() < MAX_Y) {
            // TODO: only one grid is not enough when player dies.
            if (!battleField.hasBeenVisited(next) && !grid[next.getY()][next.getX()]) {
                // valid movement, then perform it
                grid[next.getY()][next.getX()] = true;
                currentSpots.put(startSpot, next);
                return;
            }
        }

        // invalid movement
        if (strict) {
            // if on strict mode, player is taken off the grid
            Iterator<Player.Spot> visitedSpots = this.visitedSpots.get(startSpot).iterator();
            Iterator<Player.Spot> battleFieldSpots = battleField.getVisitedSpots(startSpot).iterator();
            ConcatIterator<Player.Spot> concatIterator =
                    new ConcatIterator<>(Arrays.asList(visitedSpots, battleFieldSpots));
            concatIterator.forEachRemaining(spot -> grid[spot.getY()][spot.getX()] = false);
        }

        isDead.put(startSpot, true);
    }

    public void perform(int playerN, Player.Action action) {
        perform(true, playerN, action);
    }

    public void perform(boolean strict, int playerN, Player.Action action) {
        perform(strict, playerN, action.getType());
    }

    public void perform(boolean strict, int playerN, Player.ActionsType action) {

        if (strict && dead[playerN]) {
            return;
        }

        Player.TronLightCycle lightCycle = lightCycles[playerN];

        Player.Spot current = lightCycle.getCurrent();
        Player.Spot next = current.next(action);

        if (next.getX() >= 0 && next.getX() < MAX_X && next.getY() >= 0 && next.getY() < MAX_Y) {
            if (!grid[next.getY()][next.getX()]) {
                // valid movement, then perform it
                lightCycle.moveTo(next);
                grid[next.getY()][next.getX()] = true;
                return;
            }
        }

        // invalid movement

        if (strict) {
            // if on strict mode, player is taken off the grid
            for (Player.Spot spot : lightCycle.getVisitedSpots()) {
                grid[spot.getY()][spot.getX()] = false;
            }
        }

        dead[playerN] = true;
    }

    public boolean isDead(int playerN) {
        return dead[playerN];
    }

    public Player.Spot getCurrent(int playerN) {
        return lightCycles[playerN].getCurrent();
    }

    public Player.Spot getStart(int playerN) {
        return lightCycles[playerN].getStart();
    }

    // @Override
    // public String toString() {
    // StringBuilder str = new StringBuilder();
    // for (int i = 0; i < MAX_Y; i++) {
    // for (int j = 0; j < MAX_X; j++) {
    // boolean found = false;
    // for (TronLightCycle lightCycle : lightCycles) {
    // if (lightCycle.getVisitedSpots().contains(new Spot(j, i))) {
    // str.append(lightCycle.getPlayerN());
    // found = true;
    // break;
    // }
    // }
    // if (!found) {
    // str.append('.');
    // }
    // }
    // str.append('\n');
    // }
    //
    // return str.toString();
    // }
}
