package main;

import ai.MinimaxPlayer;
import game.GamePlayer;
import game.GameState;
import game.GameStatus;
import game.othello.OthelloPlayer;
import game.othello.OthelloState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Driver {

    public static void main(String[] args) throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        runMinimaxComparison();
    }

    private static void runMinimaxComparison() {
        Random rand = new Random();
        int runs = 100;
        for(int i=1; i<=5; i++) {
            for(int j=1; j<=i; j++) {
                int iCount = 0;
                int jCount = 0;
                int tieCount = 0;
                for(int run=0; run<runs; run++) {
                    boolean whiteI = rand.nextBoolean();
                    int whiteDepth = whiteI ? i : j;
                    int blackDepth = whiteI ? j : i;
                    GamePlayer white = new MinimaxPlayer("White", 'W', whiteDepth);
                    GamePlayer black = new MinimaxPlayer("Black", 'B', blackDepth);
                    GameState state = new OthelloState(white, black);

                    while(!state.getStatus().isOver()) {
                        state = state.nextPlayer().makeMove(state).getValue();
                    }

                    GameStatus status = state.getStatus();
                    if (status.getResult() == GameStatus.Result.TIE) {
                        tieCount++;
                    } else {
                        if (status.getWinner().isEmpty()) continue;
                        GamePlayer winner = status.getWinner().get();
                        if (winner == white) {
                            if(whiteI) iCount++;
                            else jCount++;
                        } else {
                            if(whiteI) jCount++;
                            else iCount++;
                        }
                    }
                }
                String combo = String.format("(i: %d, j: %d)", i, j);
                String result = String.format(": %d-%d-%d", iCount, jCount,
                        tieCount);
                System.out.println(combo + result);
            }
        }
    }

    private static void runVersusMinimax(BufferedReader reader) throws IOException {
        int depth = 6;
        GamePlayer whiteAi = new MinimaxPlayer("White", 'W', depth);
        GamePlayer black = new OthelloPlayer("Black", 'B');
        GameState state = new OthelloState(whiteAi, black);

        while(!state.getStatus().isOver()) {
            System.out.println("----------------");
            System.out.println(state + "\n");
            Map<String, ? extends GameState> nextStates = state.nextStates();
            Set<String> availableMoves = nextStates.keySet();
            System.out.println("Next to move: " + state.nextPlayer());
            System.out.println("Available moves: " + availableMoves);
            if (state.nextPlayer() == whiteAi) {
                Map.Entry<String, ? extends GameState> move =
                        whiteAi.makeMove(state);
                System.out.println(move.getKey());
                state = move.getValue();
            } else {
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
        }

        System.out.println(state.getStatus());
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
