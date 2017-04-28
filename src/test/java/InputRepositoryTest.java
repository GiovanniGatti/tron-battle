import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("An input repository")
class InputRepositoryTest implements WithAssertions {

    @Test
    @DisplayName("reads the correct number of players")
    void loadsCorrectNumberOfPlayers() {
        //This test is buggy --> points might be the same
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 0, anyPoint(), new Player.Spot[] { anyPoint(), anyPoint() });
        Player.InputRepository repo = new Player.InputRepository(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getN()).isEqualTo(3);
    }

    @Test
    @DisplayName("reads the correct player id")
    void loadsCorrectPlayerId() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Player.Spot[] { anyPoint(), anyPoint() });
        Player.InputRepository repo = new Player.InputRepository(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getP()).isEqualTo(2);
    }

    @Test
    @DisplayName("returns the player lightcycle")
    void returnsThePlayerLightCycle() {
        Player.Spot playerStartingSpot = anyPoint();

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, playerStartingSpot, new Player.Spot[] { anyPoint(), anyPoint() });
        Player.InputRepository repo = new Player.InputRepository(inputProvider::inputStream);
        repo.update();

        Player.Spot playerLightCycle = repo.getPlayerLightCycleStartSpot();
        assertThat(playerLightCycle).isEqualTo(playerStartingSpot);
    }

    @Test
    @DisplayName("returns all opponent lightcycles")
    void returnsAllOpponentLightCycles() {
        Player.Spot opponent1LightCycle = anyPoint();
        Player.Spot opponent2LightCycle = anyPoint();

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Player.Spot[] { opponent1LightCycle, opponent2LightCycle });
        Player.InputRepository repo = new Player.InputRepository(inputProvider::inputStream);
        repo.update();

        Set<Player.Spot> opponentLightCycles = repo.getOpponentLightCyclesStartSpot();

        assertThat(opponentLightCycles)
                .contains(opponent1LightCycle, opponent2LightCycle);
    }

    // TODO: fixme, missing lots of test cases. PlayerInputProvider does not provided an API for multiple round
    // testing...

    private static class PlayerInputProvider {

        private final Queue<Integer> inputStream;

        PlayerInputProvider(
                int N,
                int P,
                Player.Spot playerP0,
                Player.Spot[] opponentsP0) {

            this(N, P, playerP0, playerP0, opponentsP0, opponentsP0);
        }

        PlayerInputProvider(
                int N,
                int P,
                Player.Spot playerP0,
                Player.Spot playerP1,
                Player.Spot[] opponentsP0,
                Player.Spot[] opponentsP1) {

            Preconditions.checkNotNullOrEmpty(opponentsP0);

            inputStream = new ArrayDeque<>();

            inputStream.add(N);
            inputStream.add(P);

            Player.Spot[] spots = new Player.Spot[2 * N];

            for (int i = 0; i < N; i++) {
                if (i == P) {
                    spots[2 * i] = playerP0;
                    spots[2 * i + 1] = playerP1;
                } else if (i < P) {
                    spots[2 * i] = opponentsP0[i];
                    spots[2 * i + 1] = opponentsP1[i];
                } else {
                    spots[2 * i] = opponentsP0[i - 1];
                    spots[2 * i + 1] = opponentsP1[i - 1];
                }
            }

            for (Player.Spot p : spots) {
                inputStream.add(p.getX());
                inputStream.add(p.getY());
            }
        }

        int inputStream() {
            return inputStream.remove();
        }
    }

    private static Player.Spot anyPoint() {
        Random random = new Random();
        return new Player.Spot(random.nextInt(KnowledgeRepo.GRID_X), random.nextInt(KnowledgeRepo.GRID_Y));
    }
}