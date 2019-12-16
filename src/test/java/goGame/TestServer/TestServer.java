package goGame.TestServer;
import goGame.Presets.TestingServer;
import org.junit.Test;


public class TestServer {
    private TestingServer testingServer;
    private Thread serverThread;
    private void setTestingServer(){
        testingServer = new TestingServer();
        serverThread  = new Thread(testingServer);
        serverThread.start();
    }
    @Test
    public void testSettingServerThread(){
        setTestingServer();
        assert(serverThread.isAlive());
    }
    @Test
    public void testConnectingClient(){

    }
}
