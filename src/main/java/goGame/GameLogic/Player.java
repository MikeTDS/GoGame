package goGame.GameLogic;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class Player implements Runnable {
    private String _color;
    private Player _opponent;
    private Socket _socket;
    private Scanner _input;
    private PrintWriter _output;
    private Game _game;

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
            if (_opponent != null && _opponent._output != null) {
                _opponent._output.println("OPPONENT_LEFT");
            }
            try {
                _socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void setup() throws IOException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        _output.println(Game.getBoardSize());
        _output.println(_color);
        if (_color.equals("Black")) {
            _game.currentPlayer = this;
            _output.println("MESSAGE Waiting for opponent to connect");
        } else if(_color.equals("White")){
            _opponent = _game.currentPlayer;
            _opponent._opponent = this;
            _opponent._output.println("MESSAGE Your move");
        }
    }

    private void processCommands() {
        while (_input.hasNextLine()) {
            String command = _input.nextLine();
            if (command.equals("QUIT")) {
                return;
            } else if (command.equals("MOVE")) {
                System.out.println(command);
                int x = Integer.parseInt(_input.nextLine());
                int y = Integer.parseInt(_input.nextLine());
                processMoveCommand(x, y);
            }
        }
    }

    private void processMoveCommand(int x, int y) {
        try {
            _game.move(x, y, this);
            _output.println("VALID_MOVE");
            _output.println(x);
            _output.println(y);
            _opponent._output.println("OPPONENT_MOVED");
            _opponent._output.println(x);
            _opponent._output.println(y);
        } catch (IllegalStateException e) {
            _output.println("WRONG_MOVE " + e.getMessage());
        }
    }

    Player getOpponent(){ return  _opponent; }
}