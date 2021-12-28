package ai;

import game.GameState;

import java.util.AbstractMap;
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


        List<Future<Result>> resultFutures = getMoves(executor, state);
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return chooseMove(resultFutures, state.nextStates());
    }

    private List<Future<Result>> getMoves(ExecutorService executor,
                                          GameState state) {
        // Separate first maximization out in order to dispatch work to
        // executor service.
        List<Callable<Result>> explorePaths = new ArrayList<>();
        Map<String, ? extends GameState> nextStates = state.nextStates();
        for(String move: nextStates.keySet()) {
            final GameState nextState = nextStates.get(move);
            explorePaths
                    .add(() -> new Result(move, getValue(nextState, depth)));
        }
        List<Future<Result>> resultFutures = new ArrayList<>();
        try {
            resultFutures = executor.invokeAll(explorePaths);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return resultFutures;
    }

    private Map.Entry<String, GameState> chooseMove(List<Future<Result>> resultFutures, Map<String, ? extends GameState> nextStates) {
        double maximizingScore = Double.NEGATIVE_INFINITY;
        String maximizingMove = null;
        for(Future<Result> resultFuture: resultFutures) {
            try {
                Result result = resultFuture.get();
                if (result.score() > maximizingScore || maximizingMove == null) {
                    maximizingMove = result.move();
                    maximizingScore = result.score();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        GameState maximizingState = nextStates.get(maximizingMove);
        return new AbstractMap.SimpleEntry<>(maximizingMove, maximizingState);
    }

    private static record Result(String move, double score) {}

}
