import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("A tron game engine")
class TronGameEngineTest implements WithAssertions {

    @Test
    @DisplayName("keeps the player alive when anyone else collides against him")
    void keepsThePlayerAliveWhenAnyoneElseCollidesAgainstHim() {
        Player.Spot player1 = new Player.Spot(5, 5);
        Player.Spot player2 = new Player.Spot(5, 6);

        TronGameEngine ge = newWithEmptyBattleField(player1, player2);

        ge.perform(player2, Player.ActionsType.UP);

        assertThat(ge.isDead(player1)).isFalse();
    }

    @Test
    @DisplayName("takes of dead players from the map")
    void takesOfDeadPlayersFromTheMap() {
        Player.Spot player1 = new Player.Spot(5, 5);
        Player.Spot player2 = new Player.Spot(7, 5);
        Player.Spot player3 = new Player.Spot(7, 3);

        TronGameEngine ge = newWithEmptyBattleField(player1, player2, player3);

        ge.perform(player1, Player.ActionsType.RIGHT);
        ge.perform(player2, Player.ActionsType.LEFT); // 2 collides against 1
        ge.perform(player3, Player.ActionsType.DOWN);

        assertThat(ge.isDead(player1)).isFalse();
        assertThat(ge.isDead(player2)).isTrue();
        assertThat(ge.isDead(player3)).isFalse();

        ge.perform(player1, Player.ActionsType.UP);
        ge.perform(player3, Player.ActionsType.DOWN); // 2 is dead, than no collision occurs

        assertThat(ge.isDead(player1)).isFalse();
        assertThat(ge.isDead(player2)).isTrue();
        assertThat(ge.isDead(player3)).isFalse();

        ge.perform(player1, Player.ActionsType.RIGHT); // 1 collides against 3
        ge.perform(player3, Player.ActionsType.LEFT); // 1 is dead, than no collision occurs

        assertThat(ge.isDead(player1)).isTrue();
        assertThat(ge.isDead(player2)).isTrue();
        assertThat(ge.isDead(player3)).isFalse();
    }

    @Test
    @DisplayName("does not let dead players to perform movements")
    void doesNotLetDeadPlayersToPerformMovements() {
        Player.Spot player = new Player.Spot(5, 5);

        TronGameEngine ge = newWithEmptyBattleField(player);

        ge.perform(player, Player.ActionsType.UP);
        ge.perform(player, Player.ActionsType.DOWN); // kills himself

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ge.perform(player, Player.ActionsType.LEFT))
                .withMessage("Unknown user starting at (5, 5)");
    }

    @Nested
    @DisplayName("marks player as dead")
    class MarksPlayerAsDead {

        @Test
        @DisplayName("when he goes through the map's north border")
        void whenHeGoesThroughTheMapsNorthBorder() {
            Player.Spot player = new Player.Spot(15, 0);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, Player.ActionsType.UP);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's south border")
        void whenHeGoesThroughTheMapsSouthBorder() {
            Player.Spot player = new Player.Spot(15, 19);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, Player.ActionsType.DOWN);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's east border")
        void whenHeGoesThroughTheMapsEastBorder() {
            Player.Spot player = new Player.Spot(0, 10);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, Player.ActionsType.LEFT);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's west border")
        void whenHeGoesThroughTheMapsWestBorder() {
            Player.Spot player = new Player.Spot(29, 10);

            TronGameEngine ge = newWithEmptyBattleField(player);

            ge.perform(player, Player.ActionsType.RIGHT);

            assertThat(ge.isDead(player)).isTrue();
        }

        @Test
        @DisplayName("when he collides against another player")
        void whenHeCollidesAgainstAnotherPlayer() {
            Player.Spot player1 = new Player.Spot(5, 5);
            Player.Spot player2 = new Player.Spot(5, 6);

            TronGameEngine ge = newWithEmptyBattleField(player1, player2);

            ge.perform(player1, Player.ActionsType.DOWN);

            assertThat(ge.isDead(player1)).isTrue();
        }
    }

    private static TronGameEngine newWithEmptyBattleField(Player.Spot playerSpot, Player.Spot... playersStartSpot) {
        Player.BattleField battleField = new Player.BattleField();

        Stream.concat(Stream.of(playerSpot), Stream.of(playersStartSpot))
                .forEach((spot) -> battleField.addLightCycleAt(spot, spot));

        return new TronGameEngine(battleField);
    }
}