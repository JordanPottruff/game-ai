package ai;

import game.GamePlayer;
import game.GameState;
import game.GameStatus;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MinimaxPlayer extends GamePlayer {

    private final String label;
    private final char symbol;
    private final int depth;

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
        if (state.nextPlayer() != this) {
            String msg =
                    String.format("Player %s is not the next player.", this);
            throw new IllegalArgumentException(msg);
        }
        MinimaxResult result = makeMove(state, depth);
        return result.toEntry();
    }

    public MinimaxResult makeMove(GameState state, int depth) {
        Map<String, ? extends GameState> nextStates = state.nextStates();
        GameStatus status = state.getStatus();
        GamePlayer nextPlayer = state.nextPlayer();

        // Maximize own-score for own-moves, but minimize own-score for
        // opponent-moves.
        if (nextPlayer == this) {
            GameState maximizingState = null;
            String maximizingMove = null;
            double maximizingScore = Double.NEGATIVE_INFINITY;
            for(Map.Entry<String, ? extends GameState> next: nextStates.entrySet()) {
                GameState nextState = next.getValue();
                double score;
                if (status.isOver()) {
                    score = getTerminalScore(status);
                } else if (depth == 1) {
                    score = nextState.getScore(this);
                } else {
                    score = makeMove(nextState, depth-1).getScore();
                }
                if (score > maximizingScore) {
                    maximizingState = nextState;
                    maximizingMove = next.getKey();
                    maximizingScore = score;
                }
            }
            return new MinimaxResult(maximizingState, maximizingMove,
                    maximizingScore);
        } else {
            GameState minimizingState = null;
            String minimizingMove = null;
            double minimizingScore = Double.POSITIVE_INFINITY;
            for(Map.Entry<String, ? extends GameState> next: nextStates.entrySet()) {
                GameState nextState = next.getValue();
                double score;
                if (status.isOver()) {
                    score = getTerminalScore(status);
                } else if (depth == 1) {
                    score = nextState.getScore(this);
                } else {
                    score = makeMove(nextState, depth-1).getScore();
                }
                if (score < minimizingScore) {
                    minimizingState = nextState;
                    minimizingMove = next.getKey();
                    minimizingScore = score;
                }
            }
            return new MinimaxResult(minimizingState, minimizingMove,
                    minimizingScore);
        }
    }

    private double getTerminalScore(GameStatus status) {
        if (status.getResult() == GameStatus.Result.TIE) return 0;
        if (status.getWinner().isEmpty()) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return status.getWinner().get().equals(this) ?
                Double.POSITIVE_INFINITY :
                Double.NEGATIVE_INFINITY;
    }

    private static final class MinimaxResult {
        private final GameState state;
        private final String move;
        private final double score;

        MinimaxResult(GameState state, String move, double score) {
            this.state = state;
            this.move = move;
            this.score = score;
        }

        GameState getState() {
            return this.state;
        }

        String getMove() {
            return this.move;
        }

        double getScore() {
            return this.score;
        }

        Map.Entry<String, GameState> toEntry() {
            return new AbstractMap.SimpleEntry<String, GameState>(move, state);
        }
    }

}
