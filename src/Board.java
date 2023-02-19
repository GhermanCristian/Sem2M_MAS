import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements Serializable {
    public static final int BOARD_SIZE = 8;
    public static final int EMPTY_POSITION = -1;
    private final int[][] board;

    public Board() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            Arrays.fill(this.board[row], EMPTY_POSITION);
        }
    }

    public static boolean inside(int i, int j) {
        return (i >= 0 && i < Board.BOARD_SIZE && j >= 0 && j < Board.BOARD_SIZE);
    }

    public int get(int x, int y) {
        if (0 <= x && x < BOARD_SIZE && 0 <= y && y < BOARD_SIZE) {
            return this.board[x][y];
        }
        throw new IndexOutOfBoundsException("Invalid position");
    }

    public void set(int x, int y, int val) throws Exception {
        if (0 <= x && x < BOARD_SIZE && 0 <= y && y < BOARD_SIZE) {
            if (this.board[x][y] == EMPTY_POSITION) {
                this.board[x][y] = val;
            } else {
                throw new Exception("Position is already occupied");
            }
        } else {
            throw new IndexOutOfBoundsException("Invalid position");
        }
    }

    public void print() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] == EMPTY_POSITION) {
                    System.out.print("__ ");
                } else {
                    System.out.printf("%02d ", this.board[i][j]);
                }
            }
            System.out.println();
        }
    }

    public List<Coordinates> getPositionsWithValue(int value) {
        List<Coordinates> emptyPositions = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == value) {
                    emptyPositions.add(new Coordinates(i, j));
                }
            }
        }
        return emptyPositions;
    }

    public List<Coordinates> getEmptyPositions() {
        return this.getPositionsWithValue(EMPTY_POSITION);
    }

    public boolean isFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_POSITION) {
                    return false;
                }
            }
        }
        return true;
    }
}
