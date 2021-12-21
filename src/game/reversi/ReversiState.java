package game.reversi;

import game.GameState;
import game.Player;

import java.util.*;

public class ReversiState implements GameState {

    private static final int BOARD_SIZE = 8;

    private final Player player;
    private final Player opposingPlayer;
    /**
     * The (x,y) coordinate system for the board increases from left to right
     * and bottom to top. The baord looks like:
     * [0,7], [1,7], ..., [7,7]
     * ...
     * [0,1], ...
     * [0,0], [1,0], ..., [7,0]
     */
    private final Board board;

    private ReversiState(Player player, Player opposingPlayer, Board board) {
        this.player = player;
        this.opposingPlayer = opposingPlayer;
        this.board = board;
    }

    public ReversiState(Player white, Player black) {
        this(white, black, new Board(white, black));
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpposingPlayer() {
        return opposingPlayer;
    }

    private Player getOtherPlayer(Player player) {
        if (player == this.player) return opposingPlayer;
        else if (player == this.opposingPlayer) return this.player;
        else throw new IllegalArgumentException(player + " is not a valid player.");
    }

    public Set<ReversiAction> getAvailableActions() {
        Set<ReversiAction> forOpposingPlayer = getAvailableActions(opposingPlayer);
        if (forOpposingPlayer.isEmpty()) {
            return getAvailableActions(player);
        }
        return forOpposingPlayer;
    }

    private Set<ReversiAction> getAvailableActions(Player player) {
        Set<ReversiAction> actions = new HashSet<>();
        for(int x=0; x<BOARD_SIZE; x++) {
            for(int y=0; y<BOARD_SIZE; y++) {
                // If a player has already put a tile here, move is invalid.
                if (board.get(x, y) != null) continue;
                Set<Coord> flips = getFlips(x, y, player);
                // If move doesn't result in flips, its invalid.
                if (flips.isEmpty()) continue;
                actions.add(getAction(flips, x, y, player));
            }
        }
        return actions;
    }

    private Set<Coord> getFlips(int x, int y, Player player) {
        List<Coord> topLeft = new ArrayList<>();
        List<Coord> top = new ArrayList<>();
        List<Coord> topRight = new ArrayList<>();
        List<Coord> right = new ArrayList<>();
        List<Coord> bottomRight = new ArrayList<>();
        List<Coord> bottom = new ArrayList<>();
        List<Coord> bottomLeft = new ArrayList<>();
        List<Coord> left = new ArrayList<>();

        for(int i=1; i<BOARD_SIZE; i++) {
            addCoord(x-i, y+i, topLeft);
            addCoord(x, y+i, top);
            addCoord(x+i, y+i, topRight);
            addCoord(x+i, y, right);
            addCoord(x+i, y-i, bottomRight);
            addCoord(x, y-i, bottom);
            addCoord(x-i, y-i, bottomLeft);
            addCoord(x-i, y, left);
        }

        Set<Coord> flips = new HashSet<>();
        flips.addAll(getFlips(topLeft, player));
        flips.addAll(getFlips(top, player));
        flips.addAll(getFlips(topRight, player));
        flips.addAll(getFlips(right, player));
        flips.addAll(getFlips(bottomRight, player));
        flips.addAll(getFlips(bottom, player));
        flips.addAll(getFlips(bottomLeft, player));
        flips.addAll(getFlips(left, player));
        return flips;
    }

    private void addCoord(int x, int y, List<Coord> coords) {
        if (board.isValid(x, y)) {
            coords.add(new Coord(x, y));
        }
    }

    private Set<Coord> getFlips(List<Coord> path, Player player) {
        Set<Coord> flips = new HashSet<>();
        for (int i=0; i<path.size(); i++) {
            Coord coord = path.get(i);
            Player owner = board.get(coord);
            if (owner == null) {
                return new HashSet<>();
            } else if (owner == player) {
                return flips;
            } else {
                if (i == path.size() - 1) {
                    return new HashSet<>();
                }
                flips.add(coord);
            }
        }
        return flips;
    }

    private ReversiAction getAction(Set<Coord> flips, int x, int y, Player player) {
        Board newBoard = this.board.copy();
        for (Coord coord: flips) {
            newBoard.board[coord.getX()][coord.getY()] = player;
        }
        newBoard.board[x][y] = player;
        String label = String.format("(%d,%d)", x, y);
        ReversiState newState = new ReversiState(player, getOtherPlayer(player), newBoard);
        return new ReversiAction(label, newState, player);
    }

    public Player getNextPlayer() {
        return null;
    }

    public double getScoreDifferential() {
        int playerCount = 0;
        int opposingPlayerCount = 0;
        for (int x=0; x<BOARD_SIZE; x++) {
            for(int y=0; y<BOARD_SIZE; y++) {
                Player owner = board.get(x, y);
                if (owner == player) playerCount++;
                if (owner == opposingPlayer) opposingPlayerCount++;
            }
        }
        return playerCount - opposingPlayerCount;
    }

    public String toJSON() {
        return board.toJSON();
    }

    public String toString() {
        return board.toString() +
                String.format(
                        "\n%s was last to move against %s",
                        player.getLabel(),
                        opposingPlayer.getLabel());
    }

    private static final class Board {
        private final static char OPEN_TILE_SYMBOL = '_';
        private final Player[][] board;

        Board(Player white, Player black) {
            board = initBoard(white, black);
        }

        private Board(Player[][] board) {
            this.board = board;
        }

        private static Player[][] initBoard(Player white, Player black) {
            Player[][] board = new Player[BOARD_SIZE][BOARD_SIZE];
            int half = BOARD_SIZE / 2;
            board[half-1][half] = white;
            board[half][half-1] = white;
            board[half-1][half-1] = black;
            board[half][half] = black;
            return board;
        }

        Player get(Coord coord) {
            return get(coord.getX(), coord.getY());
        }

        Player get(int x, int y) {
            if (isValid(x, y)) {
                return board[x][y];
            } else {
                throw new IllegalArgumentException("Invalid coordinates for game board, " + x + ", " + y);
            }
        }

        boolean isValid(Coord coord) {
            return isValid(coord.getX(), coord.getY());
        }

        boolean isValid(int x, int y) {
            return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
        }

        Board copy() {
            Player[][] copyBoard = new Player[BOARD_SIZE][BOARD_SIZE];
            for(int x=0; x<BOARD_SIZE; x++) {
                System.arraycopy(board[x], 0, copyBoard[x], 0, BOARD_SIZE);
            }
            return new Board(copyBoard);
        }

        public String toJSON() {
            StringBuilder str = new StringBuilder("{\"board\": [");
            for(int i=0; i<BOARD_SIZE; i++) {
                str.append(toJSONArray(board[i]));
                if (i != BOARD_SIZE - 1) {
                    str.append(",");
                }
            }
            return str.append("]}").toString();
        }

        private static String toJSONArray(Player[] players) {
            StringBuilder str = new StringBuilder("[");
            for(int i=0; i<players.length; i++) {
                char symbol = players[i] == null ? '_' : players[i].getSymbol();
                str.append("\"").append(symbol).append("\"");
                if (i != players.length-1) {
                    str.append(",");
                }
            }
            return str.append("]").toString();
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            for(int y=BOARD_SIZE-1; y>=0; y--) {
                str.append(y).append(" ");
                for(int x=0; x<BOARD_SIZE; x++) {
                    Player atPosition = board[x][y];
                    char symbol = atPosition == null ? OPEN_TILE_SYMBOL : atPosition.getSymbol();
                    str.append(symbol).append(" ");
                }
                str.append("\n");
            }
            str.append("  ");
            for(int i=0; i<BOARD_SIZE; i++) {
                str.append(i).append(" ");
            }
            return str.toString();
        }
    }

    private static final class Coord {
        private final int x;
        private final int y;

        Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }
}
