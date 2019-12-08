package goGame.GameLogic;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player extends AbstractPlayer implements Runnable {

    Player(Socket socket, String color, Game game) {
        _socket = socket;
        _color = color;
        _game = game;
    }

    @Override
    public void run() {
        try {
            setup();
            processCommands();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (_opponent != null && _opponent.getOutput() != null) {
                _opponent.getOutput().println("OPPONENT_LEFT");
            }
            try {
                _socket.close();
            } catch (IOException ignored) {}
        }
    }

    public void setup() throws IOException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        _lastMovePass=false;
        if (_color.equals("Black")) {
            _game.currentPlayer = this;
            _output.println("MESSAGE Waiting for opponent to connect");
        } else if(_color.equals("White")){
            _opponent = _game.currentPlayer;
            _opponent.setOpponent(this);
            _opponent.getOutput().println("MESSAGE Your move");
        }
    }

    public void processCommands() {
        while (_input.hasNextLine()) {
            String command = _input.nextLine();
            _lastMovePass=false;
            if (command.equals("QUIT")) {
                //zniszczyc ten obiekt przed wyjsciem
                return;
            } else if (command.equals("MOVE")) {
                int x = Integer.parseInt(_input.nextLine());
                int y = Integer.parseInt(_input.nextLine());
                processMoveCommand(x, y);
            }
            else if(command.equals("EXIT")){
                if(!_game._finished && _opponent!=null){
                    _output.println("SURRENDER");
                    if(_opponent!=null)
                        _opponent.getOutput().println("SURRENDER_WIN");
                }
                else{
                    _output.println("NORMAL_EXIT");
                }
            }
            else if(command.equals("PASS")){
                if(_opponent==null || _game.getCurrentPlayer()!=this){
                    _output.println("ERROR_PASS");
                }
                else{
                    if(_opponent.getPass()){
                        _output.println("QUIT_PASS");
                        _opponent.getOutput().println("QUIT_PASS");
                        //_game._finished=true; <-pododawac gdy dodam zliczanie punktow
                        //_game.countPoints();
                    }
                    else{
                        _output.println("FIRST_PASS");
                        _opponent.getOutput().println("OPPONENT_PASS");
                        _lastMovePass=true;
                        _game.skipMove();
                    }
                }
            }
        }
    }
    public void processMoveCommand(int x, int y) {
        try {
            _game.move(x, y, this);
            _output.println("VALID_MOVE");
            _output.println(x);
            _output.println(y);
            if(!_opponent.getSocket().equals(getSocket())){
                _opponent.getOutput().println("OPPONENT_MOVED");
                _opponent.getOutput().println(x);
                _opponent.getOutput().println(y);
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            _output.println("WRONG_MOVE " + e.getMessage());
        }
    }
}