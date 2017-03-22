public class LongestSequenceAI extends Player.GeneticAI {

    public LongestSequenceAI(Player.InputRepository repository) {
        super(64, 32, 512, .7, .02, repository, LongestSequenceAI::evaluate);
    }

    private static double evaluate(Player.TronSimulator engine, Player.Spot startAt, Player.ActionsType[] actions) {
        double score = 0.0;

        for (Player.ActionsType action : actions) {

            if (!engine.perform(startAt, action)) {
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