import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class TronGameEngine {

    private static final int MAX_X = 30;
    private static final int MAX_Y = 20;

    private final Player.BattleField battleField;
    private final Set<Player.Spot> deadPlayers;

    public TronGameEngine(Player.BattleField battleField) {
        this.battleField = new Player.BattleField(battleField);
        this.deadPlayers = new HashSet<>();
    }

    public void perform(Player.Spot startSpot, Player.ActionsType action) {

        if (!battleField.hasLightCycleStartingAt(startSpot)) {
            throw new IllegalArgumentException("Unknown user starting at " + startSpot);
        }

        Player.Spot current = battleField.getCurrentSpot(startSpot);
        Player.Spot next = current.next(action);

        if (next.getX() >= 0 && next.getX() < MAX_X && next.getY() >= 0 && next.getY() < MAX_Y) {
            if (!battleField.hasBeenVisited(next)) {
                battleField.moveTo(startSpot, next);
                return;
            }
        }

        // invalid movement
        deadPlayers.add(startSpot);
        battleField.killLightCycles(new HashSet<>(Collections.singletonList(startSpot)));
    }

    public boolean isDead(Player.Spot startSpot) {
        return deadPlayers.contains(startSpot);
    }

    public Player.Spot getCurrent(Player.Spot startSpot) {
        return battleField.getCurrentSpot(startSpot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TronGameEngine that = (TronGameEngine) o;
        return Objects.equals(battleField, that.battleField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleField);
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
