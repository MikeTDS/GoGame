package goGame.ClientTests;

import goGame.Client.ServerCommunicator;
import goGame.Presets.TestingServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class TestServerCommunicator extends TestingServer {
    ServerCommunicator serverCommunicator;
    @Before
    public void presetServerCommunicator(){
        setServer();
        serverCommunicator = ServerCommunicator.getInstance();
    }
    @Test
    public void testSingleton(){
        ServerCommunicator serverCommunicatorToTest = ServerCommunicator.getInstance();
        assertSame(serverCommunicatorToTest, serverCommunicator);
    }
    @Test
    public void testConnectingToServer(){
        try {
            serverCommunicator.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(serverCommunicator.getSocket());
    }
}
