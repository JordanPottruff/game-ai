package ai;

import game.GameState;

public class MinimaxPrunedParallelPlayer extends MinimaxParallelPlayer {

    public MinimaxPrunedParallelPlayer(String label, char symbol, int depth,
                                       int poolSize) {
        super(label, symbol, depth, poolSize);
    }

    @Override
    protected double getValue(GameState state, int depth) {
        return MinimaxPrunedPlayer.getValue(state, depth, this);
    }
}
