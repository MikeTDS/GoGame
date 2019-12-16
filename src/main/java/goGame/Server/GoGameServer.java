package goGame.Server;

import goGame.GameLogic.Game;
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
    private static final int MAX_GAMES = 15;
    private static int PORT = 59090;
    private static int _boardSize;
    private static ServerSocket _serverSocket;
    private static ThreadPoolExecutor _pool;
    private static ArrayList<Game> _games;
    private static ArrayList<Game> _gamesToRemove;
    private static boolean[][] _players;
    private static Socket _currentSocket;

    public static void main(String[] args) {

        _boardSize = getBoardSize();
        presetGameList();
        try{
            initializeServer();
            listenForClients();
        }catch (Exception ignored){ }
    }

    public static void initializeServer() throws Exception{
        System.out.println("Server is Running :)");
         _pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_GAMES*2);
        _serverSocket = new ServerSocket(PORT);

    }
    public static void listenForClients() {
        while (true) {
            Socket acceptedSocket;
            try {
                acceptedSocket = _serverSocket.accept();
                _currentSocket = acceptedSocket;
                Scanner acceptedSocketScanner = new Scanner(acceptedSocket.getInputStream());
                PrintWriter acceptedSocketWriter = new PrintWriter(acceptedSocket.getOutputStream(), true);
                String gameOption = getGameOption(acceptedSocketScanner);
                if(gameOption!=null){
                    if(gameOption.equals("NEW_GAME")){
                        createNewGame(acceptedSocket, acceptedSocketScanner, acceptedSocketWriter);
                    }
                    else if(gameOption.equals(("JOIN_GAME"))){
                        checkIfGamesFinished();
                        sendGameList(acceptedSocketWriter);
                        String chosenGame = getGameOption(acceptedSocketScanner);
                        if(chosenGame!=null){
                            while(!checkIfChosenGameIsCorrect(convertToInt(chosenGame), acceptedSocketWriter)){
                                chosenGame = getGameOption(acceptedSocketScanner);
                            }
                            acceptedSocketWriter.println("Connected to game: " + chosenGame);
                            connectToChosenGame(acceptedSocket, convertToInt(chosenGame), acceptedSocketWriter);
                        }
                    }
                    else{
                        System.out.println("Error occurred while choosing game option.");
                    }
                }
                else{
                    System.out.println("Error occurred while choosing game option.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static boolean checkIfChosenGameIsCorrect(int chosenGame, PrintWriter acceptedSocketWriter) {
        if(chosenGame>=_games.size() || chosenGame<0 || _players[chosenGame][1]){
            acceptedSocketWriter.println("Game is full or no game chosen.");
            return false;
        }
        return true;
    }

    private static String getGameOption(Scanner scanner){
        while(scanner.hasNextLine()){
            String response = scanner.nextLine();
            if(response != null)
                return response;
        }

        return null;
    }
    private static void createNewGame(Socket playerSocket, Scanner playerScanner, PrintWriter playerWriter){
        Game game = new Game(_boardSize);
        _games.add(game);
        playerWriter.println(_boardSize);
        playerWriter.println("Black");

        while(playerScanner.hasNextLine()){
            String response = playerScanner.nextLine();
            if(response.equals("PLAY_WITH_BOT")){
                    _pool.execute((Runnable)game.createPlayer(playerSocket,"Black"));
                    _players[_games.indexOf(game)][0] = true;
                    _pool.execute((Runnable)game.createPlayer(playerSocket, "Bot"));
                    _players[_games.indexOf(game)][1] = true;
                break;
            }
            else if(response.equals("DONT_PLAY_WITH_BOT")){
                _pool.execute((Runnable)game.createPlayer(playerSocket,"Black"));
                _players[_games.indexOf(game)][0] = true;
                break;
            }
        }
    }
    private static void connectToChosenGame(Socket playerSocket, int gameNumber, PrintWriter playerWriter){
        Game chosenGame = _games.get(gameNumber);
        playerWriter.println(_boardSize);
        playerWriter.println("White");
        _pool.execute((Runnable)chosenGame.createPlayer(playerSocket,"White"));
        _players[gameNumber][1] = true;
    }
    private static void sendGameList(PrintWriter writer) {
        if (_games.size() > 0) {
            writer.println("GAME_LIST");
            for (int i=0; i<_games.size(); i++) {
                if(_games.get(i).getCurrentPlayer()!=null && _games.get(i).getCurrentPlayer().getOpponent()!=null){
                    writer.println(i + ". " + _games.get(i).getName() + "2/2");
                }
                else{
                    writer.println(i + ". " + _games.get(i).getName() + "1/2");
                }
            }
            writer.println("NO_MORE_GAMES");
        } else {
            writer.println("EMPTY_GAMES_LIST");
        }
    }

    public static int getBoardSize() {
        String[] options = {"9", "13", "19"};
        String response =(String)JOptionPane.showInputDialog(
                null,
                "Podaj rozmiar Planszy",
                "Game of Go",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if(response!=null){
            return convertToInt(response);
        }
        else{
            System.exit(0);
            return 0;
        }
    }

    private static int convertToInt(String str){
        int num = 0;
        try{
            num = Integer.parseInt(str);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return num;
    }
    public void setBoardSize(int n){
        _boardSize=n;
    }
    public static void presetGameList(){
        _games = new ArrayList<>();
        _players = new boolean[MAX_GAMES][2];
        _gamesToRemove = new ArrayList<>();
    }
    private synchronized static void checkIfGamesFinished(){
        for(int i=0; i<_games.size(); i++){
            if(_games.get(i)._finished)
                _gamesToRemove.add(_games.get(i));
                _players[i][0]=false;
                _players[i][1]=false;
        }
        _games.removeAll(_gamesToRemove);
        _gamesToRemove.clear();
    }
    public int getPreviouslySetBoardSize(){
        return _boardSize;
    }
    public Socket getCurrentSocket(){return _currentSocket;}
}
