package goGame.Presets;

import goGame.GameLogic.Game;
import goGame.Server.GoGameServer;

import java.net.Socket;
import java.util.ArrayList;

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
    public ArrayList<Game> getGameList(){
        return goGameServer.getGameList();
    }
}
