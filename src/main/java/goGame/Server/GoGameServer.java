package goGame.Server;

import goGame.GameLogic.Game;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GoGameServer {
    private static int _port = 59090;
    private static int _boardSize;

    public static void main(String[] args) {
        _boardSize = getBoardSize();
        try{
            initializeServer();
        }catch (Exception ignored){ }
    }

    private static void initializeServer() throws Exception{
        System.out.println("Server is Running :)");
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
        while (true) {
            ServerSocket listener = new ServerSocket(_port);
            performConnection(pool, listener);
            _port++;
        }

    }

    private static void performConnection(ThreadPoolExecutor pool, ServerSocket serverSocket) throws IOException {
        Socket acceptedSocket = serverSocket.accept();
        Game game = new Game(_boardSize);
        pool.execute((Runnable)game.createPlayer(acceptedSocket,"Black"));
        Scanner acceptedSocketScanner = new Scanner(acceptedSocket.getInputStream());
        while(acceptedSocketScanner.hasNextLine()){
            String response = acceptedSocketScanner.nextLine();
            if(response.equals("PLAY_WITH_BOT")){
                pool.execute((Runnable)game.createPlayer(acceptedSocket, "Bot"));
                break;
            }
            else if(response.equals("DONT_PLAY_WITH_BOT")){
                acceptedSocket = serverSocket.accept();
                pool.execute((Runnable)game.createPlayer(acceptedSocket,"White"));
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
        }catch (Exception e){ System.out.println(e.getMessage()); }

        return num;
    }
}
