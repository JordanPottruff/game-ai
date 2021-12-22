package main;

import game.othello.OthelloPlayer;
import game.othello.OthelloState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

public class Driver {

    public static void main(String[] args) throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        runTwoPlayer(reader);
    }

    private static void runTwoPlayer(BufferedReader reader) throws IOException {
        OthelloPlayer white = new OthelloPlayer("White", 'W');
        OthelloPlayer black = new OthelloPlayer("Black", 'B');
        OthelloState state = new OthelloState(white, black);

        while(!state.getStatus().isOver()) {
            System.out.println("----------------");
            System.out.println(state + "\n");
            Map<String, OthelloState> nextStates = state.nextStates();
            Set<String> availableMoves = nextStates.keySet();
            System.out.println("Next to move: " + state.nextPlayer());
            System.out.println("Available moves: " + availableMoves);
            System.out.println("Enter move: ");

            String move = null;
            boolean first = true;
            while (move == null) {
                if (!first) {
                    System.out.println("Invalid. Try again: ");
                }
                first = false;
                String input = reader.readLine().trim();
                if (availableMoves.contains(input)) {
                    move = input;
                }
            }
            state = nextStates.get(move);
        }

        System.out.println(state.getStatus());
    }
}
