package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.function.IntSupplier;

public final class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        KnowledgeRepo repo = new KnowledgeRepo(in::nextInt);
        AI ai = new RandomAI(repo);

        while (true) {
            repo.readInput();
            Action[] actions = ai.play();
            for (Action action : actions) {
                System.out.println(action.asString());
            }
        }
    }

    /**
     * Dumbest AI possible: it does random movements considering all available possibilities
     */
    static class RandomAI extends AI {

        public RandomAI(KnowledgeRepo knowledgeRepo) {
            super(knowledgeRepo);
        }

        @Override
        public Action[] play() {

            Random random = new Random();

            List<ActionsType> possibleActions = repo.getPossibleActions();

            if (possibleActions.size() == 0) {
                return new Action[] { new Action(ActionsType.UP) };
            }

            int i = random.nextInt(possibleActions.size());

            return new Action[] { new Action(possibleActions.get(i)) };
        }
    }

    static class KnowledgeRepo {

        static final int GRID_X = 30;
        static final int GRID_Y = 20;

        private final IntSupplier inputSupplier;

        private final List<Point> playerPoints;
        private final List<Point> opponentPoints;

        private int N;
        private int P;

        KnowledgeRepo(IntSupplier inputSupplier) {
            this.inputSupplier = inputSupplier;
            this.playerPoints = new ArrayList<>();
            this.opponentPoints = new ArrayList<>();
        }

        void readInput() {
            N = inputSupplier.getAsInt(); // total number of players (2 to 4).
            P = inputSupplier.getAsInt(); // your player number (0 to 3).

            for (int i = 0; i < N; i++) {

                int X0 = inputSupplier.getAsInt(); // starting X coordinate of lightcycle (or -1)
                int Y0 = inputSupplier.getAsInt(); // starting Y coordinate of lightcycle (or -1)

                int X1 = inputSupplier.getAsInt(); // starting X coordinate of lightcycle (can be the same as X0 if you
                                                   // play before this player)
                int Y1 = inputSupplier.getAsInt(); // starting Y coordinate of lightcycle (can be the same as Y0 if you
                                                   // play before this player)

                Point startPoint = new Point(X0, Y0);
                Point currentPoint = new Point(X1, Y1);

                if ((P == 0) == (i == 0)) {
                    if (playerPoints.isEmpty() && !currentPoint.equals(startPoint)) {
                        playerPoints.add(startPoint);
                    }
                    playerPoints.add(currentPoint);
                } else {
                    if (opponentPoints.isEmpty() && !currentPoint.equals(startPoint)) {
                        opponentPoints.add(startPoint);
                    }
                    opponentPoints.add(currentPoint);
                }
            }
        }

        public int getN() {
            return N;
        }

        public int getP() {
            return P;
        }

        public List<Point> getPlayerPoints() {
            return Collections.unmodifiableList(playerPoints);
        }

        public List<Point> getOpponentPoints() {
            return Collections.unmodifiableList(opponentPoints);
        }

        public List<ActionsType> getPossibleActions() {
            List<ActionsType> possibleActions = new ArrayList<>();
            possibleActions.addAll(Arrays.asList(ActionsType.values()));

            Point player = playerPoints.get(playerPoints.size() - 1);

            if (player.x - 1 < 0
                    || playerPoints.contains(new Point(player.x - 1, player.y))
                    || opponentPoints.contains(new Point(player.x - 1, player.y))) {
                possibleActions.remove(ActionsType.LEFT);
            }

            if (player.x + 1 > (GRID_X - 1)
                    || playerPoints.contains(new Point(player.x + 1, player.y))
                    || opponentPoints.contains(new Point(player.x + 1, player.y))) {
                possibleActions.remove(ActionsType.RIGHT);
            }

            if (player.y - 1 < 0
                    || playerPoints.contains(new Point(player.x, player.y - 1))
                    || opponentPoints.contains(new Point(player.x, player.y - 1))) {
                possibleActions.remove(ActionsType.UP);
            }

            if (player.y + 1 > (GRID_Y - 1)
                    || playerPoints.contains(new Point(player.x, player.y + 1))
                    || opponentPoints.contains(new Point(player.x, player.y + 1))) {
                possibleActions.remove(ActionsType.DOWN);
            }

            return possibleActions;
        }

        static class Point {
            private final int x;
            private final int y;

            Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Point point = (Point) o;
                return x == point.x &&
                        y == point.y;
            }

            @Override
            public int hashCode() {
                return Objects.hash(x, y);
            }

            @Override
            public String toString() {
                return "(" + x + ", " + y + ")";
            }
        }
    }

    public enum ActionsType {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Represents an action that can be taken
     */
    public static class Action {

        private final ActionsType type;

        private Action(ActionsType type) {
            this.type = type;
        }

        public String asString() {
            return type.name();
        }
    }

    public static abstract class AI {

        private final Map<String, Object> conf;
        protected final KnowledgeRepo repo;

        /**
         * Builds an AI with specified configuration.<br>
         * It is recommended to create a default configuration.
         */
        public AI(Map<String, Object> conf, KnowledgeRepo knowledgeRepo) {
            this.conf = Collections.unmodifiableMap(conf);
            this.repo = knowledgeRepo;
        }

        /**
         * Builds an AI with an empty configuration.
         */
        public AI(KnowledgeRepo knowledgeRepo) {
            this(Collections.emptyMap(), knowledgeRepo);
        }

        /**
         * Implements the IA algorithm
         *
         * @return the best ordered set of actions found
         */
        public abstract Action[] play();

        public Map<String, Object> getConf() {
            return conf;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AI ai = (AI) o;
            return Objects.equals(conf, ai.conf);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(conf, getClass());
        }
    }
}
