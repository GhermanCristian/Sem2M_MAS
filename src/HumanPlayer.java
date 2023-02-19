import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.Scanner;

public class HumanPlayer extends Agent {
    private AID gameControllerAID;

    private Coordinates makeMove(Board board) {
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
                    this.sendMessage(currentCoordinates);
                    addBehaviour(new ReceiveGameStateMessageFromGameController());
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
