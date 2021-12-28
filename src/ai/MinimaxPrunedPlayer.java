package ai;

import game.GamePlayer;
import game.GameState;

import java.util.Map;

public class MinimaxPrunedPlayer extends MinimaxPlayer {

    public MinimaxPrunedPlayer(String label, char symbol, int depth) {
        super(label, symbol, depth);
    }

    @Override
    protected double getValue(GameState state, int depth) {
        return getValue(state, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, depth);
    }

    protected double getValue(GameState state, double alpha,
                              double beta, int depth) {
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
            double maxScore = Double.NEGATIVE_INFINITY;
            for(GameState nextState: nextStates.values()) {
                maxScore = Math.max(maxScore, getValue(nextState,
                        alpha, beta, depth-1));
                if (maxScore >= beta) {
                    break;
                }
                alpha = Math.max(alpha, maxScore);
            }
            return maxScore;
        } else {
            double minScore = Double.POSITIVE_INFINITY;
            for(GameState nextState: nextStates.values()) {
                minScore = Math.min(minScore, getValue(nextState,
                        alpha, beta, depth-1));
                if (minScore <= alpha) {
                    break;
                }
                beta = Math.min(beta, minScore);
            }
            return minScore;
        }
    }
}
