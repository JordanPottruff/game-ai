package game.othello;

import game.GamePlayer;
import game.GameState;

import java.util.Map;

public class OthelloPlayer extends GamePlayer {

    private final String label;
    private final char symbol;

    public OthelloPlayer(String label, char symbol) {
        this.label = label;
        this.symbol = symbol;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public Map.Entry<String, GameState> makeMove(GameState state) {
        return null;
    }
}
