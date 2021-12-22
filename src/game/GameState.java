package game;

import java.util.Map;

public interface GameState {

    /**
     * Returns the next states that can be arrived at via valid game play.
     * @return a map of each move to the resulting state. The key is the
     * label for the move, and the value is the next state arrived via the move.
     */
    Map<String, ? extends GameState> nextStates();

    /**
     * The player who made the move that resulted in this state.
     * @return the last player to move.
     */
    GamePlayer lastPlayer();

    /**
     * The player who makes the next move. This is the player who is allowed
     * to make the transition to one of the new states returned by {@code
     * nextStates}.
     * @return the next player.
     */
    GamePlayer nextPlayer();

    /**
     * Given a player within the game, returns the other player.
     * @throws IllegalArgumentException if the given player does not belong
     * to the game.
     * @return the player who opposes the given player.
     */
    GamePlayer getOtherPlayer(GamePlayer player);

    /**
     * Returns the player's "score", a numeric value used for comparison
     * against the opposing player to determine their advantage in the
     * current game state.
     * @param player the player of interest.
     * @return the score for the given player.
     */
    double getScore(GamePlayer player);

    /**
     * Returns the status of the current state.
     * @return an optional winner, or empty if no one has won.
     */
    GameStatus getStatus();

    /**
     * Returns a JSON representation of the current game state.
     * @return a JSON-serialized version of this object's data.
     */
    String toJson();
}
