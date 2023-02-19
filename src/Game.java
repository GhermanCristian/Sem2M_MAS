import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Game {

    private void createAgentController(ContainerController containerController, int totalPlayerCount) {
        Object[] GMArguments = new Object[totalPlayerCount];
        for (int i = 0; i < totalPlayerCount; i++) {
            GMArguments[i] = new AID(String.valueOf(i + 1), AID.ISLOCALNAME);
        }
        try {
            AgentController GMController = containerController.createNewAgent("GM", "GameControllerAgent", GMArguments);
            GMController.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

    private void createHumanPlayers(ContainerController containerController, int humanPlayerCount) {
        for (int i = 0; i < humanPlayerCount; i++) {
            try {
                AgentController agent = containerController.createNewAgent(String.valueOf(i + 1), "HumanPlayer", null);
                agent.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createComputerPlayers(ContainerController containerController, int totalPlayerCount, int humanPlayerCount) {
        for (int i = 0; i < totalPlayerCount - humanPlayerCount; i++) {
            try {
                AgentController agent = containerController.createNewAgent(String.valueOf(humanPlayerCount + i + 1), "ComputerPlayer", null);
                agent.start();
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

        int totalPlayerCount = 5;
        int humanPlayersCount = 2;
        this.createAgentController(containerController, totalPlayerCount);
        this.createHumanPlayers(containerController, humanPlayersCount);
        this.createComputerPlayers(containerController, totalPlayerCount, humanPlayersCount);
    }
}
