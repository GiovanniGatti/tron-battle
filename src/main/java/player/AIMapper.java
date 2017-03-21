package player;

import player.engine.AI;

public class AIMapper extends Player.AI implements AI {

    private final Player.AI ai;

    public AIMapper(Player.AI ai) {
        super(ai::updateRepository);
        this.ai = ai;
    }

    @Override
    public Player.Action[] play() {
        return ai.play();
    }
}
