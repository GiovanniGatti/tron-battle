import org.mockito.Mockito;

import com.google.common.base.MoreObjects;

public final class MockedAI {

    private MockedAI() {
        // Utility class
    }

    public static AIMapper any() {
        return new AIMapper(newBuilder().build());
    }

    public static AIMapper anyWithActions(Player.Action... actions) {
        return new AIMapper(newBuilder()
                .withActions(actions)
                .build());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Player.Action[] actions;

        private Builder() {
            this.actions = new Player.Action[] { Mockito.mock(Player.Action.class) };
        }

        public Builder withActions(Player.Action... actions) {
            this.actions = actions;
            return this;
        }

        Player.AI build() {
            return new MockedArtificialIntelligence(actions);
        }
    }

    private static class MockedArtificialIntelligence extends Player.AI {

        private final Player.Action[] actions;

        private MockedArtificialIntelligence(Player.Action[] actions) {
            super(() -> {
            });
            this.actions = actions;
        }

        @Override
        public Player.Action[] play() {
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
