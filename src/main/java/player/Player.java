package player;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntSupplier;

public final class Player {

    public static void main(String args[]) {
        // TODO: implement me!
        AI ai = null;

    }

    /**
     * Represents an action that can be taken
     */
    public static class Action {

        public Action() {
            // TODO: implement what action is
        }

        public String asString() {
            return "";
        }
    }

    public static abstract class AI {

        private final Map<String, Object> conf;
        private final IntSupplier inputSupplier;

        /**
         * Builds an AI with specified configuration.<br>
         * It is recommended to create a default configuration.
         */
        public AI(Map<String, Object> conf, IntSupplier inputSupplier) {
            this.conf = Collections.unmodifiableMap(conf);
            this.inputSupplier = inputSupplier;
        }

        /**
         * Builds an AI with an empty configuration.
         */
        public AI(IntSupplier inputSupplier) {
            this(Collections.emptyMap(), inputSupplier);
        }

        /**
         * Implements the IA algorithm
         * 
         * @return the best ordered set of actions found
         */
        public abstract Action[] play();

        public Map<String, Object> getConf() {
            return conf;
        }

        protected int readInput() {
            return inputSupplier.getAsInt();
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AI ai = (AI) o;
            return Objects.equals(conf, ai.conf);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(conf, getClass());
        }
    }
}
