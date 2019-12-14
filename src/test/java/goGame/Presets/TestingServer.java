package goGame.Presets;

import goGame.Server.GoGameServer;
import org.junit.Test;

public class TestingServer {
    GoGameServer goGameServer;
    int size=19;
    @Test
    public void setServer(){
        goGameServer = new GoGameServer();
        goGameServer.setBoardSize(size);
        GoGameServer.presetGameList();
        try{
            GoGameServer.initializeServer();
            GoGameServer.listenForClients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
