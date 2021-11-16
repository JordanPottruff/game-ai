package game.reversi;

import game.GameAction;

public class ReversiAction implements GameAction {

    private final String label;
    private final ReversiState resultingState;

    public ReversiAction(String label, ReversiState resultingState) {
        this.label = label;
        this.resultingState = resultingState;
    }

    public String getLabel() {
        return label;
    }

    public ReversiState getResultingState() {
        return resultingState;
    }
}
