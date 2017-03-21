package player.engine;

import java.util.Objects;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("An abstract game engine")
class AbstractGETest implements WithAssertions {

    @Test
    @DisplayName("starts an on going match")
    void startsWithMatchOnGoing() {
        AbstractGE ge = new AbstractGEImpl(Winner.PLAYER);
        assertThat(ge.getWinner()).isEqualTo(Winner.ON_GOING);
    }

    @Test
    @DisplayName("returns the winner after first round")
    void returnsExpectedWinner() {
        AbstractGE ge = new AbstractGEImpl(Winner.PLAYER);

        ge.run(null, null);

        assertThat(ge.getWinner()).isEqualTo(Winner.PLAYER);
        assertThat(ge.getNumberOfRounds()).isEqualTo(1);
    }

    @Nested
    @DisplayName("when loading player AI input")
    class PlayerInput {

        @Test
        @DisplayName("send the expected values")
        void playerAIInput() {
            AbstractGE ge = new AbstractGEImpl(Winner.PLAYER);
            ge.toPlayerInput(0, 2, 4);

            assertThat(ge.playerInput()).isEqualTo(0);
            assertThat(ge.playerInput()).isEqualTo(2);
            assertThat(ge.playerInput()).isEqualTo(4);
        }

        @Test
        @DisplayName("throws ISE when no input is available")
        void throwICEWhenNoInputIsAvailable() {
            AbstractGE ge = new AbstractGEImpl(Winner.PLAYER);

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(ge::playerInput)
                    .withMessageContaining("No inputs are available to player");
        }
    }

    @Nested
    @DisplayName("when loading opponent AI input")
    class OpponentInput {

        @Test
        @DisplayName("send the expected values")
        void opponentAIInput() {
            AbstractGE ge = new AbstractGEImpl(Winner.PLAYER);
            ge.toOpponentInput(0, 2, 4);

            assertThat(ge.opponentInput()).isEqualTo(0);
            assertThat(ge.opponentInput()).isEqualTo(2);
            assertThat(ge.opponentInput()).isEqualTo(4);
        }

        @Test
        @DisplayName("throws ISE when no input is available")
        void throwICEWhenNoInputIsAvailable() {
            AbstractGE ge = new AbstractGEImpl(Winner.PLAYER);

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(ge::opponentInput)
                    .withMessageContaining("No inputs are available to opponent");
        }
    }

    private static class AbstractGEImpl extends AbstractGE<AnyAI> {

        private final Winner winner;

        AbstractGEImpl(Winner winner) {
            this.winner = winner;
        }

        @Override
        public int getPlayerScore() {
            return 0;
        }

        @Override
        public int getOpponentScore() {
            return 0;
        }

        @Override
        public State getInitialState() {
            return null;
        }

        @Override
        protected Winner runRound(AnyAI playerActions, AnyAI opponentActions) {
            return winner;
        }
    }

    private static class AnyAI implements AI {

        @Override
        public boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass());
        }
    }
}