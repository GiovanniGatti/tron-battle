package player;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import player.Player.ActionsType;
import player.Player.Spot;

@DisplayName("A knowledge repository")
class KnowledgeRepoTest implements WithAssertions {

    @Test
    @DisplayName("reads the correct number of players")
    void loadsCorrectNumberOfPlayers() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 0, anyPoint(), new Spot[] { anyPoint(), anyPoint() });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getN()).isEqualTo(3);
    }

    @Test
    @DisplayName("reads the correct player id")
    void loadsCorrectPlayerId() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Spot[] { anyPoint(), anyPoint() });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getP()).isEqualTo(2);
    }

    @Test
    @DisplayName("reads both player and opponent points")
    void loadsAllPlayerPoints() {
        Spot p0 = new Spot(0, 1);
        Spot p1 = new Spot(0, 2);

        Spot opponentP0 = new Spot(0, 3);
        Spot opponentP1 = new Spot(0, 4);

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(
                        2, 1,
                        p0, p1,
                        new Spot[] { opponentP0 }, new Spot[] { opponentP1 });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getPlayerSpots()).containsOnly(p0, p1);
        assertThat(repo.getOpponentSpots()).containsOnly(opponentP0, opponentP1);
    }

    @Nested
    @DisplayName("when computing available movements")
    class WhenComputingAvailableMovements {

        @Test
        @DisplayName("returns right or down if player is at the upper left corner")
        void onLeftUpperCorner() {
            Spot player = new Spot(0, 0);
            Spot opponent = new Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.DOWN, ActionsType.RIGHT);
        }

        @Test
        @DisplayName("returns up or right if player is at left lower corner")
        void onLeftLowerCorner() {
            Spot player = new Spot(0, 19);
            Spot opponent = new Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.UP, ActionsType.RIGHT);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("returns left or down if player is at right upper corner")
        void onRightUpperCorner() {
            Spot player = new Spot(29, 0);
            Spot opponent = new Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.DOWN, ActionsType.LEFT);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("returns left or up if player is at right lower corner")
        void onRightLowerCorner() {
            Spot player = new Spot(29, 19);
            Spot opponent = new Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.UP, ActionsType.LEFT);
        }
    }

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