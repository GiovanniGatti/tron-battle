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
import player.Player.KnowledgeRepo;
import player.Player.Point;

@DisplayName("A knowledge repository")
class KnowledgeRepoTest implements WithAssertions {

    @Test
    @DisplayName("reads the correct number of players")
    void loadsCorrectNumberOfPlayers() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 0, anyPoint(), new Point[] { anyPoint(), anyPoint() });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.readInput();

        assertThat(repo.getN()).isEqualTo(3);
    }

    @Test
    @DisplayName("reads the correct player id")
    void loadsCorrectPlayerId() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Point[] { anyPoint(), anyPoint() });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.readInput();

        assertThat(repo.getP()).isEqualTo(2);
    }

    @Test
    @DisplayName("reads both player and opponent points")
    void loadsAllPlayerPoints() {
        Point p0 = new Point(0, 1);
        Point p1 = new Point(0, 2);

        Point opponentP0 = new Point(0, 3);
        Point opponentP1 = new Point(0, 4);

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(
                        2, 1,
                        p0, p1,
                        new Point[] { opponentP0 }, new Point[] { opponentP1 });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.readInput();

        assertThat(repo.getPlayerPoints()).containsOnly(p0, p1);
        assertThat(repo.getOpponentPoints()).containsOnly(opponentP0, opponentP1);
    }

    @Nested
    @DisplayName("when computing available movements")
    class WhenComputingAvailableMovements {

        @Test
        @DisplayName("returns right or down if player is at the upper left corner")
        void onLeftUpperCorner() {
            Point player = new Point(0, 0);
            Point opponent = new Point(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Point[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.readInput();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.DOWN, ActionsType.RIGHT);
        }

        @Test
        @DisplayName("returns up or right if player is at left lower corner")
        void onLeftLowerCorner() {
            Point player = new Point(0, 19);
            Point opponent = new Point(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Point[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.readInput();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.UP, ActionsType.RIGHT);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("returns left or down if player is at right upper corner")
        void onRightUpperCorner() {
            Point player = new Point(29, 0);
            Point opponent = new Point(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Point[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.readInput();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.DOWN, ActionsType.LEFT);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("returns left or up if player is at right lower corner")
        void onRightLowerCorner() {
            Point player = new Point(29, 19);
            Point opponent = new Point(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Point[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.readInput();
            List<ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(ActionsType.UP, ActionsType.LEFT);
        }
    }

    private static class PlayerInputProvider {

        private final Queue<Integer> inputStream;

        PlayerInputProvider(
                int N,
                int P,
                Point playerP0,
                Point[] opponentsP0) {

            this(N, P, playerP0, playerP0, opponentsP0, opponentsP0);
        }

        PlayerInputProvider(
                int N,
                int P,
                Point playerP0,
                Point playerP1,
                Point[] opponentsP0,
                Point[] opponentsP1) {

            Preconditions.checkNotNullOrEmpty(opponentsP0);

            inputStream = new ArrayDeque<>();

            inputStream.add(N);
            inputStream.add(P);

            Point[] points = new Point[2 * N];

            for (int i = 0; i < N; i++) {
                if (i == P) {
                    points[2 * i] = playerP0;
                    points[2 * i + 1] = playerP1;
                } else if (i < P) {
                    points[2 * i] = opponentsP0[i];
                    points[2 * i + 1] = opponentsP1[i];
                } else {
                    points[2 * i] = opponentsP0[i - 1];
                    points[2 * i + 1] = opponentsP1[i - 1];
                }
            }

            for (Point p : points) {
                inputStream.add(p.getX());
                inputStream.add(p.getY());
            }
        }

        int inputStream() {
            return inputStream.remove();
        }
    }

    private static Point anyPoint() {
        Random random = new Random();
        return new Point(random.nextInt(KnowledgeRepo.GRID_X), random.nextInt(KnowledgeRepo.GRID_Y));
    }

}