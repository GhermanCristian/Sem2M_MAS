import java.util.List;
import java.util.Random;

public class ComputerPlayer extends PlayerAgent {
    protected Coordinates makeMove(Board board) {
        final int[] DIRECTIONS_X = {-1, -1, 0, 1, 1, 1, 0, -1};
        final int[] DIRECTIONS_Y = {0, 1, 1, 1, 0, -1, -1, -1};
        final int DIRECTION_COUNT = 8;

        List<Coordinates> formerMoves = board.getPositionsWithValue(Integer.parseInt(getLocalName()));
        List<Coordinates> emptyPositions = board.getEmptyPositions();
        if (formerMoves.isEmpty()) {
            // first move => wherever available
            return emptyPositions.get(new Random().nextInt(emptyPositions.size()));
        }

        for (Coordinates formerMove : formerMoves) {
            // for a previous move, attempt to also move on one of its neighbours, starting from a random neighbour
            // if no positions are available, go to another previous move
            int randomDirection = new Random().nextInt(DIRECTION_COUNT);
            int x = formerMove.x + DIRECTIONS_X[randomDirection];
            int y = formerMove.y + DIRECTIONS_Y[randomDirection];
            int tries = 0;
            while ((!Board.inside(x, y) || board.get(x, y) != Board.EMPTY_POSITION) && tries < DIRECTION_COUNT) {
                randomDirection = (randomDirection + tries) % DIRECTION_COUNT;
                x = formerMove.x + DIRECTIONS_X[randomDirection];
                y = formerMove.y + DIRECTIONS_Y[randomDirection];
                tries++;
            }
            if (tries < DIRECTION_COUNT) {
                return new Coordinates(x, y);
            }
        }
        return emptyPositions.get(new Random().nextInt(emptyPositions.size()));
    }
}
