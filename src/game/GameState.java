package game;

import java.util.Set;

/**
 * Represents the state of a 2-player game, where the game can be scored at each state.
 */
public interface GameState {

    /**
     * Returns the player whose action resulted in the current state.
     */
    Player getPlayer();

    /**
     * Returns the opposing player.
     */
    Player getOpposingPlayer();

    /**
     * Returns the set of actions that can be performed from this state.
     * @return the next actions, or an empty set if the game is over.
     */
    Set<? extends GameAction> getAvailableActions();

    /**
     * Returns the player who takes the next action.
     * @return the next player, or null if the game is over.
     */
    Player getNextPlayer();

    /**
     * Returns the score differential between the two players.
     * @return the score of the player returned from {@code getPlayer} minus the
     * score of the player returned from {@code getOpposingPlayer}.
     */
    double getScoreDifferential();
}
