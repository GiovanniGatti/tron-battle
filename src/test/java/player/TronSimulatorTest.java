package player;

import static player.Player.ActionsType.DOWN;
import static player.Player.ActionsType.LEFT;
import static player.Player.ActionsType.RIGHT;
import static player.Player.ActionsType.UP;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.Player.BattleField;
import player.Player.Spot;
import player.Player.TronSimulator;

@DisplayName("A Tron Simulator Engine")
class TronSimulatorTest implements WithAssertions {

    @Nested
    @DisplayName("returns false")
    class EndsTheMatch {

        @Test
        @DisplayName("if player goes outside the grid")
        void whenPlayerGoesOutsideTheGrid() {

            Spot playerStartSpot = new Spot(29, 10);
            Spot opponentStartSpot = new Spot(0, 10);

            TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, RIGHT)).isFalse();
        }

        @Test
        @DisplayName("if player collides against himself")
        void whenPlayerCollidesAgainstHimself() {

            Spot playerStartSpot = new Spot(15, 10);
            Spot opponentStartSpot = new Spot(0, 0);

            TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, RIGHT)).isTrue();
            assertThat(ge.perform(playerStartSpot, LEFT)).isFalse();
        }

        @Test
        @DisplayName("if player collides against opponent")
        void whenPlayerCollidesAgainstOpponent() {

            Spot playerStartSpot = new Spot(15, 11);
            Spot opponentStartSpot = new Spot(15, 10);

            TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

            assertThat(ge.perform(playerStartSpot, UP)).isFalse();
        }
    }

    @Test
    @DisplayName("can simulate a full match")
    void canSimulateAFullMatch() {
        Spot playerStartSpot = new Spot(9, 4);
        Spot opponentStartSpot = new Spot(29, 2);

        // automatically generated
        Player.ActionsType[] playerActions = { UP, LEFT, DOWN, DOWN, RIGHT, RIGHT, UP, UP, UP, LEFT, LEFT, LEFT, DOWN,
                DOWN, DOWN, DOWN, RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, UP, RIGHT, DOWN, DOWN, DOWN, DOWN, DOWN,
                DOWN, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, UP, UP, UP, UP, UP, UP, LEFT, DOWN, DOWN, DOWN, DOWN,
                DOWN, DOWN, DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, UP,
                UP, RIGHT, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT,
                LEFT, LEFT, LEFT, UP, UP, UP, UP, UP, UP, UP, LEFT, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN,
                DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP,
                RIGHT, UP };

        Player.ActionsType[] opponentActions = { UP, UP, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT,
                LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT,
                LEFT, LEFT, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN,
                DOWN, DOWN, DOWN, DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT,
                RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT,
                RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP,
                LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, DOWN,
                DOWN, DOWN, DOWN, DOWN, UP };

        TronSimulator ge = withFreshBattleField(playerStartSpot, opponentStartSpot);

        for (int i = 0; i < playerActions.length - 1; i++) {
            assertThat(ge.perform(playerStartSpot, playerActions[i])).isTrue();
            assertThat(ge.perform(opponentStartSpot, opponentActions[i])).isTrue();
        }

        assertThat(ge.perform(playerStartSpot, playerActions[playerActions.length - 1])).isTrue();
        assertThat(ge.perform(opponentStartSpot, opponentActions[playerActions.length - 1])).isFalse();
    }

    public static TronSimulator withFreshBattleField(Spot playerStartSpot, Spot opponentStartSpot) {

        BattleField battleField = new BattleField();
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot);

        return new TronSimulator(battleField.getSnapshot());
    }

}