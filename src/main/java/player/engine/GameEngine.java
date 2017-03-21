package player.engine;

public interface GameEngine<T extends AI> {

    /**
     * Computes the new match state based on the players' actions
     */
    void run(T player, T opponent);

    /**
     * 
     * @return the match winner, {@code Winner.ON_GOING} otherwise
     */
    Winner getWinner();

    /**
     * Player input stream.
     */
    int playerInput();

    /**
     * Opponent input stream.
     */
    int opponentInput();

    /**
     * Returns the current player score
     */
    int getPlayerScore();

    /**
     * Returns the current opponent score
     */
    int getOpponentScore();

    /**
     * 
     * Returns the number of executed rounds
     */
    int getNumberOfRounds();

    /**
     *
     * @return the an immutable representation of the game engine's inital state
     */
    State getInitialState();
}
