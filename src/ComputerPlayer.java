import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends Agent {
    private AID gameControllerAID;

    private Coordinates makeMove(Board board) {
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

    protected void setup() {
        addBehaviour(new ReceiveGameStartMessageFromGameController());
        addBehaviour(new ReceiveGameStateMessageFromGameController());
    }

    @Override
    protected void takeDown() {
        doDelete();
        super.takeDown();
    }

    private class ReceiveGameStartMessageFromGameController extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage message = myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
            if (message != null) {
                gameControllerAID = message.getSender();
                System.out.println(message.getContent() + " " + getLocalName());
            }
        }
    }

    private class ReceiveGameEndMessageFromGameController extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage message = myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
            if (message != null) {
                gameControllerAID = message.getSender();
                System.out.println(message.getContent() + " " + getLocalName());
                doDelete();
            }
        }
    }

    private class ReceiveGameStateMessageFromGameController extends OneShotBehaviour {
        private void sendMessage(Coordinates currentCoordinates) {
            ACLMessage gameStateMessage = new ACLMessage(ACLMessage.INFORM);
            gameStateMessage.addReceiver(gameControllerAID);
            try {
                gameStateMessage.setContentObject(currentCoordinates);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            myAgent.send(gameStateMessage);
        }

        @Override
        public void action() {
            ACLMessage message = myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (message != null) {
                try {
                    GameState gameState = (GameState) message.getContentObject();
                    Coordinates currentCoordinates = makeMove(gameState.board);
                    System.out.printf("%s made move %s\n", getLocalName(), currentCoordinates.toString());
                    this.sendMessage(currentCoordinates);
                    addBehaviour(new ReceiveGameStateMessageFromGameController());
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
