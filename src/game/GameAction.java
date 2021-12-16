package game;

/**
 * An action that transitions to a new game state.
 */
public interface GameAction {

    /** The label for the action */
    String getLabel();

    /** The game state that results from performing the action. */
    GameState getResultingState();

    /** The player who performs the action. */
    Player getPlayer();
}
