import java.util.List;

/**
 * AIs tries to not kill itself by moving on a snail sequence
 */
public class SnailAI extends Player.AI {

    private KnowledgeRepo repo;

    public SnailAI(KnowledgeRepo knowledgeRepo) {
        super(knowledgeRepo);
        this.repo = knowledgeRepo;
    }

    @Override
    public Player.Action[] play() {
        List<Player.ActionsType> possibleActions = repo.getPossibleActions();

        if (possibleActions.isEmpty()) {
            return new Player.Action[] { new Player.Action(Player.ActionsType.DOWN) };
        }

        Player.Spot startSpot = repo.getPlayerStartingSpot();
        Player.Spot currentSpot = repo.getPlayerCurrentSpot();

        Player.ActionsType bestMovement = possibleActions.remove(0);
        Player.Spot next = currentSpot.next(bestMovement);
        double bestScore = startSpot.squareDistTo(next);
        if (repo.getPossibleActionsFor(next).size() < 2) {
            bestScore = Integer.MAX_VALUE;
        }

        for (Player.ActionsType type : possibleActions) {

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

        return new Player.Action[] { new Player.Action(bestMovement) };
    }
}