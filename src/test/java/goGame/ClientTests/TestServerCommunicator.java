package goGame.ClientTests;

import goGame.Client.ServerCommunicator;
import goGame.GUIcomponents.ScoreBoard.ExitButton;
import goGame.Presets.TestingServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestServerCommunicator{
    private ServerCommunicator serverCommunicator;
    private TestingServer testingServer;
    private String serverAddress="localhost";
    private int port = 59090;
    @Before
    public void presetServerCommunicator(){
        serverCommunicator = ServerCommunicator.getInstance(serverAddress, port);
    }
    @Test
    public void testConnectingToServer(){
        setServer();
        try {
            serverCommunicator.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(serverCommunicator.getSocket());
        assertNotNull(serverCommunicator.getScanner());
        assertNotNull(serverCommunicator.getPrintWriter());
        assertEquals(serverCommunicator.getSocket().getPort(), 59090);
    }
    @Test
    public void testSingleton(){
        ServerCommunicator serverCommunicatorToTest = ServerCommunicator.getInstance();
        assertSame(serverCommunicatorToTest, serverCommunicator);
    }
    @Test
    public void testConverter(){
        String realInt = "7";
        int answer = serverCommunicator.convertToInt(realInt);
        assertEquals(answer, 7);
    }
    @Test(expected = NumberFormatException.class)
    public void testConverterError(){
        String fakeInt = "seven";
        serverCommunicator.convertToInt(fakeInt);
    }
    private void setServer(){
        testingServer = new TestingServer();
        Thread serverThread = new Thread(testingServer);
        serverThread.start();
    }
}
