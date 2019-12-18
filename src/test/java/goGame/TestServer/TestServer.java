package goGame.TestServer;
import goGame.Client.GoGameClient;
import goGame.GameLogic.Game;
import goGame.Presets.TestingClient;
import goGame.Presets.TestingServer;
import goGame.Server.GoGameServer;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import static org.junit.Assert.*;

public class TestServer {
    private TestingServer testingServer;
    private Thread serverThread;
    private TestingClient testingClient;
    private Thread clientThread;
    private final int serverPort = 59090;
    private void setTestingServer(){
        testingServer = new TestingServer();
        serverThread  = new Thread(testingServer);
        serverThread.start();
    }
    @Before
    public void presetClientToTests(){
        testingClient = new TestingClient();
        clientThread = new Thread(testingClient);
        clientThread.start();
    }
    @Test
    public void testSettingServerThread(){
        setTestingServer();
        assert(serverThread.isAlive());
    }
    @Test
    public void testConnectingClient(){
        setTestingServer();
        GoGameClient.connectToServer();
        assertEquals(testingClient.getServerCommunicator().getSocket().getPort(), serverPort);
    }
    @Test
    public void testReceivingData() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        Scanner serverScanner = new Scanner(testingServer.getCurrentSocket().getInputStream());
        testingClient.getServerCommunicator().getPrintWriter().println("TestReceive");
        assertEquals(serverScanner.nextLine(), "TestReceive");
    }
    @Test
    public void testSendingData() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        PrintWriter serverPrintWriter = new PrintWriter(testingServer.getCurrentSocket().getOutputStream(), true);
        serverPrintWriter.println("TestSend");
        assertEquals(testingClient.getServerCommunicator().getScanner().nextLine(), "TestSend");
    }
    @Test
    public void testCreatingGame(){
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        assertNotNull(testingServer.getGameList().get(0));
    }
    @Test
    public void testJoiningGame(){
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("JOIN_GAME");
        GoGameClient.connectToLobby();
        assertEquals(testingServer.getGameList().size(), 0);
    }
    @Test
    public void testReceivingWrongGameOption(){
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("WRONG_GAME");
        assert(serverThread.isAlive());
    }
    @Test
    public void testCheckingGameState(){
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        assert(!testingServer.getGameList().get(0)._finished);
        GoGameServer.checkIfGamesFinished();
        assert(testingServer.getGameList().size()>0);
    }
    @Test
    public void testGettingBoardSize(){
        int response = GoGameServer.getBoardSize();
        assert(response==9 || response==13 || response==19);
        System.out.println(response);
    }
    @Test
    public void testSendingEmptyGameList(){
        setTestingServer();
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("JOIN_GAME");
        assertEquals(testingClient.getServerCommunicator().getScanner().nextLine(), "EMPTY_GAMES_LIST");
    }
    @Test
    public void testSendingGameList(){
        setTestingServer();
        GoGameClient.connectToServer();
        int i;
        for(i=0; i<4; i++){
            testingServer.getGameList().add(new Game(19));
        }
        testingClient.getServerCommunicator().getPrintWriter().println("JOIN_GAME");
        assertEquals(testingClient.getServerCommunicator().getScanner().nextLine(), "GAME_LIST");
        for(int k=0; k<i; k++){
            assertEquals(testingClient.getServerCommunicator().getScanner().nextLine(), k + ". Default Game.1/2");
        }
        assertEquals(testingClient.getServerCommunicator().getScanner().nextLine(), "NO_MORE_GAMES");
    }
}
