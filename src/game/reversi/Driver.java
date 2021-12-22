package game.reversi;

import ai.Minimax;
import game.GameAction;
import game.GameState;
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
            ReversiState state = new ReversiState(white, black);

            state.set(1, 3, white);
            state.set(2, 2, white);
            state.set(2, 5, black);
            state.set(3, 1, white);
            state.set(3,2, black);
            state.set(3, 3, white);
            state.set(3, 4, black);
            state.set(4, 2, white);
            state.set(4,3, black);
            state.set(4,4, white);
            state.set(5, 2, black);
            state.set(5, 3, white);
            state.set(5, 4, white);
            state.set(5,5, white);
            state.set(5,6, black);
            state.set(5, 7, black);
            state.set(6, 4, black);
            state.set(6, 5, black);
            state.set(6, 6, white);
            state.set(7, 4, black);

            waitForNextMove(server, state, white);
            return toJsonResponse(state);
        });
    }

    private static void waitForNextMove(ReversiServer server, GameState state, Player ai) {
        server.onMove((move) -> {
            Set<? extends GameAction> actions = state.getAvailableActions();
            System.out.println("move! " + move);
            System.out.println("actions: " + actions);
            for (GameAction action : actions) {
                if (action.getLabel().equals(move)) {
                    GameState newState = action.getResultingState();
                    if (newState.getNextPlayer() != ai) {
                        waitForNextMove(server, newState, ai);
                        return toJsonResponse(newState);
                    }
                    GameState afterAI = aiMove(newState, ai);
                    while (afterAI != null && afterAI.getNextPlayer() == ai) {
                        afterAI = aiMove(afterAI, ai);
                    }
                    if (afterAI == null) {
                        return toJsonResponse(newState);
                    } else {
                        waitForNextMove(server, afterAI, ai);
                        return toJsonResponse(newState, afterAI);
                    }

                }
            }
            // Replay last state.
            return toJsonResponse(state);
        });
    }

    private static GameState aiMove(GameState state, Player ai) {
        Set<? extends GameAction> actions = state.getAvailableActions();
        if (actions.isEmpty()) {
            return null;
        }
        GameAction action = Minimax.select(state, ai, 4);
        return action.getResultingState();
    }

    private static String toJsonResponse(GameState state) {
        Set<? extends GameAction> actions = state.getAvailableActions();
        if (actions.isEmpty()) {
            double differential = state.getScoreDifferential();
            Player winner = differential > 0 ? state.getPlayer() : state.getOpposingPlayer();
            return "{\"state\": " + state.toJSON() + ",\"result\": \"" + winner.getSymbol() + "\", \"diff\": \"" + differential + "\"}";
        }
        Player nextPlayer = null;
        for (GameAction action: actions) {
            nextPlayer = action.getPlayer();
            break;
        }
        return "{\"state\": " + state.toJSON() + ", \"nextPlayer\": \"" + nextPlayer.getSymbol() + "\"}";
    }

    private static String toJsonResponse(GameState first, GameState second) {
        return "{\"moves\": [" +
                toJsonResponse(first) + "," +
                toJsonResponse(second) + "]}";
    }
}
