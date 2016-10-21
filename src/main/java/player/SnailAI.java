package player;

import java.util.List;

import player.Player.Action;
import player.Player.ActionsType;
import player.Player.Spot;

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