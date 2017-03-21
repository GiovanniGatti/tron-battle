import java.util.List;
import java.util.Random;

/**
 * Dumbest AI possible: it does random movements considering all available possibilities
 */
class RandomAI extends Player.AI {

    private final KnowledgeRepo repo;

    public RandomAI(KnowledgeRepo knowledgeRepo) {
        super(knowledgeRepo::update);
        this.repo = knowledgeRepo;
    }

    @Override
    public Player.Action[] play() {

        Random random = new Random();

        List<Player.ActionsType> possibleActions = repo.getPossibleActions();

        if (possibleActions.size() == 0) {
            return new Player.Action[] { new Player.Action(Player.ActionsType.UP) };
        }

        int i = random.nextInt(possibleActions.size());

        return new Player.Action[] { new Player.Action(possibleActions.get(i)) };
    }
}
