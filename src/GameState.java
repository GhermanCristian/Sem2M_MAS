import java.io.Serializable;

public class GameState implements Serializable {
    public final static int DEFAULT_PLAYER_COUNT = 3;
    public Board board;
    public int totalPlayerCount = DEFAULT_PLAYER_COUNT;

    public GameState() {
        this.board = new Board();
    }

    private boolean checkFromPositionOnDirection(int i, int j, int currentValue, final int DIRECTION_X, final int DIRECTION_Y) {
        int consecutive = 1;
        for (int k = 1; k < 3; k++) {
            if (Board.inside(i + k * DIRECTION_X, j + k * DIRECTION_Y) && board.get(i + k * DIRECTION_X, j + k * DIRECTION_Y) == currentValue) {
                consecutive++;
            }
        }
        return consecutive == 3;
    }

    private boolean checkFromPosition(int i, int j, int currentValue) {
        final int[] DIRECTIONS_X = {1, 0, 1, 1};
        final int[] DIRECTIONS_Y = {0, 1, 1, -1};

        for (int k = 0; k < DIRECTIONS_X.length; k++) {
            boolean tempResult = this.checkFromPositionOnDirection(i, j, currentValue, DIRECTIONS_X[k], DIRECTIONS_Y[k]);
            if (tempResult) {
                return true;
            }
        }
        return false;
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
