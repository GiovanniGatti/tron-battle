package player.match;

import java.util.function.Function;
import java.util.function.Supplier;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.MockedAI;
import player.Player;
import player.engine.GameEngine;
import player.engine.MockedGE;
import player.engine.MultipleRoundMockedGE;
import player.engine.Winner;
import player.match.Match.MatchResult;

@DisplayName("A match")
class MatchTest implements WithAssertions {

    @Test
    @DisplayName("starts up only once the provided game engine")
    void startUpGameEngine() {
        MockedGE.Builder start = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round1 = MockedGE.newBuilder().withWinner(Winner.PLAYER);

        MultipleRoundMockedGE gameEngine = new MultipleRoundMockedGE(start, round1);

        Supplier<GameEngine> gameEngineSupplier = () -> gameEngine;

        Match match = new Match(anyAIInput(), anyAIInput(), gameEngineSupplier);

        match.call();

        assertThat(gameEngine.getStartCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("is played until someone wins")
    void playUntilWinner() {
        MockedGE.Builder start = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round1 = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round2 = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round3 = MockedGE.newBuilder().withWinner(Winner.PLAYER);

        MultipleRoundMockedGE gameEngine = new MultipleRoundMockedGE(start, round1, round2, round3);

        Supplier<GameEngine> gameEngineSupplier = () -> gameEngine;

        Match match = new Match(anyAIInput(), anyAIInput(), gameEngineSupplier);

        match.call();

        assertThat(gameEngine.getRunCount()).isEqualTo(3);
    }

    @Nested
    @DisplayName("returns")
    class MatchResults {

        @Test
        @DisplayName("the right winner")
        void rightWinner() {
            Supplier<GameEngine> gameEngineBuild = () -> MockedGE.anyWithWinner(Winner.OPPONENT);

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getWinner()).isEqualTo(Winner.OPPONENT);
        }

        @Test
        @DisplayName("the right player score")
        void playerScore() {
            Supplier<GameEngine> gameEngineBuild = () -> MockedGE.anyWithPlayerScore(17);

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getPlayerScore()).isEqualTo(17);
        }

        @Test
        @DisplayName("the right opponent score")
        void opponentScore() {
            Supplier<GameEngine> gameEngineBuild = () -> MockedGE.anyWithOpponentScore(17);

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getOpponentScore()).isEqualTo(17);
        }

        @Test
        @DisplayName("the right number of rounds")
        void numberOfRounds() {
            Supplier<GameEngine> gameEngineBuild = () -> MockedGE.anyWithNumberOfRounds(3);

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getRounds()).isEqualTo(3);
        }
    }

    private static Function<Supplier<Integer>, Supplier<Player.AI>> anyAIInput() {
        return (input) -> MockedAI::any;
    }
}