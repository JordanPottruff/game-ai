package game;

import java.util.Optional;
import java.util.Set;

public interface GameState {

    /**
     * Returns the next states that can be arrived at via valid game play.
     * @return a set of possible next states.
     */
    Set<GameState> nextStates();

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
     * Returns the winner within the current game state, if present.
     * @return an optional winner, or empty if no one has won.
     */
    Optional<GamePlayer> getWinner();

    /**
     * Returns a JSON representation of the current game state.
     * @return a JSON-serialized version of this object's data.
     */
    String toJson();
}
