import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private void createAgentController(ContainerController containerController, int playerCount) {
        Object[] GMArguments = new Object[playerCount];
        for (int i = 0; i < playerCount; i++) {
            GMArguments[i] = new AID(String.valueOf(i + 1), AID.ISLOCALNAME);
        }
        try {
            AgentController GMController = containerController.createNewAgent("GM", "GameControllerAgent", GMArguments);
            GMController.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

    private void createComputerPlayers(ContainerController containerController, int playerCount) {
        List<AgentController> computerPlayersController = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            try {
                computerPlayersController.add(containerController.createNewAgent(String.valueOf(i + 1), "ComputerPlayer", null));
                computerPlayersController.get(i).start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "false");
        ContainerController containerController = runtime.createMainContainer(profile);

        int playerCount = 5;
        this.createAgentController(containerController, playerCount);
        this.createComputerPlayers(containerController, playerCount);
    }
}
