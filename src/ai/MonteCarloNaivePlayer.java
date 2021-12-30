package ai;

import game.GamePlayer;
import game.GameState;
import game.GameStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class MonteCarloNaivePlayer extends GamePlayer {

    private final String label;
    private final char symbol;
    protected final int samples;

    public MonteCarloNaivePlayer(String label, char symbol, int samples) {
        this.label = label;
        this.symbol = symbol;
        this.samples = samples;
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
    public Map.Entry<String, ? extends GameState> makeMove(GameState state) {
        int samplesPerChild = samples / state.nextStates().size();
        double maxScore = Double.NEGATIVE_INFINITY;
        Map.Entry<String, ? extends GameState> maximizingMove = null;
        for(Map.Entry<String, ? extends GameState> move:
                state.nextStates().entrySet()) {
            double score = 0;
            for (int i=0; i<samplesPerChild; i++) {
                GameStatus rolloutResult = rollout(move.getValue());
                score += scoreStatus(rolloutResult);
            }
            if(score > maxScore || maximizingMove == null) {
                maxScore = score;
                maximizingMove = move;
            }
        }
        return maximizingMove;
    }

    protected static GameStatus rollout(GameState state) {
        while (!state.getStatus().isOver()) {
            state = pickRandom(state.nextStates().values());
        }
        return state.getStatus();
    }

    protected double scoreStatus(GameStatus status) {
        if (status.getResult().equals(GameStatus.Result.TIE)) {
            return 0.5;
        } else {
            return status.getWinner().map((player) -> player == this ? 1.0 :
                    0.0).orElseThrow();
        }
    }

    protected static <T> T pickRandom(Collection<T> collection) {
        int size = collection.size();
        int skip = new Random().nextInt(size);
        return collection.stream().skip(skip).findFirst().orElse(null);
    }
}
