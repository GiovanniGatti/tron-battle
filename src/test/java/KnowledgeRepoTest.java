import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("A knowledge repository")
class KnowledgeRepoTest implements WithAssertions {

    @Test
    @DisplayName("reads the correct number of players")
    void loadsCorrectNumberOfPlayers() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 0, anyPoint(), new Player.Spot[] { anyPoint(), anyPoint() });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getN()).isEqualTo(3);
    }

    @Test
    @DisplayName("reads the correct player id")
    void loadsCorrectPlayerId() {
        PlayerInputProvider inputProvider =
                new PlayerInputProvider(3, 2, anyPoint(), new Player.Spot[] { anyPoint(), anyPoint() });
        KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
        repo.update();

        assertThat(repo.getP()).isEqualTo(2);
    }

    @Test
    @DisplayName("reads both player and opponent points")
    void loadsAllPlayerPoints() {
        Player.Spot p0 = new Player.Spot(0, 1);
        Player.Spot p1 = new Player.Spot(0, 2);

        Player.Spot opponentP0 = new Player.Spot(0, 3);
        Player.Spot opponentP1 = new Player.Spot(0, 4);

        PlayerInputProvider inputProvider =
                new PlayerInputProvider(
                        2, 1,
                        p0, p1,
                        new Player.Spot[] { opponentP0 }, new Player.Spot[] { opponentP1 });
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
            Player.Spot player = new Player.Spot(0, 0);
            Player.Spot opponent = new Player.Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Player.Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<Player.ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(Player.ActionsType.DOWN, Player.ActionsType.RIGHT);
        }

        @Test
        @DisplayName("returns up or right if player is at left lower corner")
        void onLeftLowerCorner() {
            Player.Spot player = new Player.Spot(0, 19);
            Player.Spot opponent = new Player.Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Player.Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<Player.ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(Player.ActionsType.UP, Player.ActionsType.RIGHT);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("returns left or down if player is at right upper corner")
        void onRightUpperCorner() {
            Player.Spot player = new Player.Spot(29, 0);
            Player.Spot opponent = new Player.Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Player.Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<Player.ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(Player.ActionsType.DOWN, Player.ActionsType.LEFT);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("returns left or up if player is at right lower corner")
        void onRightLowerCorner() {
            Player.Spot player = new Player.Spot(29, 19);
            Player.Spot opponent = new Player.Spot(5, 10);

            PlayerInputProvider inputProvider = new PlayerInputProvider(2, 0, player, new Player.Spot[] { opponent });
            KnowledgeRepo repo = new KnowledgeRepo(inputProvider::inputStream);
            repo.update();
            List<Player.ActionsType> actions = repo.getPossibleActions();

            assertThat(actions).containsOnly(Player.ActionsType.UP, Player.ActionsType.LEFT);
        }
    }

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