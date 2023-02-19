import java.io.Serializable;

public class GameState implements Serializable {
    public final static int DEFAULT_PLAYER_COUNT = 3;
    public Board board;
    public int totalPlayerCount = DEFAULT_PLAYER_COUNT;

    public GameState() {
        this.board = new Board();
    }

    private boolean checkFromPosition(int i, int j, int currentValue) {
        int consecutive = 1;
        // vertical line below
        for (int k = 1; k < 3; k++) {
            if (Board.inside(i + k, j) && board.get(i + k, j) == currentValue) {
                consecutive++;
            }
        }
        if (consecutive == 3) {
            return true;
        }

        // horizontal line to the right
        consecutive = 1;
        for (int k = 1; k < 3; k++) {
            if (Board.inside(i, j + k) && board.get(i, j + k) == currentValue) {
                consecutive++;
            }
        }
        if (consecutive == 3) {
            return true;
        }

        // diagonal towards bottom-right
        consecutive = 1;
        for (int k = 1; k < 3; k++) {
            if (Board.inside(i + k, j + k) && board.get(i + k, j + k) == currentValue) {
                consecutive++;
            }
        }
        if (consecutive == 3) {
            return true;
        }

        // diagonal towards bottom-left
        consecutive = 1;
        for (int k = 1; k < 3; k++) {
            if (Board.inside(i + k, j - k) && board.get(i + k, j - k) == currentValue) {
                consecutive++;
            }
        }
        return consecutive == 3;
    }

    public boolean isFinished() {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                int currentValue = board.get(i, j);
                if (currentValue != Board.EMPTY_POSITION) {
                    if (this.checkFromPosition(i, j, currentValue)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
