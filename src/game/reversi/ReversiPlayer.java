package game.reversi;

import game.Player;

public class ReversiPlayer implements Player {

    private final String label;
    private final char symbol;

    public ReversiPlayer(String label, char symbol) {
        this.label = label;
        this.symbol = symbol;
    }

    public String getLabel() {
        return label;
    }

    public char getSymbol() {
        return symbol;
    }
}
