package player;

import static player.Player.ActionsType.DOWN;
import static player.Player.ActionsType.LEFT;
import static player.Player.ActionsType.RIGHT;
import static player.Player.ActionsType.UP;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.Player.Action;
import player.Player.BattleField;
import player.Player.Spot;
import player.Player.TronLightCycle;

@DisplayName("A tron game engine")
class TronGameEngineTest implements WithAssertions {

    @Test
    @DisplayName("keeps the player alive when anyone else collides against him")
    void keepsThePlayerAliveWhenAnyoneElseCollidesAgainstHim() {
        TronGameEngine ge =
                new TronGameEngine(
                        new TronLightCycle(0, new Spot(5, 5)),
                        new TronLightCycle(1, new Spot(5, 6)));

        ge.perform(1, new Action(UP));

        assertThat(ge.isDead(0)).isFalse();
    }

    @Test
    @DisplayName("takes of dead players from the map on strict mode")
    void takesOfDeadPlayersFromTheMapOnStrictMode() {
        TronGameEngine ge = new TronGameEngine(
                new TronLightCycle(0, new Spot(5, 5)),
                new TronLightCycle(1, new Spot(7, 5)),
                new TronLightCycle(2, new Spot(7, 3)));

        ge.perform(true, 0, new Action(RIGHT));
        ge.perform(true, 1, new Action(LEFT)); // 1 collides against 0
        ge.perform(true, 2, new Action(DOWN));

        assertThat(ge.isDead(0)).isFalse();
        assertThat(ge.isDead(1)).isTrue();
        assertThat(ge.isDead(2)).isFalse();

        ge.perform(true, 0, new Action(UP));
        ge.perform(true, 2, new Action(DOWN)); // 1 is dead, than no collision occurs

        assertThat(ge.isDead(0)).isFalse();
        assertThat(ge.isDead(1)).isTrue();
        assertThat(ge.isDead(2)).isFalse();

        ge.perform(true, 0, new Action(RIGHT)); // 0 collides against 2
        ge.perform(true, 2, new Action(LEFT)); // 0 is dead, than no collision occurs

        assertThat(ge.isDead(0)).isTrue();
        assertThat(ge.isDead(1)).isTrue();
        assertThat(ge.isDead(2)).isFalse();
    }

    @Test
    @DisplayName("does not take dead players from the map on relaxed mode")
    void doesNotTakeDeadPlayersFromTheMapOnRelaxedMode() {
        TronGameEngine ge =
                new TronGameEngine(
                        new TronLightCycle(0, new Spot(5, 5)),
                        new TronLightCycle(1, new Spot(7, 5)),
                        new TronLightCycle(2, new Spot(7, 3)));

        ge.perform(false, 0, new Action(RIGHT));
        ge.perform(false, 1, new Action(LEFT)); // 1 collides against 0
        ge.perform(false, 2, new Action(DOWN));

        assertThat(ge.isDead(0)).isFalse();
        assertThat(ge.isDead(1)).isTrue();
        assertThat(ge.isDead(2)).isFalse();

        ge.perform(false, 0, new Action(UP));
        ge.perform(false, 2, new Action(DOWN)); // 1 is dead, but as we are on relaxed mode, he is still there

        assertThat(ge.isDead(0)).isFalse();
        assertThat(ge.isDead(1)).isTrue();
        assertThat(ge.isDead(2)).isTrue();

        ge.perform(false, 0, new Action(RIGHT));// 1 is dead, but as we are on relaxed mode, he is still there

        assertThat(ge.isDead(0)).isTrue();
        assertThat(ge.isDead(1)).isTrue();
        assertThat(ge.isDead(2)).isTrue();
    }

    @Test
    @DisplayName("does not let dead players to perform movements on strict mode")
    void doesNotLetDeadPlayersToPerformMovementsOnStrictMode() {
        TronGameEngine ge = new TronGameEngine(new TronLightCycle(0, new Spot(5, 5)));

        ge.perform(true, 0, new Action(UP));
        ge.perform(true, 0, new Action(DOWN)); // kills himself

        ge.perform(true, 0, new Action(LEFT));

        assertThat(ge.getCurrent(0)).isEqualTo(new Spot(5, 4));
    }

    @Test
    @DisplayName("lets dead players to perform movements on relaxed mode")
    void doesNotLetDeadPlayersToPerformMovementsOnRelaxedMode() {
        TronGameEngine ge = new TronGameEngine(new TronLightCycle(0, new Spot(5, 5)));

        ge.perform(false, 0, new Action(UP));
        ge.perform(false, 0, new Action(DOWN)); // kills himself

        ge.perform(false, 0, new Action(LEFT));

        assertThat(ge.getCurrent(0)).isEqualTo(new Spot(4, 4));
        assertThat(ge.isDead(0)).isTrue();
    }

    @Nested
    @DisplayName("marks player as dead")
    class MarksPlayerAsDead {

        @Test
        @DisplayName("when he goes through the map's north border")
        void whenHeGoesThroughTheMapsNorthBorder() {
            TronGameEngine ge = new TronGameEngine(new TronLightCycle(0, new Spot(15, 0)));

            ge.perform(0, new Action(UP));

            assertThat(ge.isDead(0)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's south border")
        void whenHeGoesThroughTheMapsSouthBorder() {
            TronGameEngine ge = new TronGameEngine(new TronLightCycle(0, new Spot(15, 19)));

            ge.perform(0, new Action(DOWN));

            assertThat(ge.isDead(0)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's east border")
        void whenHeGoesThroughTheMapsEastBorder() {
            TronGameEngine ge = new TronGameEngine(new TronLightCycle(0, new Spot(0, 10)));

            ge.perform(0, new Action(LEFT));

            assertThat(ge.isDead(0)).isTrue();
        }

        @Test
        @DisplayName("when he goes through the map's west border")
        void whenHeGoesThroughTheMapsWestBorder() {
            TronGameEngine ge = new TronGameEngine(new TronLightCycle(0, new Spot(29, 10)));

            ge.perform(0, new Action(RIGHT));

            assertThat(ge.isDead(0)).isTrue();
        }

        @Test
        @DisplayName("when he collides against another player")
        void whenHeCollidesAgainstAnotherPlayer() {
            TronGameEngine ge =
                    new TronGameEngine(
                            new TronLightCycle(0, new Spot(5, 5)),
                            new TronLightCycle(1, new Spot(5, 6)));

            ge.perform(0, new Action(DOWN));

            assertThat(ge.isDead(0)).isTrue();
        }
    }

    private static TronGameEngine newWithEmptyBattleField(Spot playerStartSpot, Spot opponentStartSpot) {
        BattleField battleField = new BattleField();
        battleField.addLightCycleAt(playerStartSpot, playerStartSpot);
        battleField.addLightCycleAt(opponentStartSpot, opponentStartSpot);
        return new TronGameEngine(battleField);
    }
}