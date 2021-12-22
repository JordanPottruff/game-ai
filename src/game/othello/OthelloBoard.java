package game.othello;

import game.GamePlayer;

import java.util.*;

public class OthelloBoard {
    public static final int BOARD_SIZE = 8;

    private static final byte EMPTY_BYTE = 0;
    private static final byte WHITE_BYTE = 1;
    private static final byte BLACK_BYTE = 2;

    private final byte[] board;
    private final GamePlayer white;
    private final GamePlayer black;

    private OthelloBoard(GamePlayer white, GamePlayer black,
                         byte[] board) {
        this.white = white;
        this.black = black;
        this.board = board;
    }

    public OthelloBoard(GamePlayer white, GamePlayer black) {
        this.white = white;
        this.black = black;
        this.board = new byte[BOARD_SIZE * BOARD_SIZE];
        setTile(3, 3, white);
        setTile(3, 4, black);
        setTile(4, 3, black);
        setTile(4, 4, white);
    }

    /**
     * Converts (x,y) coordinate for a move into a label. The x-value is
     * converted into the appropriate letter, starting with "a" at 0. The
     * y-value is incremented to be 1-based. E.g. (3,5) is converted to "d6".
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @return the label for the (x,y) coordinate of the move.
     */
    public static String toMoveLabel(int x, int y) {
        return toXLabel(x) + (y + 1);
    }

    private static String toXLabel(int x) {
        return Character.toString((char) (x+97));
    }

    private static int toPosition(int x, int y) {
        return x*BOARD_SIZE+y;
    }

    private void setTile(int x, int y, GamePlayer player) {
        byte value = 0;
        if (player == white) value = WHITE_BYTE;
        if (player == black) value = BLACK_BYTE;
        board[toPosition(x, y)] = value;
    }

    public GamePlayer getTile(int x, int y) {
        byte raw = board[toPosition(x, y)];
        return toPlayer(raw);
    }

    public Map<String, OthelloBoard> getNextBoards(GamePlayer forPlayer) {
        Map<String, OthelloBoard> nextBoards = new HashMap<>();
        for(int x=0; x<BOARD_SIZE; x++) {
            for(int y=0; y<BOARD_SIZE; y++) {
                GamePlayer owner = getTile(x, y);
                if (owner == null) {
                    Optional<OthelloBoard> newBoard =
                            checkOpenSquare(x, y, forPlayer);
                    if (newBoard.isPresent()) {
                        String label = toMoveLabel(x, y);
                        nextBoards.put(label, newBoard.get());
                    }
                }
            }
        }
        return nextBoards;
    }

    private Optional<OthelloBoard> checkOpenSquare(
            int x, int y, GamePlayer forPlayer) {
        Builder copy = toBuilder();

        List<Optional<Builder>> sequences = List.of(
                checkSequence(x, y, 0, 1, copy, forPlayer), // N
                checkSequence(x, y, 1, 1, copy, forPlayer), // NE
                checkSequence(x, y, 1, 0, copy, forPlayer), // E
                checkSequence(x, y, 1, -1, copy, forPlayer), // SE
                checkSequence(x, y, 0, -1, copy, forPlayer), // S
                checkSequence(x, y, -1, -1, copy, forPlayer), // SW
                checkSequence(x, y, -1, 0, copy, forPlayer), // W
                checkSequence(x, y, -1, 1, copy, forPlayer) // NW
        );

        boolean hasChanged = false;
        for(Optional<Builder> seq: sequences) {
            if (seq.isPresent()) {
                hasChanged = true;
                break;
            }
        }
        if (hasChanged) {
            return Optional.of(copy.build());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Builder> checkSequence(
            int x, int y, int dx, int dy,
            OthelloBoard.Builder board, GamePlayer forPlayer) {

        int count = 0;
        for(int i=1; i<BOARD_SIZE; i++) {
            int nx = x + dx*i;
            int ny = y + dy*i;
            if (isValid(nx, ny)) {
                GamePlayer owner = getTile(nx, ny);
                if (owner == null) {
                    count = 0;
                    break;
                } else if (owner == forPlayer) {
                    break;
                } else {
                    count++;
                }
            } else {
                count = 0;
                break;
            }
        }
        if (count == 0) return Optional.empty();
        for(int i=0; i<=count; i++) {
            int nx = x + dx*i;
            int ny = y + dy*i;
            board.setTile(nx, ny, forPlayer);
        }
        return Optional.of(board);
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    private GamePlayer toPlayer(byte raw) {
        if (raw == WHITE_BYTE) return white;
        if (raw == BLACK_BYTE) return black;
        if (raw == EMPTY_BYTE) return null;
        throw new IllegalArgumentException("Illegal byte in othello board: " + raw);
    }

    public double getScore(GamePlayer player) {
        if (player != white && player != black) {
            String msg = "Player " + player + "does not exist on the board.";
            throw new IllegalArgumentException(msg);
        }
        byte playerByte = player == white ? WHITE_BYTE : BLACK_BYTE;
        int score = 0;
        for(byte raw: board) {
            if (raw == playerByte) {
                score++;
            }
        }
        return score;
    }

    public String toJson() {
        StringBuilder str = new StringBuilder("{\"board\":[");
        for(int x=0; x<BOARD_SIZE; x++) {
            str.append("[");
            for(int y=0; y<BOARD_SIZE; y++) {
                GamePlayer owner = getTile(x, y);
                char symbol = owner == null ? '_' : owner.getSymbol();
                str.append("\"").append(symbol).append("\"");
                if (y != BOARD_SIZE-1) {
                    str.append(",");
                }
            }
            str.append("]");
        }
        str.append("]}");
        return str.toString();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int y=BOARD_SIZE-1; y>=0; y--) {
            str.append(y+1);
            for(int x=0; x<BOARD_SIZE; x++) {
                GamePlayer owner = getTile(x, y);
                char symbol = owner == null ? '_' : owner.getSymbol();
                str.append(" ").append(symbol);
            }
            str.append("\n");
        }
        str.append(" ");
        for(int x=0; x<BOARD_SIZE; x++) {
            str.append(" ").append(toXLabel(x));
        }
        return str.toString();
    }

    public Builder toBuilder() {
        byte[] boardCopy = Arrays.copyOf(board, board.length);
        return new Builder()
                .setWhite(white)
                .setBlack(black)
                .setBoard(boardCopy);
    }

    public static class Builder {
        private GamePlayer white = null;
        private GamePlayer black = null;
        private byte[] board = new byte[BOARD_SIZE*BOARD_SIZE];

        public Builder setWhite(GamePlayer player) {
            white = player;
            return this;
        }

        public Builder setBlack(GamePlayer player) {
            black = player;
            return this;
        }

        public Builder setTile(int x, int y, GamePlayer player) {
            board[toPosition(x, y)] = toByte(player);
            return this;
        }

        public Builder setBoard(byte[] board) {
            if (board.length != BOARD_SIZE * BOARD_SIZE) {
                throw new IllegalArgumentException("Board is not appropriate " +
                        "size");
            }
            this.board = board;
            return this;
        }

        private byte toByte(GamePlayer player) {
            if (white != player && black != player) {
                throw new IllegalArgumentException("Player " + player + " " +
                        "does not belong to the board.");
            }
            return player == white ? WHITE_BYTE : BLACK_BYTE;
        }

        public OthelloBoard build() {
            return new OthelloBoard(white, black, board);
        }
    }
}
