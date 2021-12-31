package ai;

import game.GamePlayer;
import game.GameState;
import game.GameStatus;

import java.util.*;

public class MonteCarloPlayer extends MonteCarloNaivePlayer {

    private static final double C = Math.sqrt(2);

    public MonteCarloPlayer(String label, char symbol, int samples) {
        super(label, symbol, samples);
    }

    @Override
    public Map.Entry<String, ? extends GameState> makeMove(GameState root) {
        Map<GameState, Result> stateStore = new HashMap<>();
        stateStore.put(root, new Result(0, 0));
        for(int i=0; i<samples; i++) {
            runSample(root, stateStore, this);
        }
        return pickBest(root, stateStore);
    }

    protected static void runSample(GameState root,
                             Map<GameState, Result> stateStore,
                                    GamePlayer player) {
        List<GameState> selectionPath = select(root, stateStore, player);
        GameState selection = selectionPath.get(selectionPath.size()-1);
        GameStatus rolloutResult = MonteCarloNaivePlayer.rollout(selection);
        double rolloutScore = scoreStatus(rolloutResult, player);

        updateResult(selection, rolloutScore, stateStore);
        for (GameState alongPath: selectionPath) {
            updateResult(alongPath, rolloutScore, stateStore);
        }
    }

    protected static Map.Entry<String, ? extends GameState> pickBest(GameState root,
                             Map<GameState, Result> stateStore) {
        double maxTotal = Double.NEGATIVE_INFINITY;
        Map.Entry<String, ? extends GameState> maximizingMove = null;
        for(Map.Entry<String, ? extends GameState> move: root.nextStates().entrySet()) {
            double total = stateStore.get(move.getValue()).total();
            if (total > maxTotal || maximizingMove == null) {
                maxTotal = total;
                maximizingMove = move;
            }
        }
        return maximizingMove;
    }

    private static List<GameState> select(GameState root,
                                   Map<GameState, Result> stateStore,
                                          GamePlayer player) {
        GameState state = root;
        Optional<? extends GameState> child = getAvailableChild(state,
                stateStore);

        List<GameState> path = new ArrayList<>();
        while (child.isEmpty() && !state.nextStates().isEmpty()) {
            path.add(state);
            state = pickChildUCT(state, stateStore, player);
            child = getAvailableChild(state, stateStore);
        }
        child.ifPresent(path::add);
        return path;
    }

    private static GameState pickChildUCT(GameState state,
                                   Map<GameState, Result> stateStore,
                                          GamePlayer player) {
        double parentTotal = stateStore.get(state).total;
        double maxUct = Double.NEGATIVE_INFINITY;
        GameState maximizingState = null;
        for (GameState child: state.nextStates().values()) {
            Result childResult = stateStore.get(child);
            double childWins;
            if (state.nextPlayer().equals(player)) {
                childWins = childResult.wins;
            } else {
                childWins = childResult.total - childResult.wins;
            }
            double childTotal = childResult.total();
            double uct = calculateUCT(childWins, childTotal, parentTotal);
            if (uct > maxUct || maximizingState == null) {
                maxUct = uct;
                maximizingState = child;
            }
        }
        return maximizingState;
    }

    private static double calculateUCT(double wins, double childTotal,
                                double parentTotal) {
        double exploitation = (wins / childTotal);
        double exploration = C * Math.sqrt(Math.log(parentTotal)/childTotal);
        return exploitation + exploration;
    }

    private static Optional<? extends GameState> getAvailableChild(GameState state,
                                                  Map<GameState,
                                                  Result> stateStore) {
        return state.nextStates().values().stream()
                .filter((nextState) -> !isAvailable(nextState, stateStore)).findFirst();
    }

    private static boolean isAvailable(GameState state,
                                Map<GameState, Result> stateStore) {
        return stateStore.containsKey(state);
    }

    private static void updateResult(GameState state, double score,
                              Map<GameState, Result> stateStore) {
        Result existing = stateStore.getOrDefault(state, new Result(0, 0));
        stateStore.put(state, new Result(existing.wins() + score,
                existing.total() + 1));
    }

    record Result(double wins, double total) {}
}
