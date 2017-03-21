import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.engine.Winner;

@DisplayName("A PvP Game Engine")
class PvPGETest implements WithAssertions {

    @Test
    @DisplayName("finishes before opponent move if player kills himself when player plays first")
    void playerGoesFirst() {

        PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(29, 10), new Player.Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.LEFT));

        ge.run(player, opponent);

        assertThat(ge.getWinner()).isEqualTo(Winner.OPPONENT);
    }

    @Test
    @DisplayName("finishes before player move if opponent kills himself when opponent plays first")
    void opponentGoesFirst() {

        PvPGE ge = PvPGE.withFreshBattleField(false, new Player.Spot(29, 10), new Player.Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.LEFT));

        ge.run(player, opponent);

        assertThat(ge.getWinner()).isEqualTo(Winner.PLAYER);
    }

    @Test
    @DisplayName("maps correctly input when player starts")
    void mapsInputWhenPlayerStarts() {

        PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(15, 10), new Player.Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.LEFT));

        ge.run(player, opponent);

        assertThat(ge.playerInput()).isEqualTo(2); // N
        assertThat(ge.playerInput()).isEqualTo(0); // P

        assertThat(ge.playerInput()).isEqualTo(15);
        assertThat(ge.playerInput()).isEqualTo(10);
        assertThat(ge.playerInput()).isEqualTo(15);
        assertThat(ge.playerInput()).isEqualTo(10);

        assertThat(ge.playerInput()).isEqualTo(0);
        assertThat(ge.playerInput()).isEqualTo(10);
        assertThat(ge.playerInput()).isEqualTo(0);
        assertThat(ge.playerInput()).isEqualTo(10);

        assertThat(ge.opponentInput()).isEqualTo(2); // N
        assertThat(ge.opponentInput()).isEqualTo(1); // P

        assertThat(ge.opponentInput()).isEqualTo(15);
        assertThat(ge.opponentInput()).isEqualTo(10);
        assertThat(ge.opponentInput()).isEqualTo(16);
        assertThat(ge.opponentInput()).isEqualTo(10);

        assertThat(ge.opponentInput()).isEqualTo(0);
        assertThat(ge.opponentInput()).isEqualTo(10);
        assertThat(ge.opponentInput()).isEqualTo(0);
        assertThat(ge.opponentInput()).isEqualTo(10);
    }

    @Test
    @DisplayName("maps correctly input when opponent starts")
    void mapsInputWhenOpponentStarts() {

        PvPGE ge = PvPGE.withFreshBattleField(false, new Player.Spot(15, 10), new Player.Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));

        ge.run(player, opponent);

        assertThat(ge.opponentInput()).isEqualTo(2); // N
        assertThat(ge.opponentInput()).isEqualTo(0); // P

        assertThat(ge.opponentInput()).isEqualTo(0);
        assertThat(ge.opponentInput()).isEqualTo(10);
        assertThat(ge.opponentInput()).isEqualTo(0);
        assertThat(ge.opponentInput()).isEqualTo(10);

        assertThat(ge.opponentInput()).isEqualTo(15);
        assertThat(ge.opponentInput()).isEqualTo(10);
        assertThat(ge.opponentInput()).isEqualTo(15);
        assertThat(ge.opponentInput()).isEqualTo(10);

        assertThat(ge.playerInput()).isEqualTo(2); // N
        assertThat(ge.playerInput()).isEqualTo(1); // P

        assertThat(ge.playerInput()).isEqualTo(0);
        assertThat(ge.playerInput()).isEqualTo(10);
        assertThat(ge.playerInput()).isEqualTo(1);
        assertThat(ge.playerInput()).isEqualTo(10);

        assertThat(ge.playerInput()).isEqualTo(15);
        assertThat(ge.playerInput()).isEqualTo(10);
        assertThat(ge.playerInput()).isEqualTo(15);
        assertThat(ge.playerInput()).isEqualTo(10);
    }

    @Nested
    @DisplayName("ends the match")
    class EndsTheMatch {

        @Nested
        @DisplayName("with opponent as winner")
        class WithOpponentAsWinner {

            @Test
            @DisplayName("if player goes outside the grid")
            void whenPlayerGoesOusideTheGrid() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(29, 10), new Player.Spot(15, 10));

                AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));
                AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(Winner.OPPONENT);
            }

            @Test
            @DisplayName("if player collides against himself")
            void whenPlayerCollidesAgainstHimself() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(15, 10), new Player.Spot(0, 0));

                MockedAI.Builder playerRound1 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.RIGHT));
                MockedAI.Builder playerRound2 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.LEFT));
                AIMapper player = new AIMapper(new MultipleRoundMockedAI(playerRound1, playerRound2));

                MockedAI.Builder opponentRound1 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.RIGHT));
                MockedAI.Builder opponentRound2 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.RIGHT));
                AIMapper opponent = new AIMapper(new MultipleRoundMockedAI(opponentRound1, opponentRound2));

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(Winner.ON_GOING);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(Winner.OPPONENT);
            }

            @Test
            @DisplayName("if player collides against opponent")
            void whenPlayerCollidesAgainstOpponent() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(15, 11), new Player.Spot(15, 10));

                AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.UP));
                AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.LEFT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(Winner.OPPONENT);
            }
        }

        @Nested
        @DisplayName("with player as winner")
        class WithPlayerAsWinner {

            @Test
            @DisplayName("with player as winner if opponent goes outside the grid")
            public void whenOpponentGoesOusideTheGrid() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(15, 10), new Player.Spot(29, 10));

                AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));
                AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.RIGHT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(Winner.PLAYER);
            }

            @Test
            @DisplayName("if opponent collides against himself")
            void whenOpponentCollidesAgainstHimself() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(0, 0), new Player.Spot(15, 10));

                MockedAI.Builder playerRound1 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.RIGHT));
                MockedAI.Builder playerRound2 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.RIGHT));
                AIMapper player = new AIMapper(new MultipleRoundMockedAI(playerRound1, playerRound2));

                MockedAI.Builder opponentRound1 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.RIGHT));
                MockedAI.Builder opponentRound2 = MockedAI.newBuilder().withActions(
                        new Player.Action(Player.ActionsType.LEFT));
                AIMapper opponent = new AIMapper(new MultipleRoundMockedAI(opponentRound1, opponentRound2));

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(Winner.ON_GOING);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(Winner.PLAYER);
            }

            @Test
            @DisplayName("if opponent collides against player")
            void whenOpponentCollidesAgainstPlayer() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(15, 10), new Player.Spot(15, 11));

                AIMapper player = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.LEFT));
                AIMapper opponent = MockedAI.anyWithActions(new Player.Action(Player.ActionsType.UP));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(Winner.PLAYER);
            }
        }
    }

    @Test
    @DisplayName("can simulate a full match")
    void canSimulateAFullMatch() {
        // automatically generated
        Player.Action[] playerActions =
                actionsOf(Player.ActionsType.UP, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.RIGHT,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.RIGHT, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.LEFT, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.UP,
                        Player.ActionsType.RIGHT, Player.ActionsType.UP);

        List<MockedAI.Builder> builders = new ArrayList<>();
        for (Player.Action action : playerActions) {
            builders.add(MockedAI.newBuilder().withActions(action));
        }

        AIMapper player = new AIMapper(new MultipleRoundMockedAI(
                builders.toArray(new MockedAI.Builder[builders.size()])));

        Player.Action[] opponentActions =
                actionsOf(Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.RIGHT, Player.ActionsType.RIGHT,
                        Player.ActionsType.RIGHT, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP, Player.ActionsType.UP,
                        Player.ActionsType.UP,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.LEFT,
                        Player.ActionsType.LEFT, Player.ActionsType.LEFT, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.DOWN, Player.ActionsType.DOWN,
                        Player.ActionsType.DOWN, Player.ActionsType.UP);

        List<MockedAI.Builder> builders2 = new ArrayList<>();
        for (Player.Action action : opponentActions) {
            builders2.add(MockedAI.newBuilder().withActions(action));
        }

        AIMapper opponent = new AIMapper(new MultipleRoundMockedAI(builders2.toArray(new MockedAI.Builder[builders2
                .size()])));

        PvPGE ge = PvPGE.withFreshBattleField(true, new Player.Spot(9, 4), new Player.Spot(29, 2));

        while (ge.getWinner() == Winner.ON_GOING) {
            ge.run(player, opponent);
        }

        assertThat(ge.getWinner()).isEqualTo(Winner.PLAYER);
        assertThat(ge.getNumberOfRounds()).isEqualTo(115);
    }

    private static Player.Action[] actionsOf(Player.ActionsType... actions) {
        Player.Action[] a = new Player.Action[actions.length];
        for (int i = 0; i < actions.length; i++) {
            a[i] = new Player.Action(actions[i]);
        }

        return a;
    }
}