package game;

import java.util.Optional;

public class GameStatus {
    private final Result result;
    private final GamePlayer winner;

    private GameStatus(Result result, GamePlayer winner) {
        this.result = result;
        this.winner = winner;
    }

    public static GameStatus createOngoing() {
        return new GameStatus(Result.ONGOING, null);
    }

    public static GameStatus createWinner(GamePlayer winner) {
        return new GameStatus(Result.WON, winner);
    }

    public static GameStatus createTie() {
        return new GameStatus(Result.TIE, null);
    }

    public Result getResult() {
        return this.result;
    }

    public Optional<GamePlayer> getWinner() {
        if (winner == null) return Optional.empty();
        return Optional.of(winner);
    }

    public boolean isOver() {
        return result == Result.TIE || result == Result.WON;
    }

    public String toString() {
        return switch (result) {
            case ONGOING -> "Ongoing...";
            case WON -> winner + " won!";
            case TIE -> "Tie!";
        };
    }

    public enum Result {
        ONGOING,
        WON,
        TIE,
    }
}
