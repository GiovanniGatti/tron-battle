import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.function.IntSupplier;

final class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        InputRepository repo = new InputRepository(in::nextInt);
        AI ai = new FloodFillAI(repo);

        while (true) {
            ai.updateRepository();
            Action[] actions = ai.play();
            for (Action action : actions) {
                System.out.println(action.asString());
            }
        }
    }

    /**
     * Simulates the game on a relaxed mode, that is, players are never taken out of the grid when they die.
     */
    public static final class TronSimulator {

        private static final int MAX_X = 30;
        private static final int MAX_Y = 20;

        private final BattleFieldSnapshot snapshot;

        // mutable internal state
        private final boolean[][] grid;
        private final Map<Spot, Spot> currentSpots;
        private int availableSpotsCnt;

        public TronSimulator(BattleFieldSnapshot snapshot) {

            this.snapshot = snapshot;

            this.grid = new boolean[MAX_Y][MAX_X];

            Set<Spot> startSpots = snapshot.getStartSpots();
            this.currentSpots = new HashMap<>(startSpots.size());
            startSpots.forEach(s -> this.currentSpots.put(s, snapshot.getCurrentSpot(s)));

            this.availableSpotsCnt = snapshot.getAvailableSpotsCount();
        }

        /**
         * Simulates the action for a specific player
         *
         * @param startAt the players start position
         * @param action where to go
         * @return true if action has been successful, false if action kills the player.
         */
        public boolean perform(Spot startAt, ActionsType action) {
            Spot currentSpot = currentSpots.get(startAt);

            if (currentSpot == null) {
                throw new IllegalStateException("Unknown player starting at " + startAt);
            }

            Spot next = currentSpot.next(action);

            if (!getGridSize().isWithinGrid(next) || hasBeenVisited(next)) {
                return false;
            }

            currentSpots.put(startAt, next);
            grid[next.getY()][next.getX()] = true;
            availableSpotsCnt--;

            return true;
        }

        public boolean hasBeenVisited(Spot spot) {
            return hasBeenVisited(spot.getX(), spot.getY());
        }

        public boolean hasBeenVisited(int x, int y) {
            return grid[y][x] || snapshot.hasBeenVisited(x, y);
        }

        public Spot getCurrentSpot(Spot startSpot) {
            if (!currentSpots.containsKey(startSpot)) {
                throw new IllegalStateException("Unknown player starting at " + startSpot);
            }

            return currentSpots.get(startSpot);
        }

        public Set<Spot> getStartSpots() {
            return currentSpots.keySet();
        }

        public GridSize getGridSize() {
            return snapshot.getGridSize();
        }

        public int getAvailableSpotsCount() {
            return availableSpotsCnt;
        }
    }

    static class FloodFillAI extends GeneticAI {

        public FloodFillAI(InputRepository repository) {
            super(64, 32, 256, .7, .02, repository, FloodFillAI::evaluate);
        }

        private static double evaluate(TronSimulator engine, Spot startAt, ActionsType[] actions) {
            double weight = 1.0;

            for (ActionsType action : actions) {
                if (!engine.perform(startAt, action)) {
                    weight = weight - (1.0 / actions.length);
                }
            }

            int targetableArea = floodFillArea(
                    spot -> !engine.hasBeenVisited(spot),
                    engine.getGridSize(),
                    engine.getCurrentSpot(startAt));

            return ((double) targetableArea / engine.getAvailableSpotsCount()) * weight;
        }

        /**
         * Computes the available area within a grid from a given target point
         *
         * @param availableSpot a function that computes whether a spot is or is not available to be moved in
         * @param gridSize the grid size evaluator
         * @param target a target spot to start the algorithm. It might be an occupied spot.
         */
        static int floodFillArea(AvailableSpot availableSpot, GridSize gridSize, Spot target) {
            boolean[][] colored = new boolean[gridSize.getMaxY()][gridSize.getMaxX()];
            colored[target.getY()][target.getX()] = true;

            int size = 0;

            Stack<Spot> evaluationStack = new Stack<>();
            evaluationStack.push(target);

            while (!evaluationStack.isEmpty()) {
                size++;

                Spot last = evaluationStack.pop();
                Spot up = last.next(ActionsType.UP);
                Spot down = last.next(ActionsType.DOWN);
                Spot left = last.next(ActionsType.LEFT);
                Spot right = last.next(ActionsType.RIGHT);

                if (gridSize.isWithinGrid(up) &&
                        availableSpot.evaluate(up) &&
                        !colored[up.getY()][up.getX()]) {

                    evaluationStack.push(up);
                    colored[up.getY()][up.getX()] = true;
                }

                if (gridSize.isWithinGrid(down) &&
                        availableSpot.evaluate(down) &&
                        !colored[down.getY()][down.getX()]) {

                    evaluationStack.push(down);
                    colored[down.getY()][down.getX()] = true;
                }

                if (gridSize.isWithinGrid(left) &&
                        availableSpot.evaluate(left) &&
                        !colored[left.getY()][left.getX()]) {
                    evaluationStack.push(left);
                    colored[left.getY()][left.getX()] = true;
                }

                if (gridSize.isWithinGrid(right) &&
                        availableSpot.evaluate(right) &&
                        !colored[right.getY()][right.getX()]) {
                    evaluationStack.push(right);
                    colored[right.getY()][right.getX()] = true;
                }
            }

            // In order to optimize field evaluation, we consider the first spot as a free one and we decrement the
            // final result by 1 if it was not
            return size - (availableSpot.evaluate(target) ? 0 : 1);
        }

        @Override
        public String toString() {
            return "FloodFillAI{}" + super.toString();
        }
    }

    static class FibonacciLongestSequenceAI extends GeneticAI {

        private static final long MAX_VALUE = fibonacci(64);

        public FibonacciLongestSequenceAI(InputRepository repository) {
            super(64, 32, 256, .7, .02, repository, FibonacciLongestSequenceAI::evaluate);
        }

        private static long fibonacci(int size) {

            long previous = 0L;
            long next = 1L;

            for (int i = 0; i < size; i++) {
                long tmp = next;
                next += previous;
                previous = tmp;
            }

            return next;
        }

        private static double evaluate(TronSimulator engine, Spot startAt, ActionsType[] actions) {
            double score = 0.0;

            long weight = 1L;
            long previousWeight = 0L;

            for (ActionsType action : actions) {

                if (engine.perform(startAt, action)) {
                    long tmp = weight;
                    weight += previousWeight;
                    previousWeight = tmp;
                } else {
                    weight = 1L;
                    previousWeight = 0L;
                }

                score += weight;
            }

            return score / MAX_VALUE;
        }

        @Override
        public String toString() {
            return "FibonacciLongestSequenceAI{}" + super.toString();
        }
    }

    static class RelaxedLongestSequenceAI extends GeneticAI {

        public RelaxedLongestSequenceAI(InputRepository repository) {
            super(64, 32, 256, .7, .02, repository, RelaxedLongestSequenceAI::evaluate);
        }

        private static double evaluate(TronSimulator engine, Spot startAt, ActionsType[] actions) {
            double score = 0.0;

            for (ActionsType action : actions) {
                if (engine.perform(startAt, action)) {
                    score += 1.0;
                }
            }

            return score / actions.length;
        }

        @Override
        public String toString() {
            return "RelaxedLongestSequenceAI{}" + super.toString();
        }
    }

    public static class GeneticAI extends AI {

        private static final ActionsType[] POSSIBLE_ACTIONS = ActionsType.values();
        private static final int TOURNAMENT_SIZE = 2;

        private final boolean eletism;
        private final Random random;
        private final InputRepository repo;
        private final int geneLength;
        private final int popSize;
        private final int generations;
        private final double crossoverRate;
        private final double mutationRate;
        private final EvaluationFunction evaluationFunction;

        public GeneticAI(
                boolean eletism,
                int geneLength,
                int popSize,
                int generations,
                double crossoverRate,
                double mutationRate,
                InputRepository repo,
                EvaluationFunction evaluationFunction) {

            super(repo);
            this.eletism = eletism;
            this.geneLength = geneLength;
            this.popSize = popSize;
            this.generations = generations;
            this.crossoverRate = crossoverRate;
            this.mutationRate = mutationRate;
            this.evaluationFunction = evaluationFunction;
            this.random = new Random();
            this.repo = repo;
        }

        public GeneticAI(
                int geneLength,
                int popSize,
                int generations,
                double crossoverRate,
                double mutationRate,
                InputRepository repo,
                EvaluationFunction evaluationFunction) {

            this(true, geneLength, popSize, generations, crossoverRate, mutationRate, repo, evaluationFunction);
        }

        @Override
        public Action[] play() {
            // long currentTimeMillis = System.currentTimeMillis();
            Chromosome chromosome = find(geneLength, popSize, generations);
            // System.err.println(System.currentTimeMillis() - currentTimeMillis);

            BattleFieldSnapshot battleField = repo.getBattleField();

            Spot startSpot = repo.getPlayerLightCycleStartSpot();
            Spot currentSpot = battleField.getCurrentSpot(startSpot);

            Optional<ActionsType> maybeNextValidMove = Arrays.stream(chromosome.genes)
                    .filter(actionsType -> {
                        //FIXME: it shouldn't be able to walk through himself
                        Spot next = currentSpot.next(actionsType);
                        return battleField.getGridSize().isWithinGrid(next) && !battleField.hasBeenVisited(next);
                    }).findFirst();

            ActionsType nextAction = maybeNextValidMove.orElse(ActionsType.RIGHT);

            return new Action[] { new Action(nextAction) };
        }

        private Chromosome find(int movements, int popSize, int generations) {

            // Create the pool
            List<Chromosome> pool = new ArrayList<>(popSize);
            List<Chromosome> newPool = new ArrayList<>(popSize);

            BattleFieldSnapshot battleFieldSnapshot = repo.getBattleField();

            // Generate unique chromosomes in the pool
            for (int i = 0; i < popSize; i++) {

                ActionsType[] genes = generateRandomMovements(movements);
                // FIXME: for now, we are only able to simulate player's movements
                Chromosome chromosome =
                        new Chromosome(genes, evaluationFunction, repo.getPlayerLightCycleStartSpot(), random);

                chromosome.evaluate(new TronSimulator(battleFieldSnapshot));

                pool.add(chromosome);
            }

            // System.err.println("===");
            // Loop until solution is found
            for (int generation = 0; generation < generations; generation++) {
                // Clear the new pool
                newPool.clear();

                if (eletism) {
                    int bestIndex = 0;
                    Chromosome best = pool.get(0);
                    for (int i = 1; i < pool.size(); i++) {
                        Chromosome chromosome = pool.get(i);
                        if (chromosome.getScore() > best.getScore()) {
                            bestIndex = i;
                            best = chromosome;
                        }
                    }

                    pool.remove(bestIndex);
                    newPool.add(best);
                }

                // Loop until the pool has been processed
                for (int x = pool.size() - 1; x >= 0; x -= 2) {
                    // Select two members
                    // Chromosome[] parents = selectParents(pool);

                    Chromosome[] parents = new Chromosome[2];
                    parents[0] = tournamentSelection(pool);
                    parents[1] = tournamentSelection(pool);

                    // Cross over and mutate
                    Chromosome[] children;
                    if (random.nextFloat() <= crossoverRate) {
                        children = parents[0].crossOver(parents[1]);
                    } else {
                        children = parents;
                    }

                    if (random.nextFloat() <= mutationRate) {
                        children[0] = children[0].mutate();
                    }

                    if (random.nextFloat() <= mutationRate) {
                        children[1] = children[1].mutate();
                    }

                    // evaluate new nodes
                    children[0].evaluate(new TronSimulator(battleFieldSnapshot));
                    children[1].evaluate(new TronSimulator(battleFieldSnapshot));

                    // Add to the new pool
                    newPool.add(children[0]);
                    newPool.add(children[1]);
                }

                // Add the newPool back to the old pool
                pool.clear();
                pool.addAll(newPool);

                // double totalScore = newPool.stream()
                // .mapToDouble(Chromosome::getScore)
                // .sum();
                //
                // System.err.println(generation + "," + totalScore);
            }

            Chromosome best = newPool.stream()
                    .max(Comparator.comparingDouble(Chromosome::getScore))
                    .orElseThrow(() -> new IllegalStateException("Pool should contain at least one chromosome"));

            // System.err.println(best.getScore());
            // System.err.println("===");
            return best;
        }

        // FIXME: generic selection function
        Chromosome rouletteSelect(List<Chromosome> population) {
            // calculate the total weight
            double weight_sum = population.stream()
                    .mapToDouble(Chromosome::getScore)
                    .sum();

            // get a random value
            double value = random.nextDouble() * weight_sum;

            // locate the random value based on the weights
            for (int i = 0; i < population.size(); i++) {
                Chromosome individual = population.get(i);
                value -= individual.getScore();
                if (value <= 0) {
                    // FIXME: strongest have preference
                    return individual;
                }
            }

            // when rounding errors occur, we return the last item's index
            return population.get(population.size() - 1);
        }

        private Chromosome tournamentSelection(List<Chromosome> population) {

            Chromosome best = population.get(random.nextInt(population.size()));
            for (int j = 1; j < TOURNAMENT_SIZE; j++) {
                Chromosome candidate = population.get(random.nextInt(population.size()));
                if (candidate.getScore() > best.getScore()) {
                    best = candidate;
                }
            }

            return best;
        }

        private ActionsType[] generateRandomMovements(int movements) {

            ActionsType[] actions = new ActionsType[movements];

            for (int i = 0; i < movements; i++) {
                actions[i] = POSSIBLE_ACTIONS[random.nextInt(POSSIBLE_ACTIONS.length)];
            }

            return actions;
        }

        @Override
        public String toString() {
            return "GeneticAI{" +
                    "geneLength=" + geneLength +
                    ", popSize=" + popSize +
                    ", generations=" + generations +
                    ", crossoverRate=" + crossoverRate +
                    ", mutationRate=" + mutationRate +
                    "} ";
        }
    }

    private static class Chromosome implements Comparable<Chromosome> {

        private static final ActionsType[] POSSIBLE_ACTIONS = ActionsType.values();

        private final EvaluationFunction evaluationFunction;
        private final Spot startAt;
        private final Random random;
        private final ActionsType[] genes;
        private double score;

        public Chromosome(
                ActionsType[] genes,
                EvaluationFunction evaluationFunction,
                Spot startAt,
                Random random) {

            this.genes = genes;
            this.evaluationFunction = evaluationFunction;
            this.startAt = startAt;
            this.random = random;
            this.score = 0.0;
        }

        public void evaluate(TronSimulator gameEngine) {
            this.score = evaluationFunction.evaluate(gameEngine, startAt, genes);
        }

        public Chromosome[] crossOver(Chromosome another) {

            int pivot = random.nextInt(another.genes.length);

            ActionsType[] child1 = new ActionsType[another.genes.length];
            ActionsType[] child2 = new ActionsType[genes.length];

            System.arraycopy(genes, 0, child1, 0, pivot);
            System.arraycopy(another.genes, pivot, child1, pivot, (another.genes.length - pivot));

            System.arraycopy(another.genes, 0, child2, 0, pivot);
            System.arraycopy(genes, pivot, child2, pivot, (genes.length - pivot));

            return new Chromosome[] {
                    new Chromosome(child1, evaluationFunction, startAt, random),
                    new Chromosome(child2, evaluationFunction, startAt, random) };
        }

        public Chromosome mutate() {
            ActionsType[] mutate = new ActionsType[genes.length];
            int pivot = random.nextInt(genes.length);

            System.arraycopy(genes, 0, mutate, 0, pivot);
            System.arraycopy(genes, pivot + 1, mutate, pivot + 1, (genes.length - pivot - 1));
            mutate[pivot] = POSSIBLE_ACTIONS[random.nextInt(POSSIBLE_ACTIONS.length)];

            return new Chromosome(mutate, evaluationFunction, startAt, random);
        }

        public double getScore() {
            return score;
        }

        @Override
        public int compareTo(Chromosome c) {
            if (score < c.score) {
                return -1;
            } else if (score > c.score) {
                return 1;
            }

            return 0;
        }

        @Override
        public String toString() {
            return "Chromosome{" + "startAt=" + startAt +
                    ", genes=" + Arrays.toString(genes) +
                    ", score=" + score +
                    '}';
        }
    }

    static class InputRepository extends Repository {

        private int N;
        private int P;

        private final BattleField battleField;
        private final Set<Spot> opponenetsStartSpots;
        private Spot playerStartSpot;

        protected InputRepository(IntSupplier inputSupplier) {
            super(inputSupplier);
            this.battleField = new BattleField();
            opponenetsStartSpots = new HashSet<>();
        }

        @Override
        public void update() {
            N = readInput(); // total number of players (2 to 4).
            P = readInput(); // your player number (0 to 3).

            Set<Spot> lightCyclesAlive = new HashSet<>(N);

            for (int i = 0; i < N; i++) {

                int X0 = readInput(); // starting X coordinate of lightcycle (or -1)
                int Y0 = readInput(); // starting Y coordinate of lightcycle (or -1)

                int X1 = readInput(); // starting X coordinate of lightcycle (can be the same as X0 if you
                // play before this player)
                int Y1 = readInput(); // starting Y coordinate of lightcycle (can be the same as Y0 if you
                // play before this player)

                Spot startSpot = new Spot(X0, Y0);
                Spot currentSpot = new Spot(X1, Y1);

                if (battleField.hasLightCycleStartingAt(startSpot)) {
                    battleField.moveTo(startSpot, currentSpot);
                } else {

                    if (i != P) {
                        opponenetsStartSpots.add(startSpot);
                    } else {
                        playerStartSpot = startSpot;
                    }

                    battleField.addLightCycleAt(startSpot, currentSpot);
                }

                lightCyclesAlive.add(startSpot);
            }

            Set<Spot> killedLightCycles = new HashSet<>(battleField.getLightCyclesStartingSpots());
            killedLightCycles.removeAll(lightCyclesAlive);

            battleField.killLightCycles(killedLightCycles);
        }

        public int getN() {
            return N;
        }

        public int getP() {
            return P;
        }

        public BattleFieldSnapshot getBattleField() {
            return battleField.getSnapshot();
        }

        public Spot getPlayerLightCycleStartSpot() {
            return playerStartSpot;
        }

        public Set<Spot> getOpponentLightCyclesStartSpot() {
            return Collections.unmodifiableSet(opponenetsStartSpots);
        }
    }

    public static final class BattleField {

        private static final int MAX_X = 30;
        private static final int MAX_Y = 20;

        private static final GridSize GRID_SIZE = new GridSize(MAX_X, MAX_Y);

        private final Map<Spot, Spot> currentSpot;
        private final Map<Spot, Set<Spot>> visitedSpots;
        private final boolean[][] grid;
        private int availableSpotsCnt;

        public BattleField() {
            this.grid = new boolean[MAX_Y][MAX_X];
            this.currentSpot = new HashMap<>();
            this.visitedSpots = new HashMap<>();
            this.availableSpotsCnt = MAX_X * MAX_Y;
        }

        public BattleField(BattleField another) {
            this.availableSpotsCnt = another.availableSpotsCnt;
            this.currentSpot = new HashMap<>(another.currentSpot);
            this.visitedSpots = new HashMap<>(another.visitedSpots.size());

            another.visitedSpots.forEach((key, value) -> this.visitedSpots.put(key, new HashSet<>(value)));

            this.grid = new boolean[another.grid.length][another.grid[0].length];

            int i = 0;
            for (boolean[] row : another.grid) {
                System.arraycopy(row, 0, grid[i++], 0, row.length);
            }
        }

        public Set<Spot> getLightCyclesStartingSpots() {
            return currentSpot.keySet();
        }

        public boolean hasLightCycleStartingAt(Spot startSpot) {
            return currentSpot.containsKey(startSpot);
        }

        public void addLightCycleAt(Spot startSpot, Spot currentSpot) {

            if (!startSpot.equals(currentSpot) && !startSpot.isNeighborOf(currentSpot)) {
                throw new IllegalStateException(
                        "Cannot add non-neighbors spots start=" + startSpot +
                                ", current=" + currentSpot);
            }

            this.currentSpot.put(startSpot, currentSpot);

            Set<Spot> visitedSpots = new HashSet<>(256);
            visitedSpots.add(startSpot);
            visitedSpots.add(currentSpot);

            this.visitedSpots.put(startSpot, visitedSpots);

            grid[startSpot.getY()][startSpot.getX()] = true;
            availableSpotsCnt--;
            if (!startSpot.equals(currentSpot)) {
                grid[currentSpot.getY()][currentSpot.getX()] = true;
                availableSpotsCnt--;
            }
        }

        public void moveTo(Spot startSpot, Spot currentSpot) {
            Spot past = this.currentSpot.get(startSpot);

            if (past == null || !past.isNeighborOf(currentSpot)) {
                throw new IllegalStateException("Cannot move from " + past + " to " + currentSpot);
            }

            this.currentSpot.put(startSpot, currentSpot);
            visitedSpots.get(startSpot).add(currentSpot);

            grid[currentSpot.getY()][currentSpot.getX()] = true;
            availableSpotsCnt--;
        }

        public Spot getCurrentSpot(Spot startSpot) {
            return currentSpot.get(startSpot);
        }

        public boolean hasBeenVisited(int x, int y) {
            return grid[y][x];
        }

        public GridSize getGridSize() {
            return GRID_SIZE;
        }

        public boolean hasBeenVisited(Spot spot) {
            return hasBeenVisited(spot.getX(), spot.getY());
        }

        public Set<Spot> getStartSpots() {
            return currentSpot.keySet();
        }

        public void killLightCycles(Set<Spot> startSpots) {

            for (Spot startSpot : startSpots) {
                currentSpot.remove(startSpot);

                Set<Spot> spots = visitedSpots.remove(startSpot);
                for (Spot spot : spots) {
                    grid[spot.getY()][spot.getX()] = false;
                    availableSpotsCnt++;
                }
            }
        }

        public int getAvailableSpotsCount() {
            return availableSpotsCnt;
        }

        public BattleFieldSnapshot getSnapshot() {
            return new BattleFieldSnapshot(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BattleField that = (BattleField) o;
            return Arrays.deepEquals(grid, that.grid) &&
                    Objects.equals(currentSpot, that.currentSpot) &&
                    Objects.equals(visitedSpots, that.visitedSpots);
        }

        @Override
        public int hashCode() {
            return Objects.hash(grid, currentSpot, visitedSpots);
        }
    }

    public static final class BattleFieldSnapshot {

        private final BattleField battleField;

        public BattleFieldSnapshot(BattleField battleField) {

            // In order to optimize, we won't be copying the arena's state,
            // and therefore after each round the snapshot is deprecated
            this.battleField = battleField;
        }

        public Set<Spot> getStartSpots() {
            return battleField.getStartSpots();
        }

        public Spot getCurrentSpot(Spot startSpot) {
            return battleField.getCurrentSpot(startSpot);
        }

        public boolean hasBeenVisited(int x, int y) {
            return battleField.hasBeenVisited(x, y);
        }

        public GridSize getGridSize() {
            return battleField.getGridSize();
        }

        public int getAvailableSpotsCount() {
            return battleField.getAvailableSpotsCount();
        }

        public boolean hasBeenVisited(Spot spot) {
            return battleField.hasBeenVisited(spot);
        }
    }

    public static final class Spot {
        private final int x;
        private final int y;

        public Spot(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isNeighborOf(Spot spot) {
            return (x + 1 == spot.x && y == spot.y) ||
                    (x - 1 == spot.x && y == spot.y) ||
                    (x == spot.x && y - 1 == spot.y) ||
                    (x == spot.x && y + 1 == spot.y);
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
                throw new IllegalStateException("Unknown action type " + type);
            }
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

        public Action(ActionsType type) {
            this.type = type;
        }

        public String asString() {
            return type.name();
        }

        public ActionsType getType() {
            return type;
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

        private final RepositoryUpdater updater;

        public AI(RepositoryUpdater updater) {
            this.updater = updater;
        }

        /**
         * Implements the IA algorithm
         *
         * @return the best ordered set of actions found
         */
        public abstract Action[] play();

        public void updateRepository() {
            updater.update();
        }

        @Override
        public final boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass());
        }

        @Override
        public final int hashCode() {
            return Objects.hash(getClass());
        }
    }

    public static abstract class Repository implements RepositoryUpdater {

        private final IntSupplier inputSupplier;

        protected Repository(IntSupplier inputSupplier) {
            this.inputSupplier = inputSupplier;
        }

        /**
         * Reads and parse input stream.
         */
        public abstract void update();

        protected int readInput() {
            return inputSupplier.getAsInt();
        }
    }

    public interface RepositoryUpdater {
        void update();
    }

    public interface EvaluationFunction {
        double evaluate(TronSimulator engine, Spot startAt, ActionsType[] actions);
    }

    public static class GridSize {
        private final int maxX;
        private final int maxY;

        public GridSize(int maxX, int maxY) {
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMaxY() {
            return maxY;
        }

        public boolean isWithinGrid(int x, int y) {
            return x >= 0 && x < maxX && y >= 0 && y < maxY;
        }

        public boolean isWithinGrid(Spot spot) {
            return isWithinGrid(spot.getX(), spot.getY());
        }
    }

    public interface AvailableSpot {
        boolean evaluate(Spot spot);
    }
}
