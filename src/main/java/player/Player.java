package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

public final class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        InputRepository repo = new InputRepository(in::nextInt);
        AI ai = new LongestSequenceAI(repo);

        while (true) {
            ai.updateRepository();
            Action[] actions = ai.play();
            for (Action action : actions) {
                System.out.println(action.asString());
            }
        }
    }

    public static final class TronGameEngine {

        private static final int MAX_X = 30;
        private static final int MAX_Y = 20;

        private final boolean[] dead;

        private final boolean[][] visitedSpots;
        private final TronLightCycle[] lightCycles;

        public TronGameEngine(Collection<TronLightCycle> lightCycles) {

            this.lightCycles = new TronLightCycle[lightCycles.size()];
            this.visitedSpots = new boolean[MAX_Y][MAX_X];
            for (TronLightCycle lightCycle : lightCycles) {
                this.lightCycles[lightCycle.getPlayerN()] = new TronLightCycle(lightCycle);
                for (Spot visitedSpot : lightCycle.getVisitedSpots()) {
                    this.visitedSpots[visitedSpot.getY()][visitedSpot.getX()] = true;
                }
            }

            this.dead = new boolean[lightCycles.size()];
        }

        public TronGameEngine(TronLightCycle... lightCycles) {
            this(Arrays.asList(lightCycles));
        }

        public void perform(int playerN, Action action) {
            perform(true, playerN, action);
        }

        public void perform(boolean strict, int playerN, Action action) {
            perform(strict, playerN, action.getType());
        }

        public void perform(boolean strict, int playerN, ActionsType action) {

            if (strict && dead[playerN]) {
                return;
            }

            TronLightCycle lightCycle = lightCycles[playerN];

            Spot current = lightCycle.getCurrent();
            Spot next = current.next(action);

            if (next.getX() >= 0 && next.getX() < MAX_X && next.getY() >= 0 && next.getY() < MAX_Y) {
                if (!visitedSpots[next.getY()][next.getX()]) {
                    // valid movement, then perform it
                    lightCycle.moveTo(next);
                    visitedSpots[next.getY()][next.getX()] = true;
                    return;
                }
            }

            // invalid movement

            if (strict) {
                // if on strict mode, player is taken off the grid
                for (Spot spot : lightCycle.getVisitedSpots()) {
                    visitedSpots[spot.getY()][spot.getX()] = false;
                }
            }

            dead[playerN] = true;
        }

        public boolean isDead(int playerN) {
            return dead[playerN];
        }

        public Spot getCurrent(int playerN) {
            return lightCycles[playerN].getCurrent();
        }

        public Spot getStart(int playerN) {
            return lightCycles[playerN].getStart();
        }
    }

    static class LongestSequenceAI extends GeneticAI {

        public LongestSequenceAI(InputRepository repository) {
            super(64, 128, 32, .7, .1, repository, LongestSequenceAI::evaluate);
        }

        private static double evaluate(TronGameEngine engine, int playerN, ActionsType[] actions) {
            double score = 0;

            for (ActionsType action : actions) {
                engine.perform(false, playerN, action);

                if (engine.isDead(playerN)) {
                    break;
                }

                score += 1.0;
            }

            return score / actions.length;
        }

        @Override
        public String toString() {
            return "LongestSequenceAI{}" + super.toString();
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

            ActionsType nextAction = chromosome.genes[0];

            return new Action[] { new Action(nextAction) };
        }

        private Chromosome find(int movements, int popSize, int generations) {

            // Create the pool
            List<Chromosome> pool = new ArrayList<>(popSize);
            List<Chromosome> newPool = new ArrayList<>(popSize);

            // Generate unique chromosomes in the pool
            for (int i = 0; i < popSize; i++) {

                ActionsType[] genes = generateRandomMovements(movements);
                // FIXME: for now, we are only able to simulate player's movements
                Chromosome chromosome = new Chromosome(genes, evaluationFunction, repo.getP(), random);

                chromosome.evaluate(new TronGameEngine(repo.getInGameLightCycles()));

                pool.add(chromosome);
            }

            System.err.println("===");
            // Loop until solution is found
            for (int generation = 0; generation < generations; generation++) {
                // Clear the new pool
                newPool.clear();

                if (eletism) {
                    // FIXME: listIterator instead?
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
                    children[0].evaluate(new TronGameEngine(repo.getInGameLightCycles()));
                    children[1].evaluate(new TronGameEngine(repo.getInGameLightCycles()));

                    // Add to the new pool
                    newPool.add(children[0]);
                    newPool.add(children[1]);
                }

                // Add the newPool back to the old pool
                pool.clear();
                pool.addAll(newPool);

                double totalScore = newPool.stream()
                        .mapToDouble(Chromosome::getScore)
                        .sum();

                System.err.println(generation + "," + totalScore);
            }

            Chromosome best = newPool.stream()
                    .max(Comparator.comparingDouble(Chromosome::getScore))
                    .orElseThrow(() -> new IllegalStateException("Pool should contain at least one chromosome"));

            System.err.println(best.getScore());
            System.err.println("===");
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
        private final int playerN;
        private final Random random;
        private final ActionsType[] genes;
        private double score;

        public Chromosome(
                ActionsType[] genes,
                EvaluationFunction evaluationFunction,
                int playerN,
                Random random) {

            this.genes = genes;
            this.evaluationFunction = evaluationFunction;
            this.playerN = playerN;
            this.random = random;
            this.score = 0.0;
        }

        public void evaluate(TronGameEngine gameEngine) {
            this.score = evaluationFunction.evaluate(gameEngine, playerN, genes);
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
                    new Chromosome(child1, evaluationFunction, playerN, random),
                    new Chromosome(child2, evaluationFunction, playerN, random) };
        }

        public Chromosome mutate() {
            ActionsType[] mutate = new ActionsType[genes.length];
            int pivot = random.nextInt(genes.length);

            System.arraycopy(genes, 0, mutate, 0, pivot);
            System.arraycopy(genes, pivot + 1, mutate, pivot + 1, (genes.length - pivot - 1));
            mutate[pivot] = POSSIBLE_ACTIONS[random.nextInt(POSSIBLE_ACTIONS.length)];

            return new Chromosome(mutate, evaluationFunction, playerN, random);
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
            return "Chromosome{" + "playerN=" + playerN +
                    ", genes=" + Arrays.toString(genes) +
                    ", score=" + score +
                    '}';
        }
    }

    static class InputRepository extends Repository {

        private int N;
        private int P;

        private final List<TronLightCycle> inGameLightCycles;

        protected InputRepository(IntSupplier inputSupplier) {
            super(inputSupplier);
            this.inGameLightCycles = new ArrayList<>(4);
        }

        @Override
        public void update() {
            N = readInput(); // total number of players (2 to 4).
            P = readInput(); // your player number (0 to 3).

            List<TronLightCycle> inGameLightCycles = new ArrayList<>(N);

            for (int i = 0; i < N; i++) {

                int X0 = readInput(); // starting X coordinate of lightcycle (or -1)
                int Y0 = readInput(); // starting Y coordinate of lightcycle (or -1)

                int X1 = readInput(); // starting X coordinate of lightcycle (can be the same as X0 if you
                // play before this player)
                int Y1 = readInput(); // starting Y coordinate of lightcycle (can be the same as Y0 if you
                // play before this player)

                Spot startSpot = new Spot(X0, Y0);
                Spot currentSpot = new Spot(X1, Y1);

                Optional<TronLightCycle> maybeLightCycle =
                        this.inGameLightCycles.stream()
                                .filter(lightCycle -> lightCycle.getStart().equals(startSpot))
                                .findFirst();

                if (maybeLightCycle.isPresent()) {
                    TronLightCycle lightCycle = maybeLightCycle.get();
                    lightCycle.moveTo(currentSpot);
                    inGameLightCycles.add(lightCycle);
                    continue;
                }

                inGameLightCycles.add(new TronLightCycle(i, startSpot));
            }

            this.inGameLightCycles.clear();
            this.inGameLightCycles.addAll(inGameLightCycles);
        }

        public int getN() {
            return N;
        }

        public int getP() {
            return P;
        }

        public List<TronLightCycle> getInGameLightCycles() {
            return Collections.unmodifiableList(inGameLightCycles);
        }

        public TronLightCycle getPlayerLightCycle() {
            return inGameLightCycles.stream()
                    .filter(lightCycle -> lightCycle.getPlayerN() == getP())
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Unable to find player <" + getP() + ">"));
        }

        public List<TronLightCycle> getOpponentLightCycles() {
            return inGameLightCycles.stream()
                    .filter(lightCycle -> lightCycle.getPlayerN() != getP())
                    .collect(Collectors.toList());
        }
    }

    public static final class TronLightCycle {

        private final int playerN;
        private Spot current;
        private final Spot start;
        private final Set<Spot> visitedSpots;

        public TronLightCycle(int playerN, Spot current) {
            this.playerN = playerN;
            this.current = current;
            this.start = current;
            this.visitedSpots = new HashSet<>();
            this.visitedSpots.add(current);
        }

        public TronLightCycle(TronLightCycle another) {
            this.playerN = another.playerN;
            this.current = another.current;
            this.start = another.start;
            this.visitedSpots = new HashSet<>(another.visitedSpots);
        }

        public void moveTo(Spot spot) {
            this.current = spot;
            this.visitedSpots.add(spot);
        }

        public Spot getCurrent() {
            return current;
        }

        public Spot getStart() {
            return start;
        }

        public int getPlayerN() {
            return playerN;
        }

        public Set<Spot> getVisitedSpots() {
            return Collections.unmodifiableSet(visitedSpots);
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
                throw new IllegalStateException("Unknown action type " + type);
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

        private final Map<String, Object> conf;
        private final RepositoryUpdater updater;

        /**
         * Builds an AI with specified configuration.<br>
         * It is recommended to create a default configuration.
         */
        public AI(Map<String, Object> conf, RepositoryUpdater updater) {
            this.conf = Collections.unmodifiableMap(conf);
            this.updater = updater;
        }

        /**
         * Builds an AI with an empty configuration.
         */
        public AI(RepositoryUpdater updater) {
            this(Collections.emptyMap(), updater);
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
        double evaluate(TronGameEngine engine, int playerN, ActionsType[] actions);
    }
}
