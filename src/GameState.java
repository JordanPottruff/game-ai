import java.util.Set;

/**
 * Represents the state of a 2-player game, where the game can be scored at each state.
 */
public interface GameState {

    /**
     * Returns the first player of the game.
     */
    Player getFirstPlayer();

    /**
     * Returns the second player of the game.
     */
    Player getSecondPlayer();

    /**
     * Returns the set of actions that can be performed from this state.
     * @return the next actions, or an empty set if the game is over.
     */
    Set<GameAction> getAvailableActions();

    /**
     * Returns the player who takes the next action.
     * @return the next player, or null if the game is over.
     */
    Player getNextPlayer();

    /**
     * Returns the score differential between the two players.
     * @return the first player's score subtracted by the second player's score.
     */
    double getScoreDifferential();
}
