package goGame.Client;

import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GUI.MenuFrame;
import goGame.GUI.GuiFrame;
import goGame.GUIcomponents.ScoreBoard.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GoGameClient {
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int WIDTH = 1200,
                             HEIGHT = 900,
                             DEFAULT_SERVER_PORT = 59090;

    private static ServerCommunicator _serverCommunicator;
    private static GuiFrame _clientFrame;
    private static GoBoard _goBoard;
    private static ScoreBoard _scoreBoard;
    private static MenuFrame _menuFrame;
    private static boolean closedSocket = false;
    private static String color, opponentColor;
    private static boolean _finished;

    public static void main( String[] args ) {
            connectToServer();
            chooseGame();
    }

    private static void chooseGame(){
        String chosenGame = chooseMenu();
        if(chosenGame.equals("New game")){
            _serverCommunicator.getPrintWriter().println("NEW_GAME");
            initializeGame();
            try {
                play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(chosenGame.equals("Join game")){
            _serverCommunicator.getPrintWriter().println("JOIN_GAME");
            connectToLobby();
            launchingGame();
        }
    }

    public static void initializeGame(){
        int goBoardSize = convertToInt(getResponse());
        _finished = false;
        _goBoard = new GoBoard(goBoardSize);
        _scoreBoard = new ScoreBoard();
        _clientFrame = new GuiFrame(WIDTH, HEIGHT);
        _clientFrame.add(_goBoard);
        _clientFrame.add(_scoreBoard);
        _clientFrame.setVisible(true);
        _clientFrame.saveBoardImage();
    }

    private static void launchingGame(){
        String response = getResponse();
        while(response.equals("Game is full or no game chosen.")){
            JOptionPane.showMessageDialog(null, response);
            response = getResponse();
        }

        if(response.contains("Connected to game")){
            _menuFrame.dispose();
            initializeGame();
            try {
                play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void connectToServer(){
        _serverCommunicator = ServerCommunicator.getInstance(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
        try {
            _serverCommunicator.connectToServer();
        }catch (Exception e){ System.out.println(e.getMessage()); }
    }

    public static void connectToLobby(){
        _menuFrame = new MenuFrame();
        showGames();
    }

    private static void showGames(){
        if(_serverCommunicator.getScanner().hasNextLine()){
            if(_serverCommunicator.getScanner().nextLine().equals("GAME_LIST")){
                while(_serverCommunicator.getScanner().hasNextLine()){
                    String response = _serverCommunicator.getScanner().nextLine();
                    if(response.equals("NO_MORE_GAMES"))
                        break;
                    _menuFrame.addGamesToList(response);
                }
            }
        }
    }

    public static void sendChosenGame(int game){
        _serverCommunicator.getPrintWriter().println(game);
    }
    public static String getResponse() {
        String response = null;
        while (_serverCommunicator.getScanner().hasNextLine()){
            response = _serverCommunicator.getScanner().nextLine();
            if(response != null)
                break;
        }

        return response;
    }

    private static String chooseMenu(){
        String[] options = {"New game", "Join game"};
        String response = ((String) JOptionPane.showInputDialog(
                null,
                "Choose game:",
                "Game of Go",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]));
        if(response==null || (response!=null && response.equals("")))
            System.exit(-1);
        return response;

    }

    public static int convertToInt(String str){
        int num = 0;
        try{
            num = Integer.parseInt(str);
        }catch (Exception e){ System.out.println(e.getMessage()); }

        return num;
    }

    public static void play() throws Exception {
        String response;
        try {
            setupClient();
            while (_serverCommunicator.getScanner().hasNext() && !_finished){
                response = _serverCommunicator.getScanner().nextLine();
                performActionFromResponse(response);
            }
            _serverCommunicator.getPrintWriter().println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("quited");
            if(!closedSocket)
                _serverCommunicator.getSocket().close();
            _clientFrame.dispose();
        }
    }

    public static void setupClient() {
        setColor(_serverCommunicator.getScanner().nextLine());
        setOpponentColor(color.equals("Black") ? "White" : "Black");
        if(color.equals("Black")){
            _clientFrame.chooseOpponent();
        }
        _clientFrame.setTitle("Go: Gracz " + color);
    }

    public static void performActionFromResponse(String response) throws IOException {
        int x, y;
        switch (response){
            case "VALID_MOVE":
                response = _serverCommunicator.getScanner().nextLine();
                x = convertToInt(response);

                response = _serverCommunicator.getScanner().nextLine();
                y = convertToInt(response);

                _goBoard.setStone(x, y, color);
                _goBoard.repaint();
                _clientFrame.saveBoardImage();
                _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                break;
            case "OPPONENT_MOVED":
                response = _serverCommunicator.getScanner().nextLine();
                x = convertToInt(response);

                response = _serverCommunicator.getScanner().nextLine();
                y = convertToInt(response);

                _goBoard.setStone(x, y, opponentColor);
                _goBoard.repaint();
                _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                break;
            case "KILL":
                response = _serverCommunicator.getScanner().nextLine();
                while (!response.equals("KILL_STOP")){
                    x = convertToInt(response);

                    response = _serverCommunicator.getScanner().nextLine();
                    y = convertToInt(response);
                    response = _serverCommunicator.getScanner().nextLine();
                    _goBoard.removeStone(x, y, response);
                    response = _serverCommunicator.getScanner().nextLine();
                }
                _goBoard.repaint();
                _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                break;
            case "SURRENDER":
                JOptionPane.showMessageDialog(_clientFrame, "You surrendered the game. Shame!");
                _serverCommunicator.getSocket().close();
                closedSocket=true;
                _clientFrame.dispose();
                break;
            case "SURRENDER_WIN":
                JOptionPane.showMessageDialog(_clientFrame, "You won, because your opponent surrendered.");
                _serverCommunicator.getSocket().close();
                closedSocket=true;
                _clientFrame.dispose();
                break;
            case "NORMAL_EXIT":
                JOptionPane.showMessageDialog(_clientFrame, "See you!");
                _serverCommunicator.getSocket().close();
                closedSocket=true;
                _clientFrame.dispose();
                break;
            case "FIRST_PASS":
                JOptionPane.showMessageDialog(_clientFrame, "You passed.");
                break;
            case "QUIT_PASS":
                JOptionPane.showMessageDialog(_clientFrame, "Two passes in row. Game finished.");
                break;
            case "ERROR_PASS":
                JOptionPane.showMessageDialog(_clientFrame, "Can't pass. Wait for opponent.");
                break;
            case "OPPONENT_PASS":
                JOptionPane.showMessageDialog(_clientFrame, "Opponent passed. Your move.");
                break;
            case "POINTS":
                int points = Integer.parseInt(_serverCommunicator.getScanner().nextLine());
                ScoreBoard.showPoints(points);
                break;
            case "WINNER":
                _finished = true;
                JOptionPane.showMessageDialog(_clientFrame, "You won.");
                _serverCommunicator.getSocket().close();
                closedSocket=true;
                _clientFrame.dispose();
                break;
            case "LOSER":
                _finished = true;
                JOptionPane.showMessageDialog(_clientFrame, "You lost.");
                _serverCommunicator.getSocket().close();
                closedSocket=true;
                _clientFrame.dispose();
                break;
            case "DRAW":
                _finished = true;
                JOptionPane.showMessageDialog(_clientFrame, "Draw.");
                _serverCommunicator.getSocket().close();
                closedSocket=true;
                _clientFrame.dispose();
                break;
        }
    }

    public static GuiFrame getGuiFrame(){
        return _clientFrame;
    }
    public ServerCommunicator getServerCommunicator(){
        return _serverCommunicator;
    }
    public static MenuFrame getMenuFrame(){
        return _menuFrame;
    }
    public static String getColor(){return color;}
    public static String getOpponentColor(){return opponentColor;}
    public static void setColor(String clr){ color = clr; }
    public static void setOpponentColor(String clr){ opponentColor = clr; }
}
