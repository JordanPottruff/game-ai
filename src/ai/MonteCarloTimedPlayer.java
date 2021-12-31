package ai;

import game.GamePlayer;
import game.GameState;

import java.util.HashMap;
import java.util.Map;

public class MonteCarloTimedPlayer extends GamePlayer {

    private final String label;
    private final char symbol;
    protected final long delayMillis;

    public MonteCarloTimedPlayer(String label, char symbol, long delayMillis) {
        this.label = label;
        this.symbol = symbol;
        this.delayMillis = delayMillis;
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
    public Map.Entry<String, ? extends GameState> makeMove(GameState root) {
        long startMillis = System.currentTimeMillis();
        Map<GameState, MonteCarloPlayer.Result> stateStore = new HashMap<>();
        stateStore.put(root, new MonteCarloPlayer.Result(0, 0));
        while (System.currentTimeMillis() < startMillis + delayMillis) {
            MonteCarloPlayer.runSample(root, stateStore, this);
        }
        return MonteCarloPlayer.pickBest(root, stateStore);
    }
}
