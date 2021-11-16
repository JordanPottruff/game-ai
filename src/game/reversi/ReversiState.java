package game.reversi;

import game.GameState;
import game.Player;

import java.util.Set;

public class ReversiState implements GameState {

    private final Player white;
    private final Player black;

    public ReversiState(Player white, Player black) {
        this.white = white;
        this.black = black;
    }

    public Player getFirstPlayer() {
        return white;
    }

    public Player getSecondPlayer() {
        return black;
    }

    public Set<ReversiAction> getAvailableActions() {
        return null;
    }

    public Player getNextPlayer() {
        return null;
    }

    public double getScoreDifferential() {
        return 0;
    }
}
