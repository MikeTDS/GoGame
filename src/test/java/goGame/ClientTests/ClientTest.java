package goGame.ClientTests;
import goGame.Client.GoGameClient;
import goGame.Presets.TestingServer;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.PrintWriter;
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
        assertNotNull(testingServer.getCurrentSocket());
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
    @Test
    public void testConverter(){
        assertEquals(0, GoGameClient.convertToInt("seven"));
        assertEquals(7, GoGameClient.convertToInt("7"));
    }
    @Test
    public void testPlaying() throws Exception {
        setTestingServer();
        GoGameClient.connectToServer();
        goGameClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        assert(serverThread.isAlive());
        assert(GoGameClient.getGuiFrame().isVisible());
        GoGameClient.play();
    }

    @Test(expected = NullPointerException.class)
    public void testSurrender() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("SURRENDER");
    }
    @Test(expected = NullPointerException.class)
    public void testExit() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("NORMAL_EXIT");
    }
    @Test(expected = NullPointerException.class)
    public void testSurrenderWin() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("SURRENDER_WIN");
    }
    @Test
    public void testPassesSpam() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("FIRST_PASS");
        GoGameClient.performActionFromResponse("QUIT_PASS");
        GoGameClient.performActionFromResponse("ERROR_PASS");
        GoGameClient.performActionFromResponse("OPPONENT_PASS");
        assert(serverThread.isAlive());
    }
    @Test(expected = NullPointerException.class)
    public void testWinnerState() throws IOException {
        setTestingServer();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("WINNER");
        assert(serverThread.isAlive());
    }
    @Test
    public void testCommunicationWithServer() throws Exception {
        setTestingServer();
        GoGameClient.connectToServer();
        assertNotNull(testingServer.getCurrentSocket());
        PrintWriter currentSocketPW = new PrintWriter(testingServer.getCurrentSocket().getOutputStream(), true);
        goGameClient.getServerCommunicator().getPrintWriter().println("NEW_GAME"); // Client -> Server
        GoGameClient.initializeGame();
        currentSocketPW.println("WINNER"); //Server -> Client
        GoGameClient.play();
    }
    @Test
    public void testSendingWrongMessageToClient() throws Exception {
        setTestingServer();
        GoGameClient.connectToServer();
        assertNotNull(testingServer.getCurrentSocket());
        PrintWriter currentSocketPW = new PrintWriter(testingServer.getCurrentSocket().getOutputStream(), true);
        goGameClient.getServerCommunicator().getPrintWriter().println("NEW_GAME"); // Client -> Server
        GoGameClient.initializeGame();
        for(int i=0; i<10; i++){
            currentSocketPW.println("CRASH_TEST " + i); //Server -> Client
        }
        assert(Thread.currentThread().isAlive());
        GoGameClient.play();
    }

    @Test
    public void testSetupClient(){
        setTestingServer();
        GoGameClient.connectToServer();
        GoGameClient.initializeGame();
        GoGameClient.setupClient();
        assertEquals(GoGameClient.getColor(), "Black");
        assertEquals(GoGameClient.getOpponentColor(), "White");
    }
    private void setTestingServer(){
        testingServer = new TestingServer();
        serverThread = new Thread(testingServer);
        serverThread.start();
    }
}
