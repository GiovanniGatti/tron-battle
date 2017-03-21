package player;

import static player.MockedAI.newBuilder;
import static player.Player.ActionsType.DOWN;
import static player.Player.ActionsType.LEFT;
import static player.Player.ActionsType.RIGHT;
import static player.Player.ActionsType.UP;
import static player.engine.Winner.ON_GOING;
import static player.engine.Winner.OPPONENT;
import static player.engine.Winner.PLAYER;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.MockedAI.Builder;
import player.Player.Action;
import player.Player.ActionsType;
import player.Player.Spot;

@DisplayName("A PvP Game Engine")
class PvPGETest implements WithAssertions {

    @Test
    @DisplayName("finishes before opponent move if player kills himself when player plays first")
    void playerGoesFirst() {

        PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(29, 10), new Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Action(RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Action(LEFT));

        ge.run(player, opponent);

        assertThat(ge.getWinner()).isEqualTo(OPPONENT);
    }

    @Test
    @DisplayName("finishes before player move if opponent kills himself when opponent plays first")
    void opponentGoesFirst() {

        PvPGE ge = PvPGE.withFreshBattleField(false, new Spot(29, 10), new Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Action(RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Action(LEFT));

        ge.run(player, opponent);

        assertThat(ge.getWinner()).isEqualTo(PLAYER);
    }

    @Test
    @DisplayName("maps correctly input when player starts")
    void mapsInputWhenPlayerStarts() {

        PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(15, 10), new Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Action(RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Action(LEFT));

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

        PvPGE ge = PvPGE.withFreshBattleField(false, new Spot(15, 10), new Spot(0, 10));

        AIMapper player = MockedAI.anyWithActions(new Action(RIGHT));
        AIMapper opponent = MockedAI.anyWithActions(new Action(RIGHT));

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

                PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(29, 10), new Spot(15, 10));

                AIMapper player = MockedAI.anyWithActions(new Action(RIGHT));
                AIMapper opponent = MockedAI.anyWithActions(new Action(RIGHT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(OPPONENT);
            }

            @Test
            @DisplayName("if player collides against himself")
            void whenPlayerCollidesAgainstHimself() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(15, 10), new Spot(0, 0));

                Builder playerRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder playerRound2 = newBuilder().withActions(new Action(LEFT));
                AIMapper player = new AIMapper(new MultipleRoundMockedAI(playerRound1, playerRound2));

                Builder opponentRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder opponentRound2 = newBuilder().withActions(new Action(RIGHT));
                AIMapper opponent = new AIMapper(new MultipleRoundMockedAI(opponentRound1, opponentRound2));

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(ON_GOING);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(OPPONENT);
            }

            @Test
            @DisplayName("if player collides against opponent")
            void whenPlayerCollidesAgainstOpponent() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(15, 11), new Spot(15, 10));

                AIMapper player = MockedAI.anyWithActions(new Action(UP));
                AIMapper opponent = MockedAI.anyWithActions(new Action(LEFT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(OPPONENT);
            }
        }

        @Nested
        @DisplayName("with player as winner")
        class WithPlayerAsWinner {

            @Test
            @DisplayName("with player as winner if opponent goes outside the grid")
            public void whenOpponentGoesOusideTheGrid() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(15, 10), new Spot(29, 10));

                AIMapper player = MockedAI.anyWithActions(new Action(RIGHT));
                AIMapper opponent = MockedAI.anyWithActions(new Action(RIGHT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(PLAYER);
            }

            @Test
            @DisplayName("if opponent collides against himself")
            void whenOpponentCollidesAgainstHimself() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(0, 0), new Spot(15, 10));

                Builder playerRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder playerRound2 = newBuilder().withActions(new Action(RIGHT));
                AIMapper player = new AIMapper(new MultipleRoundMockedAI(playerRound1, playerRound2));

                Builder opponentRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder opponentRound2 = newBuilder().withActions(new Action(LEFT));
                AIMapper opponent = new AIMapper(new MultipleRoundMockedAI(opponentRound1, opponentRound2));

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(ON_GOING);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(PLAYER);
            }

            @Test
            @DisplayName("if opponent collides against player")
            void whenOpponentCollidesAgainstPlayer() {

                PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(15, 10), new Spot(15, 11));

                AIMapper player = MockedAI.anyWithActions(new Action(LEFT));
                AIMapper opponent = MockedAI.anyWithActions(new Action(UP));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(PLAYER);
            }
        }
    }

    @Test
    @DisplayName("can simulate a full match")
    void canSimulateAFullMatch() {
        // automatically generated
        Action[] playerActions =
                actionsOf(UP, LEFT, DOWN, DOWN, RIGHT, RIGHT, UP, UP, UP, LEFT, LEFT, LEFT, DOWN, DOWN, DOWN, DOWN,
                        RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, UP, RIGHT, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN,
                        LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, UP, UP, UP, UP, UP, UP, LEFT, DOWN, DOWN, DOWN, DOWN,
                        DOWN, DOWN, DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, UP,
                        UP, RIGHT, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT,
                        LEFT, LEFT, LEFT, UP, UP, UP, UP, UP, UP, UP, LEFT, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN,
                        DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP,
                        RIGHT, UP);

        List<Builder> builders = new ArrayList<>();
        for (Action action : playerActions) {
            builders.add(newBuilder().withActions(action));
        }

        AIMapper player = new AIMapper(new MultipleRoundMockedAI(builders.toArray(new Builder[builders.size()])));

        Action[] opponentActions =
                actionsOf(UP, UP, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT,
                        LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, DOWN,
                        DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN,
                        DOWN, DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT,
                        RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT,
                        RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP, UP,
                        LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, DOWN,
                        DOWN, DOWN, DOWN, DOWN, UP);

        List<Builder> builders2 = new ArrayList<>();
        for (Action action : opponentActions) {
            builders2.add(newBuilder().withActions(action));
        }

        AIMapper opponent = new AIMapper(new MultipleRoundMockedAI(builders2.toArray(new Builder[builders2.size()])));

        PvPGE ge = PvPGE.withFreshBattleField(true, new Spot(9, 4), new Spot(29, 2));

        while (ge.getWinner() == ON_GOING) {
            ge.run(player, opponent);
        }

        assertThat(ge.getWinner()).isEqualTo(PLAYER);
        assertThat(ge.getNumberOfRounds()).isEqualTo(115);
    }

    private static Action[] actionsOf(ActionsType... actions) {
        Action[] a = new Action[actions.length];
        for (int i = 0; i < actions.length; i++) {
            a[i] = new Action(actions[i]);
        }

        return a;
    }
}