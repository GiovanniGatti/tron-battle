package player;

import static player.Player.Action;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import player.engine.AI;

public class MultipleRoundMockedAI extends Player.AI implements AI {

    private final Iterator<Player.AI> rounds;

    public MultipleRoundMockedAI(MockedAI.Builder... rounds) {
        super(() -> {});

        this.rounds = Arrays.stream(rounds)
                .map(MockedAI.Builder::build)
                .collect(Collectors.toList())
                .iterator();
    }

    @Override
    public Action[] play() {
        return rounds.next().play();
    }
}
