package main;

import ai.*;
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

        MinimaxPlayer minimax = new MinimaxPlayer("Minimax", 'M', 4);
        MinimaxPlayer minimaxPruned = new MinimaxPlayer("MinimaxPruned", 'U', 4);
        MinimaxPlayer minimaxParallel = new MinimaxParallelPlayer(
                "MinimaxParallel", 'A', 4, 3);
        MinimaxPlayer minimaxPrunedParallel = new MinimaxPrunedParallelPlayer(
                "MinimaxPrunedParallel", 'Z', 8, 3);
        MonteCarloNaivePlayer monteCarloNaivePlayer =
                new MonteCarloNaivePlayer("MonteCarloNaive"
                , 'N', 10000);
        MonteCarloPlayer monteCarloPlayer = new MonteCarloPlayer(
                "MonteCarlo", 'C', 5000);
        // runVersusPlayer(reader, monteCarloPlayer);
        compare(monteCarloPlayer, minimaxPrunedParallel, 10);
    }

    private static void compareMinimax() {
        for(int high=1; high<=8; high++) {
            for(int low=1; low<=high; low++) {
                System.out.println("High: " + high + ", Low: " + low);
                compare(new MinimaxPrunedParallelPlayer("High", 'H', high, 3),
                        new MinimaxPrunedParallelPlayer("Low", 'L', low, 3),
                        50);
                System.out.println();
            }
        }
    }

    private static void compare(GamePlayer a, GamePlayer b, int runs) {
        Random rand = new Random();
        int aWins = 0;
        int bWins = 0;
        long aTime = 0;
        long bTime = 0;
        for(int i=1; i<=runs; i++) {
            boolean aIsWhite = rand.nextBoolean();
            GamePlayer white = aIsWhite ? a : b;
            GamePlayer black = aIsWhite ? b : a;

            GameState state = new OthelloState(white, black);
            while(!state.getStatus().isOver()) {
                GamePlayer nextPlayer = state.nextPlayer();
                long startTime = System.currentTimeMillis();
                state = nextPlayer.makeMove(state).getValue();
                long totalTime = System.currentTimeMillis() - startTime;
                if (nextPlayer.equals(a)) {
                    aTime += totalTime;
                } else {
                    bTime += totalTime;
                }
            }

            GameStatus status = state.getStatus();
            if (status.getResult() == GameStatus.Result.WON) {
                GamePlayer winner = status.getWinner().get();
                System.out.println(winner);
                if (winner.equals(white)) {
                    if (aIsWhite) aWins++;
                    else bWins++;
                } else {
                    if (aIsWhite) bWins++;
                    else aWins++;
                }
            }
        }
        double aTimeDouble = ((double)aTime)/1000;
        double bTimeDouble = ((double)bTime)/1000;
        String aOutcome = a.getLabel() + ": " + aWins + " in " + aTimeDouble +
        " seconds.";
        String bOutcome =
                b.getLabel() + ": " + bWins + " in " + bTimeDouble + " " +
                        "seconds.";
        System.out.println(aOutcome + ", " + bOutcome);
    }

    private static void runMinimaxParallelComparison() {
        Random rand = new Random();
        int runs = 100;
        int poolSize = 3;
        for(int i=1; i<=5; i++) {
            for(int j=1; j<=i; j++) {
                int iCount = 0;
                int jCount = 0;
                int tieCount = 0;
                for(int run=0; run<runs; run++) {
                    boolean whiteI = rand.nextBoolean();
                    int whiteDepth = whiteI ? i : j;
                    int blackDepth = whiteI ? j : i;
                    GamePlayer white = new MinimaxParallelPlayer("White", 'W',
                            whiteDepth, poolSize);
                    GamePlayer black = new MinimaxParallelPlayer("Black", 'B'
                            , blackDepth, poolSize);
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

    private static void runVersusPlayer(BufferedReader reader,
                                        GamePlayer player) throws IOException {
        int depth = 8;
        int poolSize = 3;
        GamePlayer whiteAi = player;
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
