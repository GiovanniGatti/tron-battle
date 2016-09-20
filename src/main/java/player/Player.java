package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.IntSupplier;

public final class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        KnowledgeRepo repo = new KnowledgeRepo(in::nextInt);
        AI ai = new SnailAI(repo);

        while (true) {
            repo.readInput();
            Action[] actions = ai.play();
            for (Action action : actions) {
                System.out.println(action.asString());
            }
        }
    }

    /**
     * AIs tries to not kill itself by moving on a snail sequence
     */
    static class SnailAI extends AI {

        public SnailAI(KnowledgeRepo knowledgeRepo) {
            super(knowledgeRepo);
        }

        @Override
        public Action[] play() {
            List<ActionsType> possibleActions = repo.getPossibleActions();

            if (possibleActions.isEmpty()) {
                return new Action[] { new Action(ActionsType.DOWN) };
            }

            Spot startSpot = repo.getPlayerStartingSpot();
            Spot currentSpot = repo.getPlayerCurrentSpot();

            ActionsType bestMovement = possibleActions.remove(0);
            Spot next = currentSpot.next(bestMovement);
            double bestScore = startSpot.squareDistTo(next);
            if (repo.getPossibleActionsFor(next).size() < 2) {
                bestScore = Integer.MAX_VALUE;
            }

            for (ActionsType type : possibleActions) {

                next = currentSpot.next(type);
                double score = startSpot.squareDistTo(next);
                if (repo.getPossibleActionsFor(next).size() < 2) {
                    score = Integer.MAX_VALUE;
                }

                if (score < bestScore) {
                    bestScore = score;
                    bestMovement = type;
                }
            }

            return new Action[] { new Action(bestMovement) };
        }
    }

    static class KnowledgeRepo {

        static final int GRID_X = 30;
        static final int GRID_Y = 20;

        private final IntSupplier inputSupplier;

        private final List<Spot> playerSpots;
        private final List<Spot> opponentSpots;

        private int N;
        private int P;

        private Spot playerStartingSpot;
        private Spot playerCurrentSpot;

        KnowledgeRepo(IntSupplier inputSupplier) {
            this.inputSupplier = inputSupplier;
            this.playerSpots = new ArrayList<>();
            this.opponentSpots = new ArrayList<>();
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

                Spot startSpot = new Spot(X0, Y0);
                Spot currentSpot = new Spot(X1, Y1);

                if ((P == 0) == (i == 0)) {
                    if (playerSpots.isEmpty() && !currentSpot.equals(startSpot)) {
                        playerSpots.add(startSpot);
                    }
                    playerSpots.add(currentSpot);

                    playerStartingSpot = startSpot;
                    playerCurrentSpot = currentSpot;
                } else {
                    if (opponentSpots.isEmpty() && !currentSpot.equals(startSpot)) {
                        opponentSpots.add(startSpot);
                    }
                    opponentSpots.add(currentSpot);
                }
            }
        }

        public int getN() {
            return N;
        }

        public int getP() {
            return P;
        }

        public List<Spot> getPlayerSpots() {
            return Collections.unmodifiableList(playerSpots);
        }

        public List<Spot> getOpponentSpots() {
            return Collections.unmodifiableList(opponentSpots);
        }

        public List<ActionsType> getPossibleActions() {
            return getPossibleActionsFor(playerCurrentSpot);
        }

        public List<ActionsType> getPossibleActionsFor(Spot spot) {
            List<ActionsType> possibleActions = new ArrayList<>();
            possibleActions.addAll(Arrays.asList(ActionsType.values()));

            if (spot.x - 1 < 0
                    || playerSpots.contains(new Spot(spot.x - 1, spot.y))
                    || opponentSpots.contains(new Spot(spot.x - 1, spot.y))) {
                possibleActions.remove(ActionsType.LEFT);
            }

            if (spot.x + 1 > (GRID_X - 1)
                    || playerSpots.contains(new Spot(spot.x + 1, spot.y))
                    || opponentSpots.contains(new Spot(spot.x + 1, spot.y))) {
                possibleActions.remove(ActionsType.RIGHT);
            }

            if (spot.y - 1 < 0
                    || playerSpots.contains(new Spot(spot.x, spot.y - 1))
                    || opponentSpots.contains(new Spot(spot.x, spot.y - 1))) {
                possibleActions.remove(ActionsType.UP);
            }

            if (spot.y + 1 > (GRID_Y - 1)
                    || playerSpots.contains(new Spot(spot.x, spot.y + 1))
                    || opponentSpots.contains(new Spot(spot.x, spot.y + 1))) {
                possibleActions.remove(ActionsType.DOWN);
            }

            return possibleActions;
        }

        public Spot getPlayerStartingSpot() {
            return playerStartingSpot;
        }

        public Spot getPlayerCurrentSpot() {
            return playerCurrentSpot;
        }
    }

    static class Spot {
        private final int x;
        private final int y;

        Spot(int x, int y) {
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
            Spot spot = (Spot) o;
            return x == spot.x &&
                    y == spot.y;
        }

        public double squareDistTo(Spot another) {
            return (x - another.x) * (x - another.x) + (y - another.y) * (y - another.y);
        }

        public Spot next(ActionsType type) {
            switch (type) {
            case UP:
                return new Spot(x, y - 1);
            case DOWN:
                return new Spot(x, y + 1);
            case LEFT:
                return new Spot(x - 1, y);
            case RIGHT:
                return new Spot(x + 1, y);
            default:
                throw new IllegalStateException("Unkown action type " + type);
            }
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

    public enum ActionsType {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Represents an action that can be taken
     */
    public static class Action {

        private final ActionsType type;

        Action(ActionsType type) {
            this.type = type;
        }

        public String asString() {
            return type.name();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Action action = (Action) o;

            return type == action.type;

        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type);
        }

        @Override
        public String toString() {
            return asString();
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
