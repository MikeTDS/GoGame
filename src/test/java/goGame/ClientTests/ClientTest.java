package goGame.ClientTests;

import goGame.Client.GoGameClient;
import goGame.Presets.TestingServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTest {
    GoGameClient goGameClient;
    private TestingServer testingServer;
    private Thread serverThread;
    @Before
    public void presetClient(){
        goGameClient = new GoGameClient();
    }
    @Test
    public void testConnectingToServer(){
        setTestingServer();
        GoGameClient.connectToServer();
        assertNotNull(goGameClient.getServerCommunicator().getSocket());
    }
    @Test
    public void testGettingSizeOfBoardFromServer(){
        setTestingServer();
        GoGameClient.connectToServer();
        goGameClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        assertEquals(GoGameClient.getResponse(), Integer.toString(testingServer.getBoardSize()));
    }
    @Test
    public void testCreatingGame(){
        setTestingServer();
        GoGameClient.connectToServer();
        goGameClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        assert(serverThread.isAlive());
        assertNotNull(GoGameClient.getGuiFrame());
        assert(GoGameClient.getGuiFrame().isVisible());
    }
    @Test
    public void testCrashingNewGame(){
        setTestingServer();
        GoGameClient.connectToServer();
        goGameClient.getServerCommunicator().getPrintWriter().println("CRASH_TEST");
        assert(serverThread.isAlive());
        assertNull(GoGameClient.getGuiFrame());
    }
    @Test
    public void testEnteringLobby(){
        setTestingServer();
        GoGameClient.connectToServer();
        goGameClient.getServerCommunicator().getPrintWriter().println("JOIN_GAME");
        GoGameClient.connectToLobby();
        assertNotNull(GoGameClient.getMenuFrame());
        assert(GoGameClient.getMenuFrame().isVisible());
    }
    //dopisac testy: converttoint, play, performaction, setupclient
    private void setTestingServer(){
        testingServer = new TestingServer();
        serverThread = new Thread(testingServer);
        serverThread.start();
    }
}
