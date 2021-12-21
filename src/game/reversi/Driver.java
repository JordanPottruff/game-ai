package game.reversi;

import game.Player;

import java.io.IOException;
import java.util.Set;

public class Driver {

    public static void main(String[] args) throws IOException {
        ReversiServer server = new ReversiServer("localhost", 8001);
        Player white = new ReversiPlayer("white", 'W');
        Player black = new ReversiPlayer("black", 'B');

        server.onNewGame((unused) -> {
            System.out.println("New game!");
            final ReversiState state = new ReversiState(white, black);

            waitForNextMove(server, state);
            return toJSONResponse(state);
        });
    }

    private static void waitForNextMove(ReversiServer server, ReversiState state) {
        server.onMove((move) -> {
            Set<ReversiAction> actions = state.getAvailableActions();
            System.out.println("move! " + move);
            System.out.println("actions: " + actions);
            for (ReversiAction action : actions) {
                if (action.getLabel().equals(move)) {
                    ReversiState newState = action.getResultingState();
                    waitForNextMove(server, newState);
                    return toJSONResponse(newState);
                }
            }
            // Replay last state.
            return toJSONResponse(state);
        });
    }

    private static String toJSONResponse(ReversiState state) {
        Set<ReversiAction> actions = state.getAvailableActions();
        if (actions.isEmpty()) {
            double differential = state.getScoreDifferential();
            Player winner = differential > 0 ? state.getPlayer() : state.getOpposingPlayer();
            return "{\"state\": " + state.toJSON() + ",\"result\": \"" + winner.getSymbol() + "\", \"diff\": \"" + differential + "\"}";
        }
        Player nextPlayer = null;
        for (ReversiAction action: actions) {
            nextPlayer = action.getPlayer();
            break;
        }
        return "{\"state\": " + state.toJSON() + ", \"nextPlayer\": \"" + nextPlayer.getSymbol() + "\"}";
    }
}
