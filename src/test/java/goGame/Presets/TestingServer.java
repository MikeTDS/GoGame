package goGame.Presets;

import goGame.GameLogic.Game;
import goGame.Server.GoGameServer;

import java.net.Socket;
import java.util.ArrayList;

public class TestingServer implements Runnable{
    private static final int BOARD_SIZE = 19;

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
        goGameServer.setBoardSize(BOARD_SIZE);
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
