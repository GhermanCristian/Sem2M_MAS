import java.util.Scanner;

public class HumanPlayer extends PlayerAgent {
    protected Coordinates makeMove(Board board) {
        board.print();
        System.out.printf("Player %s, enter coordinates\n", getLocalName());
        while (true) {
            Scanner scanner = new Scanner(System.in); // do not close this
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            if (Board.inside(x, y) && board.get(x, y) == Board.EMPTY_POSITION) {
                return new Coordinates(x, y);
            }
            System.out.println("Invalid move. Try again");
        }
    }
}
