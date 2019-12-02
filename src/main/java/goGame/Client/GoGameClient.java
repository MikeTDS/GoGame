package goGame.Client;

import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GUI.GuiFrame;
import goGame.GUIcomponents.ScoreBoard.ScoreBoard;

import javax.swing.*;
import java.awt.*;

public class GoGameClient {
    private static final String DEFAULT_SERVER_ADRESS = "localhost";
    private static final int WIDTH = 1200,
                             HEIGHT = 900,
                             DEFAULT_SERVER_PORT = 59090;

    private static ServerComunitator _serverComunitator;
    private static GuiFrame _clientFrame;
    private static GoBoard _goBoard;
    private static ScoreBoard _scoreBoard;

    public static void main( String[] args ) throws Exception {
        initializeGame();
        play();
    }


    public static void initializeGame(){
        _serverComunitator = new ServerComunitator(DEFAULT_SERVER_ADRESS, DEFAULT_SERVER_PORT);
        try {
            ServerComunitator.connectToServer();
        }catch (Exception e){ System.out.println(e.getMessage()); }

        int goBoardSize = getBoardSize();
        _goBoard = new GoBoard(goBoardSize, _serverComunitator);
        _scoreBoard = new ScoreBoard();
        _clientFrame = new GuiFrame(WIDTH, HEIGHT);
        _clientFrame.add(_goBoard);
        _clientFrame.add(_scoreBoard);
    }

    public static int getBoardSize() {
        String response = ServerComunitator.getScanner().nextLine();

        return convertToInt(response);
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
            String response = ServerComunitator.getScanner().nextLine();
            String color = response;
            String opponentColor = color.equals("Black") ? "White" : "Black";
            _clientFrame.setTitle("Go: Gracz " + color);
            while (ServerComunitator.getScanner().hasNextLine()) {
                response = ServerComunitator.getScanner().nextLine();

                switch (response){
                    case "VALID_MOVE":
                        response = ServerComunitator.getScanner().nextLine();
                        x = convertToInt(response);

                        response = ServerComunitator.getScanner().nextLine();
                        y = convertToInt(response);

                        _goBoard.setStone(x, y, color);
                        _goBoard.repaint();
                        _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                        break;
                    case "OPPONENT_MOVED":
                        response = ServerComunitator.getScanner().nextLine();
                        x = convertToInt(response);

                        response = ServerComunitator.getScanner().nextLine();
                        y = convertToInt(response);

                        _goBoard.setStone(x, y, opponentColor);
                        _goBoard.repaint();
                        _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                        break;
                    case "KILL":
                        response = ServerComunitator.getScanner().nextLine();
                        while (!response.equals("KILL_STOP")){
                            x = convertToInt(response);

                            response = ServerComunitator.getScanner().nextLine();
                            y = convertToInt(response);
                            response = ServerComunitator.getScanner().nextLine();
                            _goBoard.removeStone(x, y, response);
                            response = ServerComunitator.getScanner().nextLine();
                        }
                        _goBoard.repaint();
                        _scoreBoard.showStones(_goBoard.getStonesAmount("black"), _goBoard.getStonesAmount("white"));
                        break;
                    case "SURRENDER":
                        JOptionPane.showMessageDialog(_clientFrame, "You surrendered the game. Shame!");
                        ServerComunitator.getSocket().close();
                        break;
                    case "SURRENDER_WIN":
                        JOptionPane.showMessageDialog(_clientFrame, "You won, because your opponent surrendered.");
                        ServerComunitator.getSocket().close();
                        break;
                    case "NORMAL_EXIT":
                        JOptionPane.showMessageDialog(_clientFrame, "See you!");
                        ServerComunitator.getSocket().close();
                        break;
                }
            }
            ServerComunitator.getPrintWriter().println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("quited");
            ServerComunitator.getSocket().close();
            _clientFrame.dispose();
        }
    }
    public static GuiFrame getGuiFrame(){
        return _clientFrame;
    }
}
