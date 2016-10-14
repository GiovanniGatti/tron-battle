package player;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import player.Player.InputRepository;
import player.Player.Spot;
import player.Player.TronLightCycle;

@DisplayName("An input repository")
class InputRepositoryTest implements WithAssertions {

    @Test
    @DisplayName("reads the correct number of players")
    void loadsCorrectNumberOfPlayers() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 0, anyPoint(), new Spot[] { anyPoint(), anyPoint() });
        InputRepository repo = new InputRepository(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getN()).isEqualTo(3);
    }

    @Test
    @DisplayName("reads the correct player id")
    void loadsCorrectPlayerId() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Spot[] { anyPoint(), anyPoint() });
        InputRepository repo = new InputRepository(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getP()).isEqualTo(2);
    }

    @Test
    @DisplayName("returns the player lightcycle")
    void returnsThePlayerLightCycle() {
        Spot playerStartingSpot = anyPoint();

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, playerStartingSpot, new Spot[] { anyPoint(), anyPoint() });
        InputRepository repo = new InputRepository(inputProvider::inputStream);
        repo.update();

        TronLightCycle playerLightCycle = repo.getPlayerLightCycle();
        assertThat(playerLightCycle.getPlayerN()).isEqualTo(2);
        assertThat(playerLightCycle.getCurrent()).isEqualTo(playerStartingSpot);
        assertThat(playerLightCycle.getVisitedSpots()).containsOnly(playerStartingSpot);
    }

    @Test
    @DisplayName("returns all opponent lightcycles")
    void returnsAllOpponentLightCycles() {
        Spot opponent1LightCycle = anyPoint();
        Spot opponent2LightCycle = anyPoint();

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Spot[] { opponent1LightCycle, opponent2LightCycle });
        InputRepository repo = new InputRepository(inputProvider::inputStream);
        repo.update();

        List<TronLightCycle> opponentLightCycles = repo.getOpponentLightCycles();

        assertThat(opponentLightCycles)
                .extracting(TronLightCycle::getPlayerN)
                .contains(0, 1);

        assertThat(opponentLightCycles)
                .extracting(TronLightCycle::getStart)
                .contains(opponent1LightCycle, opponent2LightCycle);
    }

    // TODO: fixme, missing lots of test cases. PlayerInputProvider does not provided an API for multiple round
    // testing...

    private static class PlayerInputProvider {

        private final Queue<Integer> inputStream;

        PlayerInputProvider(
                int N,
                int P,
                Spot playerP0,
                Spot[] opponentsP0) {

            this(N, P, playerP0, playerP0, opponentsP0, opponentsP0);
        }

        PlayerInputProvider(
                int N,
                int P,
                Spot playerP0,
                Spot playerP1,
                Spot[] opponentsP0,
                Spot[] opponentsP1) {

            Preconditions.checkNotNullOrEmpty(opponentsP0);

            inputStream = new ArrayDeque<>();

            inputStream.add(N);
            inputStream.add(P);

            Spot[] spots = new Spot[2 * N];

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

            for (Spot p : spots) {
                inputStream.add(p.getX());
                inputStream.add(p.getY());
            }
        }

        int inputStream() {
            return inputStream.remove();
        }
    }

    private static Spot anyPoint() {
        Random random = new Random();
        return new Spot(random.nextInt(KnowledgeRepo.GRID_X), random.nextInt(KnowledgeRepo.GRID_Y));
    }
}