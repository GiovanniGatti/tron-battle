package player;

import static player.Player.ActionsType.DOWN;
import static player.Player.ActionsType.LEFT;
import static player.Player.ActionsType.RIGHT;
import static player.Player.ActionsType.UP;

import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.Player.BattleField;
import player.Player.Spot;

@DisplayName("A tron game engine")
class TronGameEngineTest implements WithAssertions {

    @Test
    @DisplayName("keeps the player alive when anyone else collides against him")
    void keepsThePlayerAliveWhenAnyoneElseCollidesAgainstHim() {
        Spot player1 = new Spot(5, 5);
        Spot player2 = new Spot(5, 6);

        TronGameEngine ge = newWithEmptyBattleField(player1, player2);

        ge.perform(player2, UP);

        assertThat(ge.isDead(player1)).isFalse();
    }

    @Test
    @DisplayName("takes of dead players from the map")
    void takesOfDeadPlayersFromTheMap() {
        Spot player1 = new Spot(5, 5);
        Spot player2 = new Spot(7, 5);
        Spot player3 = new Spot(7, 3);

        TronGameEngine ge = newWithEmptyBattleField(player1, player2, player3);

        ge.perform(player1, RIGHT);
        ge.perform(player2, LEFT); // 2 collides against 1
        ge.perform(player3, DOWN);

        assertThat(ge.isDead(player1)).isFalse();
        assertThat(ge.isDead(player2)).isTrue();
        assertThat(ge.isDead(player3)).isFalse();

        ge.perform(player1, UP);
        ge.perform(player3, DOWN); // 2 is dead, than no collision occurs

        assertThat(ge.isDead(player1)).isFalse();
        assertThat(ge.isDead(player2)).isTrue();
        assertThat(ge.isDead(player3)).isFalse();

        ge.perform(player1, RIGHT); // 1 collides against 3
        ge.perform(player3, LEFT); // 1 is dead, than no collision occurs

        assertThat(ge.isDead(player1)).isTrue();
        assertThat(ge.isDead(player2)).isTrue();
        assertThat(ge.isDead(player3)).isFalse();
    }

    @Test
    @DisplayName("does not let dead players to perform movements")
    void doesNotLetDeadPlayersToPerformMovements() {
        Spot player = new Spot(5, 5);

        TronGameEngine ge = newWithEmptyBattleField(player);

        ge.perform(player, UP);
        ge.perform(player, DOWN); // kills himself

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ge.perform(player, LEFT))
                .withMessage("Unknown user starting at (5, 5)");
    }

    @Nested
    @DisplayName("marks player as dead")
    class MarksPlayerAsDead {

        @Test
        @DisplayName("when he goes through the map's north border")
        void whenHeGoesThroughTheMapsNorthBorder() {
            Spot player = new Spot(15, 0);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, UP);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's south border")
        void whenHeGoesThroughTheMapsSouthBorder() {
            Spot player = new Spot(15, 19);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, DOWN);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's east border")
        void whenHeGoesThroughTheMapsEastBorder() {
            Spot player = new Spot(0, 10);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, LEFT);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's west border")
        void whenHeGoesThroughTheMapsWestBorder() {
            Spot player = new Spot(29, 10);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, RIGHT);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he collides against another player")
        void whenHeCollidesAgainstAnotherPlayer() {
            Spot player1 = new Spot(5, 5);
            Spot player2 = new Spot(5, 6);

            TronGameEngine ge = newWithEmptyBattleField(player1, player2);

            ge.perform(player1, DOWN);

            assertThat(ge.isDead(player1)).isTrue();
        }
    }

    private static TronGameEngine newWithEmptyBattleField(Spot playerSpot, Spot... playersStartSpot) {
        BattleField battleField = new BattleField();

        Stream.concat(Stream.of(playerSpot), Stream.of(playersStartSpot))
                .forEach((spot) -> battleField.addLightCycleAt(spot, spot));

        return new TronGameEngine(battleField);
    }
}