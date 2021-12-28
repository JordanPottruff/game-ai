package ai;

import game.GamePlayer;
import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MinimaxParallelPlayer extends MinimaxPlayer {

    private final int poolSize;

    public MinimaxParallelPlayer(String label, char symbol, int depth,
                          int poolSize) {
        super(label, symbol, depth);
        this.poolSize = poolSize;
    }

    @Override
    public Map.Entry<String, GameState> makeMove(GameState state) {
        checkInvalidState(state);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        // Separate first maximization out in order to dispatch work to
        // executor service.
        List<Callable<MinimaxResult>> explorePaths = new ArrayList<>();
        Map<String, ? extends GameState> nextStates = state.nextStates();
        for(Map.Entry<String, ? extends GameState> next: nextStates.entrySet()) {
            final GameState nextState = next.getValue();
            final String nextMove = next.getKey();
            final int curDepth = depth;
            explorePaths.add(() ->
                            new MinimaxResult(
                                    nextState, nextMove, getScore(nextState,
                                    curDepth)));
        }
        List<Future<MinimaxResult>> resultFutures = new ArrayList<>();
        try {
            resultFutures = executor.invokeAll(explorePaths);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double maximizingScore = Double.NEGATIVE_INFINITY;
        String maximizingMove = null;
        GameState maximizingState = null;
        for(Future<MinimaxResult> resultFuture: resultFutures) {
            try {
                MinimaxResult result = resultFuture.get();
                if (result.getScore() > maximizingScore || maximizingState == null) {
                    maximizingState = result.getState();
                    maximizingMove = result.getMove();
                    maximizingScore = result.getScore();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new MinimaxResult(maximizingState, maximizingMove,
                maximizingScore).toEntry();
    }

}
