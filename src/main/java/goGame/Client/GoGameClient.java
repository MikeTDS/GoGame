package goGame.Client;

import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GUI.MenuFrame;
import goGame.GUI.GuiFrame;
import goGame.GUIcomponents.ScoreBoard.ScoreBoard;

import javax.swing.*;

public class GoGameClient {
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int WIDTH = 1200,
                             HEIGHT = 900,
                             DEFAULT_SERVER_PORT = 59090;

    private static ServerComunicator _serverComunicator;
    public static LobbyComunicator _lobbyCommunicator;
    private static GuiFrame _clientFrame;
    private static GoBoard _goBoard;
    private static ScoreBoard _scoreBoard;
    private static MenuFrame _menuFrame;

    public static void main( String[] args ) throws Exception {
        String chosenGame = chooseMenu();
        if (chosenGame.equals("New game")){
            initializeGame();
            play();
        }
        else if(chosenGame.equals("Join game")){
            connectToLobby();
        }
    }


    private static void initializeGame(){
        _serverComunicator = ServerComunicator.getInstance(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
        try {
            _serverComunicator.connectToServer();
        }catch (Exception e){ System.out.println(e.getMessage()); }

        int goBoardSize = getBoardSize();
        _goBoard = new GoBoard(goBoardSize);
        _scoreBoard = new ScoreBoard();
        _clientFrame = new GuiFrame(WIDTH, HEIGHT);
        _clientFrame.add(_goBoard);
        _clientFrame.add(_scoreBoard);
    }

    private static void connectToLobby(){
        _lobbyCommunicator = LobbyComunicator.getInstance();
        try{
            _lobbyCommunicator.connectToServer();
        }
        catch (Exception e ){System.out.println(e.getMessage());}
        _menuFrame = new MenuFrame();
        //_menuFrame.addGamesToList(games from server);
    }


    private static int getBoardSize() {
        String response = _serverComunicator.getScanner().nextLine();

        return convertToInt(response);
    }

    private static String chooseMenu(){
        String[] options = {"New game", "Join game"};
        return ((String) JOptionPane.showInputDialog(
                null,
                "Choose game:",
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

    public static void play() throws Exception {
        try {
            int x, y;
            String response = _serverComunicator.getScanner().nextLine();
            String color = response;
            String opponentColor = color.equals("Black") ? "White" : "Black";
            if(color.equals("Black")){
                _clientFrame.chooseOpponent();
            }
            _clientFrame.setTitle("Go: Gracz " + color);
            while (_serverComunicator.getScanner().hasNextLine()) {
                response = _serverComunicator.getScanner().nextLine();
                switch (response){
                    case "VALID_MOVE":
                        response = _serverComunicator.getScanner().nextLine();
                        x = convertToInt(response);

                        response = _serverComunicator.getScanner().nextLine();
                        y = convertToInt(response);

                        _goBoard.setStone(x, y, color);
                        _goBoard.repaint();
                        _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                        break;
                    case "OPPONENT_MOVED":
                        response = _serverComunicator.getScanner().nextLine();
                        x = convertToInt(response);

                        response = _serverComunicator.getScanner().nextLine();
                        y = convertToInt(response);

                        _goBoard.setStone(x, y, opponentColor);
                        _goBoard.repaint();
                        _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                        break;
                    case "KILL":
                        response = _serverComunicator.getScanner().nextLine();
                        while (!response.equals("KILL_STOP")){
                            x = convertToInt(response);

                            response = _serverComunicator.getScanner().nextLine();
                            y = convertToInt(response);
                            response = _serverComunicator.getScanner().nextLine();
                            _goBoard.removeStone(x, y, response);
                            response = _serverComunicator.getScanner().nextLine();
                        }
                        _goBoard.repaint();
                        _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                        break;
                    case "SURRENDER":
                        JOptionPane.showMessageDialog(_clientFrame, "You surrendered the game. Shame!");
                        _serverComunicator.getSocket().close();
                        break;
                    case "SURRENDER_WIN":
                        JOptionPane.showMessageDialog(_clientFrame, "You won, because your opponent surrendered.");
                        _serverComunicator.getSocket().close();
                        break;
                    case "NORMAL_EXIT":
                        JOptionPane.showMessageDialog(_clientFrame, "See you!");
                        _serverComunicator.getSocket().close();
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
                }
            }
            _serverComunicator.getPrintWriter().println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("quited");
            _serverComunicator.getSocket().close();
            _clientFrame.dispose();
        }
    }
    public static GuiFrame getGuiFrame(){
        return _clientFrame;
    }
}
