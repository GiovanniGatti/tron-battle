package player.engine;

import static player.MockedAI.newBuilder;
import static player.Player.ActionsType.LEFT;
import static player.Player.ActionsType.RIGHT;
import static player.Player.ActionsType.UP;
import static player.engine.Winner.ON_GOING;
import static player.engine.Winner.OPPONENT;
import static player.engine.Winner.PLAYER;

import java.util.Arrays;
import java.util.Iterator;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.MockedAI;
import player.MockedAI.Builder;
import player.MultipleRoundMockedAI;
import player.Player.AI;
import player.Player.Action;
import player.Player.Spot;

@DisplayName("A PvP Game Engine")
class PvPGETest implements WithAssertions {

    @Test
    @DisplayName("finishes before opponent move if player kills himself when player plays first")
    void playerGoesFirst() {
        Spot playerStartSpot = new Spot(29, 10);
        Spot opponentStartSpot = new Spot(0, 10);

        Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
        PvPGE ge = new PvPGE(true, inputStream::next);
        ge.start();

        AI player = MockedAI.anyWithActions(new Action(RIGHT));
        AI opponent = MockedAI.anyWithActions(new Action(LEFT));

        ge.run(player, opponent);

        assertThat(ge.getWinner()).isEqualTo(OPPONENT);
    }

    @Test
    @DisplayName("finishes before player move if opponent kills himself when opponent plays first")
    void opponentGoesFirst() {
        Spot playerStartSpot = new Spot(29, 10);
        Spot opponentStartSpot = new Spot(0, 10);

        Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
        PvPGE ge = new PvPGE(false, inputStream::next);
        ge.start();

        AI player = MockedAI.anyWithActions(new Action(RIGHT));
        AI opponent = MockedAI.anyWithActions(new Action(LEFT));

        ge.run(player, opponent);

        assertThat(ge.getWinner()).isEqualTo(PLAYER);
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

                Spot playerStartSpot = new Spot(29, 10);
                Spot opponentStartSpot = new Spot(15, 10);

                Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
                PvPGE ge = new PvPGE(true, inputStream::next);
                ge.start();

                AI player = MockedAI.anyWithActions(new Action(RIGHT));
                AI opponent = MockedAI.anyWithActions(new Action(RIGHT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(OPPONENT);
            }

            @Test
            @DisplayName("if player collides against himself")
            void whenPlayerCollidesAgainstHimself() {

                Spot playerStartSpot = new Spot(15, 10);
                Spot opponentStartSpot = new Spot(0, 0);

                Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
                PvPGE ge = new PvPGE(true, inputStream::next);
                ge.start();

                Builder playerRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder playerRound2 = newBuilder().withActions(new Action(LEFT));
                MultipleRoundMockedAI player = new MultipleRoundMockedAI(playerRound1, playerRound2);

                Builder opponentRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder opponentRound2 = newBuilder().withActions(new Action(RIGHT));
                MultipleRoundMockedAI opponent = new MultipleRoundMockedAI(opponentRound1, opponentRound2);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(ON_GOING);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(OPPONENT);
            }

            @Test
            @DisplayName("if player collides against opponent")
            void whenPlayerCollidesAgainstOpponent() {
                Spot playerStartSpot = new Spot(15, 11);
                Spot opponentStartSpot = new Spot(15, 10);

                Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
                PvPGE ge = new PvPGE(true, inputStream::next);
                ge.start();

                AI player = MockedAI.anyWithActions(new Action(UP));
                AI opponent = MockedAI.anyWithActions(new Action(LEFT));

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
                Spot playerStartSpot = new Spot(15, 10);
                Spot opponentStartSpot = new Spot(29, 10);

                Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
                PvPGE ge = new PvPGE(true, inputStream::next);
                ge.start();

                AI player = MockedAI.anyWithActions(new Action(RIGHT));
                AI opponent = MockedAI.anyWithActions(new Action(RIGHT));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(PLAYER);
            }

            @Test
            @DisplayName("if opponent collides against himself")
            void whenOpponentCollidesAgainstHimself() {

                Spot playerStartSpot = new Spot(0, 0);
                Spot opponentStartSpot = new Spot(15, 10);

                Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
                PvPGE ge = new PvPGE(true, inputStream::next);
                ge.start();

                Builder playerRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder playerRound2 = newBuilder().withActions(new Action(RIGHT));
                MultipleRoundMockedAI player = new MultipleRoundMockedAI(playerRound1, playerRound2);

                Builder opponentRound1 = newBuilder().withActions(new Action(RIGHT));
                Builder opponentRound2 = newBuilder().withActions(new Action(LEFT));
                MultipleRoundMockedAI opponent = new MultipleRoundMockedAI(opponentRound1, opponentRound2);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(ON_GOING);

                ge.run(player, opponent);
                assertThat(ge.getWinner()).isEqualTo(PLAYER);
            }

            @Test
            @DisplayName("if opponent collides against player")
            void whenOpponentCollidesAgainstPlayer() {
                Spot playerStartSpot = new Spot(15, 10);
                Spot opponentStartSpot = new Spot(15, 11);

                Iterator<Spot> inputStream = Arrays.asList(playerStartSpot, opponentStartSpot).iterator();
                PvPGE ge = new PvPGE(true, inputStream::next);
                ge.start();

                AI player = MockedAI.anyWithActions(new Action(LEFT));
                AI opponent = MockedAI.anyWithActions(new Action(UP));

                ge.run(player, opponent);

                assertThat(ge.getWinner()).isEqualTo(PLAYER);
            }
        }
    }
}