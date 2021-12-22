package game.othello;

import game.GamePlayer;
import game.GameState;
import game.GameStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OthelloState implements GameState {

    // Cached values.
    private Map<String, OthelloState> nextStatesCache = null;
    private Map<GamePlayer, Double> scoresCache = new HashMap<>();

    // Immutable state.
    private final OthelloPlayer white;
    private final OthelloPlayer black;
    private final OthelloPlayer lastToMove;
    private final OthelloBoard board;

    public OthelloState(OthelloPlayer white, OthelloPlayer black) {
        this(white, black, white, new OthelloBoard(white, black));
    }

    private OthelloState(OthelloPlayer white, OthelloPlayer black,
                         OthelloPlayer lastToMove, OthelloBoard board) {
        this.white = white;
        this.black = black;
        this.lastToMove = lastToMove;
        this.board = board;
    }

    @Override
    public Map<String, OthelloState> nextStates() {
        // Compute next states if it's not already cached.
        if (nextStatesCache == null) {
            this.nextStatesCache = nextStatesNoCache();
        }
        return this.nextStatesCache;
    }

    // Computes the next states that can be arrived at but does not cache the
    // result.
    private Map<String, OthelloState> nextStatesNoCache() {
        // First look for moves in the player who did not go last.
        OthelloPlayer player = getOtherPlayer(lastPlayer());
        Map<String, OthelloBoard> nextBoards = board.getNextBoards(player);
        if (nextBoards.isEmpty()) {
            player = lastPlayer();
            nextBoards = board.getNextBoards(player);
        }
        // Convert each board to a game state.
        final OthelloPlayer fPlayer = player;
        return nextBoards.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, board ->
                        new OthelloState(white, black, fPlayer, board.getValue())
                ));
    }

    @Override
    public OthelloPlayer lastPlayer() {
        return lastToMove;
    }

    @Override
    public OthelloPlayer nextPlayer() {
        Map<String, OthelloState> nextStates = nextStates();
        // Examine first of next-states to see who the last player is, which
        // is the next player of this state.
        for(Map.Entry<String, OthelloState> nextState: nextStates.entrySet()) {
            return nextState.getValue().lastPlayer();
        }
        return null;
    }

    @Override
    public OthelloPlayer getOtherPlayer(GamePlayer player) {
        if (player == white) return black;
        if (player == black) return white;
        throw new IllegalArgumentException("Player " + player + "is not a " +
                "valid player");
    }

    @Override
    public double getScore(GamePlayer player) {
        if (player != white && player != black) {
            throw new IllegalArgumentException("Player " + player + " is " +
                    "invalid.");
        }
        Double cachedScore = scoresCache.get(player);
        if (cachedScore == null) {
            double score = board.getScore(player);
            scoresCache.put(player, score);
            return score;
        }
        return cachedScore;
    }

    @Override
    public GameStatus getStatus() {
        if(nextStates().isEmpty()) {
            double whiteScore = getScore(white);
            double blackScore = getScore(black);
            if (whiteScore == blackScore) return GameStatus.createTie();
            if (whiteScore > blackScore) return GameStatus.createWinner(white);
            else return GameStatus.createWinner(black);
        }
        return GameStatus.createOngoing();
    }

    @Override
    public String toJson() {
        return null;
    }

    public String toString() {
        return board.toString();
    }
}
