package goGame.Presets;

import goGame.GameLogic.Game;
import goGame.Server.GoGameServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
        goGameServer.presetGameList();
        ServerSocket ss = new ServerSocket(); // don't bind just yet
        ss.setReuseAddress(true);
        ss.bind(new InetSocketAddress(59090));
        goGameServer.initpool();
        goGameServer.setServerSocker(ss);
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

    public void closeSocket() throws IOException {
        if(goGameServer != null)
            goGameServer.getServerSocket().close();
    }
}
