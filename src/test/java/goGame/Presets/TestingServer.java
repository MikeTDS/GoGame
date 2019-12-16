package goGame.Presets;

import goGame.Server.GoGameServer;
import org.junit.Test;

import java.net.Socket;

public class TestingServer implements Runnable{
    GoGameServer goGameServer;
    @Override
    public void run() {
        try {
            setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setup() throws Exception {
        goGameServer = new GoGameServer();
        goGameServer.setBoardSize(19);
        GoGameServer.presetGameList();
        GoGameServer.initializeServer();
        GoGameServer.listenForClients();
    }
    public int getBoardSize(){
        return goGameServer.getPreviouslySetBoardSize();
    }
    public Socket getCurrentSocket(){
        return goGameServer.getCurrentSocket();
    }
}
