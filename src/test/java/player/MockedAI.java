package player;

import org.mockito.Mockito;

import com.google.common.base.MoreObjects;

import player.Player.Action;

public final class MockedAI {

    private MockedAI() {
        // Utility class
    }

    public static AIMapper any() {
        return new AIMapper(newBuilder().build());
    }

    public static AIMapper anyWithActions(Action... actions) {
        return new AIMapper(newBuilder()
                .withActions(actions)
                .build());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Action[] actions;

        private Builder() {
            this.actions = new Action[] { Mockito.mock(Action.class) };
        }

        public Builder withActions(Action... actions) {
            this.actions = actions;
            return this;
        }

        Player.AI build() {
            return new MockedArtificialIntelligence(actions);
        }
    }

    private static class MockedArtificialIntelligence extends Player.AI {

        private final Action[] actions;

        private MockedArtificialIntelligence(Action[] actions) {
            super(() -> {});
            this.actions = actions;
        }

        @Override
        public Action[] play() {
            return actions;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("actions", actions)
                    .toString();
        }
    }
}
