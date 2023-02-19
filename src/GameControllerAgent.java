import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameControllerAgent extends Agent {
    private GameState gameState;
    private ArrayList<AID> computerPlayers;
    private int currentPlayerIndex;

    private void setPlayers() {
        Object[] arguments = getArguments();
        this.computerPlayers = new ArrayList<>();
        if (arguments != null && arguments.length > 0) {
            for (Object argument : arguments) {
                this.computerPlayers.add((AID) argument);
            }
            this.gameState.totalPlayerCount = arguments.length;
        }
    }

    private void increasePlayerIndex() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.gameState.totalPlayerCount;
    }

    protected void setup() {
        this.gameState = new GameState();
        this.setPlayers();
        this.currentPlayerIndex = new Random().nextInt(this.gameState.totalPlayerCount);
        addBehaviour(new SendGameStartToAllPlayersBehaviour());
        addBehaviour(new SendGameStateToOnePlayerBehaviour(this.currentPlayerIndex));
    }

    @Override
    protected void takeDown() {
        doDelete();
        super.takeDown();
    }

    private class SendGameStartToAllPlayersBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage gameStartMessage = new ACLMessage(ACLMessage.PROPOSE);
            computerPlayers.forEach(gameStartMessage::addReceiver);
            gameStartMessage.setContent("START");
            myAgent.send(gameStartMessage);
        }
    }

    private class SendGameEndToAllPlayersBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage gameStartMessage = new ACLMessage(ACLMessage.PROPOSE);
            computerPlayers.forEach(gameStartMessage::addReceiver);
            gameStartMessage.setContent("END");
            myAgent.send(gameStartMessage);
        }
    }

    private class SendGameStateToOnePlayerBehaviour extends OneShotBehaviour {
        private final int playerIndex;

        public SendGameStateToOnePlayerBehaviour(int playerIndex) {
            this.playerIndex = playerIndex;
        }

        private void sendMessage() {
            ACLMessage gameStateMessage = new ACLMessage(ACLMessage.INFORM);
            gameStateMessage.addReceiver(computerPlayers.get(playerIndex));
            try {
                gameStateMessage.setContentObject(gameState);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            myAgent.send(gameStateMessage);
        }

        @Override
        public void action() {
            this.sendMessage();
            if (!gameState.isFinished()) {
                addBehaviour(new ReceiveCoordinatesFromPlayerBehaviour(currentPlayerIndex));
            } else {
                addBehaviour(new SendGameEndToAllPlayersBehaviour());
            }
        }
    }

    private class ReceiveCoordinatesFromPlayerBehaviour extends OneShotBehaviour {
        private final int playerIndex;

        public ReceiveCoordinatesFromPlayerBehaviour(int playerIndex) {
            this.playerIndex = playerIndex;
        }

        private void receiveMessage() {
            ACLMessage message = myAgent.blockingReceive();
            if (message != null) {
                try {
                    if (!computerPlayers.get(this.playerIndex).getLocalName().equals(message.getSender().getLocalName())) {
                        throw new RuntimeException("Invalid player");
                    }
                    Coordinates currentCoordinates = (Coordinates) message.getContentObject();
                    gameState.board.set(currentCoordinates.x, currentCoordinates.y, Integer.parseInt(computerPlayers.get(this.playerIndex).getLocalName()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void action() {
            this.receiveMessage();
            if (!gameState.isFinished()) {
                if (gameState.board.isFull()) {
                    System.out.println("DRAW");
                    addBehaviour(new SendGameEndToAllPlayersBehaviour());
                    return;
                }
                increasePlayerIndex();
                addBehaviour(new SendGameStateToOnePlayerBehaviour(currentPlayerIndex));
            } else {
                System.out.printf("Player %s won\n", computerPlayers.get(currentPlayerIndex).getLocalName());
                gameState.board.print();
                addBehaviour(new SendGameEndToAllPlayersBehaviour());
            }
        }
    }
}
