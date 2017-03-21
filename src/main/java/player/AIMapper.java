package player;

import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ai", ai)
                .toString();
    }
}
