package goGame.ClientTests;
import goGame.Client.GoGameClient;
import goGame.Presets.TestingClient;
import goGame.Presets.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.PrintWriter;
import static org.junit.Assert.*;

public class ClientTest {
    private TestingClient testingClient;
    private Thread clientThread;
    private TestingServer testingServer;
    private Thread serverThread;

    @Before
    public void setTestingServer() throws InterruptedException {
        Thread.sleep(100);
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

    @After
    public void closeSocket() throws IOException {
        testingServer.closeSocket();
        testingServer = null;
        testingClient = null;
    }

    @Test
    public void testConnectingToServer(){
        GoGameClient.connectToServer();
        assertNotNull(testingClient.getServerCommunicator().getSocket());
        assertNotNull(testingServer.getCurrentSocket());
    }
    @Test
    public void testGettingSizeOfBoardFromServer(){
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        assertEquals(GoGameClient.getResponse(), Integer.toString(testingServer.getBoardSize()));
    }
    @Test
    public void testCreatingGame(){
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        assert(serverThread.isAlive());
        assertNotNull(GoGameClient.getGuiFrame());
        assert(GoGameClient.getGuiFrame().isVisible());
        testingClient.disposeFrame();
    }

    @Test
    public void testEnteringLobby(){
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("JOIN_GAME");
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
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        assert(serverThread.isAlive());
        assert(GoGameClient.getGuiFrame().isVisible());
        GoGameClient.play();
    }

    @Test(expected = NullPointerException.class)
    public void testSurrender() throws IOException, NoSuchFieldException, IllegalAccessException {
        testingClient.resetFrame();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("SURRENDER");
    }
    @Test(expected = NullPointerException.class)
    public void testExit() throws IOException, NoSuchFieldException, IllegalAccessException {
        testingClient.resetFrame();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("NORMAL_EXIT");
    }
    @Test(expected = NullPointerException.class)
    public void testSurrenderWin() throws IOException, NoSuchFieldException, IllegalAccessException {
        testingClient.resetFrame();
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("SURRENDER_WIN");
    }
    @Test
    public void testPassesSpam() throws IOException {
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("FIRST_PASS");
        GoGameClient.performActionFromResponse("QUIT_PASS");
        GoGameClient.performActionFromResponse("ERROR_PASS");
        GoGameClient.performActionFromResponse("OPPONENT_PASS");
        assert(serverThread.isAlive());
    }
    @Test(expected = NullPointerException.class)
    public void testWinnerState() throws IOException {
        GoGameClient.connectToServer();
        GoGameClient.performActionFromResponse("WINNER");
        assert(serverThread.isAlive());
    }
    @Test
    public void testCommunicationWithServer() throws Exception {
        GoGameClient.connectToServer();
        assertNotNull(testingServer.getCurrentSocket());
        PrintWriter currentSocketPW = new PrintWriter(testingServer.getCurrentSocket().getOutputStream(), true);
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME"); // Client -> Server
        GoGameClient.initializeGame();
        testingClient.disposeFrame();
        currentSocketPW.println("WINNER"); //Server -> Client
        GoGameClient.play();
    }
    @Test
    public void testSendingWrongMessageToClient() throws Exception {
        GoGameClient.connectToServer();
        assertNotNull(testingServer.getCurrentSocket());
        PrintWriter currentSocketPW = new PrintWriter(testingServer.getCurrentSocket().getOutputStream(), true);
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME"); // Client -> Server
        GoGameClient.initializeGame();
        for(int i=0; i<10; i++){
            currentSocketPW.println("CRASH_TEST " + i); //Server -> Client
        }
        assert(Thread.currentThread().isAlive());
        testingClient.disposeFrame();
    }

    @Test
    public void testSetupClient(){
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        testingClient.disposeFrame();
        GoGameClient.setupClient();
        assertEquals(GoGameClient.getColor(), "Black");
        assertEquals(GoGameClient.getOpponentColor(), "White");
    }
}
