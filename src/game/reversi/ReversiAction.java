package game.reversi;

import game.GameAction;
import game.Player;

public class ReversiAction implements GameAction {

    private final String label;
    private final ReversiState resultingState;
    private final Player player;

    public ReversiAction(String label, ReversiState resultingState, Player player) {
        this.label = label;
        this.resultingState = resultingState;
        this.player = player;
    }

    public String getLabel() {
        return label;
    }

    public ReversiState getResultingState() {
        return resultingState;
    }

    public Player getPlayer() { return player; }

    public String toString() {
        return label;
    }
}
