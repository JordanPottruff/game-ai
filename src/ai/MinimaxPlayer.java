package ai;

import game.GamePlayer;
import game.GameState;
import game.GameStatus;

import java.util.AbstractMap;
import java.util.Map;

public class MinimaxPlayer extends GamePlayer {

    private final String label;
    private final char symbol;
    protected final int depth;

    public MinimaxPlayer(String label, char symbol, int depth) {
        this.label = label;
        this.symbol = symbol;
        this.depth = depth;
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
        checkInvalidState(state);
        Map<String, ? extends GameState> nextStates = state.nextStates();
        double maxScore = Double.NEGATIVE_INFINITY;
        String maximizingMove = null;
        for(String move: nextStates.keySet()) {
            GameState nextState = nextStates.get(move);
            double score = getValue(nextState, depth);
            if(score > maxScore || maximizingMove == null) {
                maxScore = score;
                maximizingMove = move;
            }
        }
        GameState maximizingState = nextStates.get(maximizingMove);
        return new AbstractMap.SimpleEntry<>(maximizingMove, maximizingState);
    }

    protected void checkInvalidState(GameState state) {
        if (state.nextPlayer() != this) {
            String msg =
                    String.format("Player %s is not the next player.", this);
            throw new IllegalArgumentException(msg);
        }
    }

    protected double getValue(GameState state, int depth) {
        Map<String, ? extends GameState> nextStates = state.nextStates();
        GamePlayer nextPlayer = state.nextPlayer();


        if (depth == 1) {
            return state.getScore(this);
        } else if (state.getStatus().isOver()) {
            return getTerminalScore(state.getStatus());
        }

        // Maximize own-score for own-moves, but minimize own-score for
        // opponent moves.
        if (nextPlayer == this) {
            return nextStates.values().stream()
                    .map((nextState) -> getValue(nextState, depth-1))
                    .max(Double::compare)
                    .orElse(Double.NEGATIVE_INFINITY);
        } else {
            return nextStates.values().stream()
                    .map((nextState) -> getValue(nextState, depth-1))
                    .min(Double::compare)
                    .orElse(Double.POSITIVE_INFINITY);
        }
    }

    protected double getScore(GameState state, int depth) {
        GameStatus status = state.getStatus();
        if (status.isOver()) {
            return getTerminalScore(status);
        } else if (depth == 1){
            return state.getScore(this);
        }
        return getValue(state, depth-1);
    }

    protected double getTerminalScore(GameStatus status) {
        if (status.getResult() == GameStatus.Result.TIE) return 0;
        if (status.getWinner().isEmpty()) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return status.getWinner().get().equals(this) ?
                Double.POSITIVE_INFINITY :
                Double.NEGATIVE_INFINITY;
    }
}
