package ai;

import game.GamePlayer;
import game.GameState;

import java.util.Optional;
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
    public GameState makeMove(GameState state) {
        if (state.nextPlayer() != this) {
            String msg =
                    String.format("Player %s is not the next player.", this);
            throw new IllegalArgumentException(msg);
        }
        return makeMove(state, depth).getState();
    }

    public MinimaxResult makeMove(GameState state, int depth) {
        Set<GameState> nextStates = state.nextStates();
        Optional<GamePlayer> winner = state.getWinner();
        GamePlayer nextPlayer = state.nextPlayer();

        // Maximize own-score for own-moves, but minimize own-score for
        // opponent-moves.
        if (nextPlayer == this) {
            GameState maximizingState = null;
            double maximizingScore = Double.NEGATIVE_INFINITY;
            for(GameState nextState: nextStates) {
                double score;
                if (winner.isPresent()) {
                    score = getTerminalScore(winner.get());
                } else if (depth == 0) {
                    score = nextState.getScore(this);
                } else {
                    score = makeMove(nextState, depth-1).getScore();
                }
                if (score > maximizingScore) {
                    maximizingState = nextState;
                    maximizingScore = score;
                }
            }
            return new MinimaxResult(maximizingState, maximizingScore);
        } else {
            GameState minimizingState = null;
            double minimizingScore = Double.POSITIVE_INFINITY;
            for(GameState nextState: nextStates) {
                double score;
                if (winner.isPresent()) {
                    score = getTerminalScore(winner.get());
                } else if (depth == 0) {
                    score = nextState.getScore(this);
                } else {
                    score = makeMove(nextState, depth-1).getScore();
                }
                if (score < minimizingScore) {
                    minimizingState = nextState;
                    minimizingScore = score;
                }
            }
            return new MinimaxResult(minimizingState, minimizingScore);
        }
    }

    private double getTerminalScore(GamePlayer winner) {
        return winner == this ?
                Double.POSITIVE_INFINITY :
                Double.NEGATIVE_INFINITY;
    }

    private static final class MinimaxResult {
        private final GameState state;
        private final double score;

        MinimaxResult(GameState state, double score) {
            this.state = state;
            this.score = score;
        }

        GameState getState() {
            return this.state;
        }

        double getScore() {
            return this.score;
        }
    }

}
