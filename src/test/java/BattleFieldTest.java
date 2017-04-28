import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The battlefield")
class BattleFieldTest implements WithAssertions {

    @Test
    @DisplayName("correctly returns all spots as available when no player is in field anymore")
    void correctlyReturnsAllSpotsAsAvailableWhenNoPlayerIsInFieldAnymore() {

        Player.Spot playerStartSpot = new Player.Spot(0, 0);
        Player.Spot opponentStartSpot = new Player.Spot(0, 1);

        BattleFieldHelper battleField = new BattleFieldHelper(new Player.BattleField());
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot.next(Player.ActionsType.RIGHT));
        battleField.moveTo(playerStartSpot, Player.ActionsType.RIGHT);
        battleField.moveTo(opponentStartSpot, Player.ActionsType.RIGHT);
        battleField.moveTo(playerStartSpot, Player.ActionsType.RIGHT);

        battleField.killLightCycles(playerStartSpot, opponentStartSpot);

        Player.GridSize gridSize = battleField.getGridSize();
        assertThat(battleField.getAvailableSpotsCount()).isEqualTo(gridSize.getMaxX() * gridSize.getMaxY());
    }

    @Test
    @DisplayName("correctly returns all spots as available when player is killed")
    void correctlyReturnsAllSpotsAsAvailableWhenPlayerIsKilled() {

        Player.Spot playerStartSpot = new Player.Spot(0, 0);
        Player.Spot opponentStartSpot = new Player.Spot(0, 1);

        BattleFieldHelper battleField = new BattleFieldHelper(new Player.BattleField());
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot.next(Player.ActionsType.RIGHT));
        battleField.moveTo(playerStartSpot, Player.ActionsType.RIGHT);
        battleField.moveTo(opponentStartSpot, Player.ActionsType.RIGHT);
        battleField.moveTo(playerStartSpot, Player.ActionsType.RIGHT);

        battleField.killLightCycles(playerStartSpot);

        Player.GridSize gridSize = battleField.getGridSize();
        assertThat(battleField.getAvailableSpotsCount()).isEqualTo(gridSize.getMaxX() * gridSize.getMaxY() - 3);
    }

    @Test
    @DisplayName("correctly keeps track of available spots")
    void correctlyKeepsTrackOfAvailableSpots() {
        Player.Spot playerStartSpot = new Player.Spot(0, 0);
        Player.Spot opponentStartSpot = new Player.Spot(15, 10);

        BattleFieldHelper battleField = new BattleFieldHelper(new Player.BattleField());
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot.next(Player.ActionsType.UP));
        battleField.moveTo(playerStartSpot, Player.ActionsType.RIGHT);

        Player.GridSize gridSize = battleField.getGridSize();
        assertThat(battleField.getAvailableSpotsCount()).isEqualTo(gridSize.getMaxX() * gridSize.getMaxY() - 4);
    }

    private static class BattleFieldHelper {

        private final Player.BattleField battleField;

        BattleFieldHelper(Player.BattleField battleField) {
            this.battleField = battleField;
        }

        void moveTo(Player.Spot startSpot, Player.ActionsType actionsType) {
            battleField.moveTo(startSpot, battleField.getCurrentSpot(startSpot).next(actionsType));
        }

        public void addLightCycleAt(Player.Spot startSpot, Player.Spot currentSpot) {
            battleField.addLightCycleAt(startSpot, currentSpot);
        }

        public Player.GridSize getGridSize() {
            return battleField.getGridSize();
        }

        public int getAvailableSpotsCount() {
            return battleField.getAvailableSpotsCount();
        }

        public void killLightCycles(Player.Spot spot, Player.Spot... spots) {
            Set<Player.Spot> all = Stream.concat(Stream.of(spot), Arrays.stream(spots))
                    .collect(Collectors.toSet());
            battleField.killLightCycles(all);
        }
    }
}