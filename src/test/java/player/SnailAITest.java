package player;

import static org.mockito.Mockito.when;
import static player.Player.ActionsType.DOWN;
import static player.Player.ActionsType.LEFT;
import static player.Player.ActionsType.RIGHT;
import static player.Player.ActionsType.UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import player.Player.Action;
import player.Player.KnowledgeRepo;
import player.Player.Spot;
import player.Player.SnailAI;

@DisplayName("The snail AI")
public class SnailAITest implements WithAssertions {

    @Test
    @DisplayName("keep going straight ahead when no need to turn")
    void goStraightAhead() {
        KnowledgeRepo repo = Mockito.mock(KnowledgeRepo.class);

        when(repo.getN()).thenReturn(2);
        when(repo.getP()).thenReturn(1);
        when(repo.getPlayerStartingSpot()).thenReturn(new Spot(3, 3));
        when(repo.getPlayerCurrentSpot()).thenReturn(new Spot(4, 3));

        when(repo.getPossibleActions()).thenReturn(new ArrayList<>(Arrays.asList(UP, RIGHT)));
        when(repo.getPossibleActionsFor(new Spot(4, 2))).thenReturn(new ArrayList<>(Arrays.asList(UP, LEFT, RIGHT)));
        when(repo.getPossibleActionsFor(new Spot(5, 3))).thenReturn(new ArrayList<>(Arrays.asList(UP, DOWN, RIGHT)));

        SnailAI ai = new SnailAI(repo);

        Action[] actions = ai.play();

        assertThat(actions)
                .hasSize(1)
                .containsOnly(new Action(UP));
    }

    @Test
    @DisplayName("turn on the corners")
    void turnOnCorner() {
        KnowledgeRepo repo = Mockito.mock(KnowledgeRepo.class);

        when(repo.getN()).thenReturn(2);
        when(repo.getP()).thenReturn(1);

        when(repo.getPlayerStartingSpot()).thenReturn(new Spot(3, 3));
        when(repo.getPlayerCurrentSpot()).thenReturn(new Spot(4, 2));

        when(repo.getPossibleActions()).thenReturn(new ArrayList<>(Arrays.asList(UP, LEFT, RIGHT)));
        when(repo.getPossibleActionsFor(new Spot(4, 1))).thenReturn(new ArrayList<>(Arrays.asList(UP, LEFT, RIGHT)));
        when(repo.getPossibleActionsFor(new Spot(3, 2))).thenReturn(new ArrayList<>(Arrays.asList(UP, LEFT)));
        when(repo.getPossibleActionsFor(new Spot(5, 2))).thenReturn(new ArrayList<>(Arrays.asList(UP, DOWN, RIGHT)));

        SnailAI ai = new SnailAI(repo);

        Action[] actions = ai.play();

        assertThat(actions)
                .hasSize(1)
                .containsOnly(new Action(LEFT));
    }

    @Test
    @DisplayName("break snail movement when movement is not available")
    void whenMovementIsNotPossible() {
        KnowledgeRepo repo = Mockito.mock(KnowledgeRepo.class);

        when(repo.getN()).thenReturn(2);
        when(repo.getP()).thenReturn(1);

        when(repo.getPlayerStartingSpot()).thenReturn(new Spot(3, 3));
        when(repo.getPlayerCurrentSpot()).thenReturn(new Spot(4, 2));

        when(repo.getPossibleActions()).thenReturn(new ArrayList<>(Arrays.asList(UP, RIGHT)));
        when(repo.getPossibleActionsFor(new Spot(4, 1))).thenReturn(new ArrayList<>(Arrays.asList(UP, RIGHT)));
        when(repo.getPossibleActionsFor(new Spot(5, 2))).thenReturn(new ArrayList<>(Arrays.asList(UP, DOWN, RIGHT)));

        SnailAI ai = new SnailAI(repo);

        Action[] actions = ai.play();

        assertThat(actions)
                .hasSize(1)
                .containsOnly(new Action(UP));
    }

    @Test
    @DisplayName("goes down when game is lost")
    void goesDownWhenGameIsLost() {
        KnowledgeRepo repo = Mockito.mock(KnowledgeRepo.class);

        when(repo.getN()).thenReturn(2);
        when(repo.getP()).thenReturn(1);

        when(repo.getPlayerStartingSpot()).thenReturn(new Spot(3, 3));
        when(repo.getPlayerCurrentSpot()).thenReturn(new Spot(4, 2));

        when(repo.getPossibleActions()).thenReturn(Collections.emptyList());

        SnailAI ai = new SnailAI(repo);

        Action[] actions = ai.play();

        assertThat(actions)
                .hasSize(1)
                .containsOnly(new Action(DOWN));
    }

    @Test
    @DisplayName("looks forward to next movement in order to avoid locking itself")
    void doesNotLockItSelf() {
        KnowledgeRepo repo = Mockito.mock(KnowledgeRepo.class);

        when(repo.getN()).thenReturn(2);
        when(repo.getP()).thenReturn(1);

        when(repo.getPlayerStartingSpot()).thenReturn(new Spot(3, 3));
        when(repo.getPlayerCurrentSpot()).thenReturn(new Spot(5, 5));

        when(repo.getPossibleActions()).thenReturn(new ArrayList<>(Arrays.asList(LEFT, RIGHT)));

        when(repo.getPossibleActionsFor(new Spot(4, 5))).thenReturn(new ArrayList<>(Collections.singletonList(LEFT)));
        when(repo.getPossibleActionsFor(new Spot(6, 5))).thenReturn(Arrays.asList(UP, RIGHT));

        SnailAI ai = new SnailAI(repo);

        Action[] actions = ai.play();

        assertThat(actions)
                .hasSize(1)
                .containsOnly(new Action(RIGHT));
    }

    @Test
    @DisplayName("does not locks itself against the wall")
    void doesNotLockItSelfAgainstWall() {
        KnowledgeRepo repo = Mockito.mock(KnowledgeRepo.class);

        when(repo.getN()).thenReturn(2);
        when(repo.getP()).thenReturn(1);

        when(repo.getPlayerStartingSpot()).thenReturn(new Spot(7, 1));
        when(repo.getPlayerCurrentSpot()).thenReturn(new Spot(4, 0));

        when(repo.getPossibleActions()).thenReturn(new ArrayList<>(Arrays.asList(LEFT, RIGHT)));

        when(repo.getPossibleActionsFor(new Spot(5, 0))).thenReturn(Collections.emptyList());
        when(repo.getPossibleActionsFor(new Spot(3, 0))).thenReturn(Arrays.asList(DOWN, LEFT));

        SnailAI ai = new SnailAI(repo);

        Action[] actions = ai.play();

        assertThat(actions)
                .hasSize(1)
                .containsOnly(new Action(LEFT));
    }
}