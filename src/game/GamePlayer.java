package game;

public abstract class GamePlayer {

    /**
     * The label for a player is a string that can be used to identify them.
     * @return the player's label.
     */
    public abstract String getLabel();

    /**
     * The symbol for a player is a character used to represent them in
     * places where the full label returned by {@code getLabel} is not
     * appropriate.
     * @return the player's symbol.
     */
    public abstract char getSymbol();

    /**
     * Given a state of the game, returns a new state chosen by the player.
     * Essentially, this method defines how a player plays the game.
     * @param state the state that the player will move from.
     * @return the state resulting from the player's move.
     * @throws IllegalArgumentException if the player is not the next player
     * to make a move in the given game state.
     */
    public abstract GameState makeMove(GameState state);

    public String toString() {
        return this.getLabel();
    }
}
