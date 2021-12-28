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
        return getValue(state, depth, this);
    }

    protected static double getValue(GameState state, int depth,
                                     GamePlayer player) {
        return getValue(state, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, depth, player);
    }

    protected static double getValue(GameState state, double alpha,
                              double beta, int depth, GamePlayer player) {
        Map<String, ? extends GameState> nextStates = state.nextStates();
        GamePlayer nextPlayer = state.nextPlayer();

        if (depth == 1) {
            return state.getScore(player);
        } else if (state.getStatus().isOver()) {
            return getTerminalScore(state.getStatus(), player);
        }

        // Maximize own-score for own-moves, but minimize own-score for
        // opponent moves.
        if (nextPlayer == player) {
            double maxScore = Double.NEGATIVE_INFINITY;
            for(GameState nextState: nextStates.values()) {
                maxScore = Math.max(maxScore, getValue(nextState,
                        alpha, beta, depth-1, player));
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
                        alpha, beta, depth-1, player));
                if (minScore <= alpha) {
                    break;
                }
                beta = Math.min(beta, minScore);
            }
            return minScore;
        }
    }
}
