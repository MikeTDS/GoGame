package goGame.Client;

import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GUI.GuiFrame;
import goGame.GUIcomponents.ScoreBoard.ScoreBoard;

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

    private static void initializeGame(){
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

    private static int getBoardSize() {
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

    private static void play() throws Exception {
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
                        break;
                    case "OPPONENT_MOVED":
                        response = ServerComunitator.getScanner().nextLine();
                        x = convertToInt(response);

                        response = ServerComunitator.getScanner().nextLine();
                        y = convertToInt(response);

                        _goBoard.setStone(x, y, opponentColor);
                        _goBoard.repaint();
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
}
