package ai;

import game.GameAction;
import game.GameState;
import game.Player;

import java.util.Set;

public class Minimax {

    public static GameAction select(GameState state, Player forPlayer, int levels) {
        System.out.println(state.getAvailableActions().size());
        return selectResult(state, forPlayer, levels).getAction();
    }

    public static MinimaxResult selectResult(GameState state, Player forPlayer, int levels) {
        Player nextPlayer = state.getNextPlayer();
        Set<? extends GameAction> actions = state.getAvailableActions();

        if (nextPlayer == forPlayer) {
            GameAction maximizingAction = null;
            double maximumScore = Double.NEGATIVE_INFINITY;
            for(GameAction action: actions) {
                double score;
                GameState resultingState = action.getResultingState();
                Set<? extends GameAction> nextActions = resultingState.getAvailableActions();
                if (nextActions.isEmpty() || levels == 0) {
                    score = resultingState.getScore(forPlayer);
                } else {
                    score = selectResult(resultingState, forPlayer, levels-1).getScore();
                }
                if (score > maximumScore) {
                    maximizingAction = action;
                    maximumScore = score;
                }
            }
            return new MinimaxResult(maximizingAction, maximumScore);
        } else {
            GameAction minimizingAction = null;
            double minimumScore = Double.POSITIVE_INFINITY;
            for(GameAction action: actions) {
                double score;
                GameState resultingState = action.getResultingState();
                Set<? extends GameAction> nextActions = resultingState.getAvailableActions();
                if (nextActions.isEmpty() || levels == 0) {
                    score = resultingState.getScore(forPlayer);
                } else {
                    score = selectResult(resultingState, forPlayer, levels-1).getScore();
                }
                if (score < minimumScore) {
                    minimizingAction = action;
                    minimumScore = score;
                }
            }
            return new MinimaxResult(minimizingAction, minimumScore);
        }
    }

    private static class MinimaxResult {

        private final GameAction action;
        private final double score;

        public MinimaxResult(GameAction action, double score) {
            this.action = action;
            this.score = score;
        }

        public GameAction getAction() {
            return this.action;
        }

        public double getScore() {
            return this.score;
        }
    }
}
