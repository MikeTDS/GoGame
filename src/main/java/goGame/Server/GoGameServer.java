package goGame.Server;

import goGame.GameLogic.Game;

import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GoGameServer {
    private static int _port = 59090;

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(_port)) {
            System.out.println("Server is Running :)");
            ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
            while (true) {
                Game game = new Game();
                pool.execute(game.createPlayer(listener.accept(), 'B'));
                pool.execute(game.createPlayer(listener.accept(), 'W'));
            }
        }
    }
}
