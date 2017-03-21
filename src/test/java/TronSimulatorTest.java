import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("A Tron Simulator Engine")
class TronSimulatorTest implements WithAssertions {

    @Nested
    @DisplayName("returns false")
    class EndsTheMatch {

        @Test
        @DisplayName("if player goes outside the grid by right border")
        void whenPlayerGoesOutsideTheGridByRightBorder() {

            Player.Spot playerStartSpot = new Player.Spot(29, 10);
            Player.Spot opponentStartSpot = new Player.Spot(0, 10);

            Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, Player.ActionsType.RIGHT)).isFalse();
        }

        @Test
        @DisplayName("if player goes outside the grid by left border")
        void whenPlayerGoesOutsideTheGridByLeftBorder() {

            Player.Spot playerStartSpot = new Player.Spot(0, 10);
            Player.Spot opponentStartSpot = new Player.Spot(0, 10);

            Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, Player.ActionsType.LEFT)).isFalse();
        }

        @Test
        @DisplayName("if player goes outside the grid by upper border")
        void whenPlayerGoesOutsideTheGridByUpperBorder() {

            Player.Spot playerStartSpot = new Player.Spot(15, 0);
            Player.Spot opponentStartSpot = new Player.Spot(0, 10);

            Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, Player.ActionsType.UP)).isFalse();
        }

        @Test
        @DisplayName("if player goes outside the grid by lower border")
        void whenPlayerGoesOutsideTheGridByLowerBorder() {

            Player.Spot playerStartSpot = new Player.Spot(15, 19);
            Player.Spot opponentStartSpot = new Player.Spot(0, 10);

            Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, Player.ActionsType.DOWN)).isFalse();
        }

        @Test
        @DisplayName("if player collides against himself")
        void whenPlayerCollidesAgainstHimself() {

            Player.Spot playerStartSpot = new Player.Spot(15, 10);
            Player.Spot opponentStartSpot = new Player.Spot(0, 0);

            Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, Player.ActionsType.RIGHT)).isTrue();
            assertThat(ge.perform(playerStartSpot, Player.ActionsType.LEFT)).isFalse();
        }

        @Test
        @DisplayName("if player collides against opponent")
        void whenPlayerCollidesAgainstOpponent() {

            Player.Spot playerStartSpot = new Player.Spot(15, 11);
            Player.Spot opponentStartSpot = new Player.Spot(15, 10);

            Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, Player.ActionsType.UP)).isFalse();
        }
    }

    @Test
    @DisplayName("can simulate a full match")
    void canSimulateAFullMatch() {
        Player.Spot playerStartSpot = new Player.Spot(9, 4);
        Player.Spot opponentStartSpot = new Player.Spot(29, 2);

        // automatically generated
        Player.ActionsType[] playerActions = { Player.ActionsType.UP, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.RIGHT, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.LEFT, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.RIGHT, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.UP,
                Player.ActionsType.RIGHT, Player.ActionsType.UP };

        Player.ActionsType[] opponentActions = { Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                Player.ActionsType.UP };

        Player.TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

        for (int i = 0; i < playerActions.length - 1; i++) {
            assertThat(ge.perform(playerStartSpot, playerActions[i])).isTrue();
            assertThat(ge.perform(opponentStartSpot, opponentActions[i])).isTrue();
        }

        assertThat(ge.perform(playerStartSpot, playerActions[playerActions.length - 1])).isTrue();
        assertThat(ge.perform(opponentStartSpot, opponentActions[playerActions.length - 1])).isFalse();
    }

    public static Player.TronSimulator withFreshBattleField(Player.Spot playerStartSpot, Player.Spot opponentStartSpot) {

        Player.BattleField battleField = new Player.BattleField();
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot);

        return new Player.TronSimulator(battleField.getSnapshot());
    }

}