package goGame.Server;

import goGame.GameLogic.Game;
import goGame.GameLogic.Player;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GoGameServer {
    private static int _lobbyPort = 50000;
    private static int _port = 59090;
    private static int _boardSize;
    private static ServerSocket _lobbySocket;
    private static ArrayList<Game> _games;
    private static boolean[][] _players;

    public static void main(String[] args) {

        _boardSize = getBoardSize();
        try{
            initializeServer();
            openLobby();
        }catch (Exception ignored){ }
    }

    private static void initializeServer() throws Exception{
        System.out.println("Server is Running :)");
        _games = new ArrayList<>();
        _players = new boolean[15][30];
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
        while (true) {
            ServerSocket listener = new ServerSocket(_port);
            performConnection(pool, listener);
            _port++;
        }

    }

    private static void openLobby() throws IOException {
        _lobbySocket = new ServerSocket(_lobbyPort);
    }

    private static void performConnection(ThreadPoolExecutor pool, ServerSocket serverSocket) throws IOException {
        Socket acceptedSocket = serverSocket.accept();
        Game game = new Game(_boardSize);
        _games.add(game);
        Scanner acceptedSocketScanner = new Scanner(acceptedSocket.getInputStream());
        PrintWriter acceptedSocketOutput = new PrintWriter(acceptedSocket.getOutputStream(), true);
        acceptedSocketOutput.println(_boardSize);
        acceptedSocketOutput.println("Black");

        while(acceptedSocketScanner.hasNextLine()){
            String response = acceptedSocketScanner.nextLine();
            if(response.equals("PLAY_WITH_BOT")){
                pool.execute((Runnable)game.createPlayer(acceptedSocket,"Black"));
                pool.execute((Runnable)game.createPlayer(acceptedSocket, "Bot"));
                break;
            }
            else if(response.equals("DONT_PLAY_WITH_BOT")){
                pool.execute((Runnable)game.createPlayer(acceptedSocket,"Black"));
                _players[_games.indexOf(game)][0] = true;
                acceptedSocket = serverSocket.accept();
                acceptedSocketOutput = new PrintWriter(acceptedSocket.getOutputStream(), true);
                acceptedSocketOutput.println(_boardSize);
                acceptedSocketOutput.println("White");
                pool.execute((Runnable)game.createPlayer(acceptedSocket,"White"));
                _players[_games.indexOf(game)][1] = true;
                break;
            }
        }
    }

    private static int getBoardSize() {
        String[] options = {"9", "13", "19"};
        return convertToInt((String) JOptionPane.showInputDialog(
                null,
                "Podaj rozmiar Planszy",
                "Game of Go",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]));
    }

    private static int convertToInt(String str){
        int num = 0;
        try{
            num = Integer.parseInt(str);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return num;
    }
    public void sendGameList(){

    }
}
