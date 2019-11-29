package goGame.GameLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class Player implements Runnable {
    private char _color;
    private Player _opponent;
    private Socket _socket;
    private Scanner _input;
    private PrintWriter _output;
    private Game _game;

    Player(Socket socket, char color) {
        this._socket = socket;
        this._color = color;
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
                _opponent._output.println("OTHER_PLAYER_LEFT");
            }
            try {
                _socket.close();
            } catch (IOException e) {}
        }
    }

    private void setup() throws IOException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        _output.println("WELCOME " + _color);
        if (_color == 'B') {
            _game.currentPlayer = this;
            _output.println("MESSAGE Waiting for opponent to connect");
        } else {
            _opponent = _game.currentPlayer;
            _opponent._opponent = this;
            _opponent._output.println("MESSAGE Your move");
        }
    }

    private void processCommands() {
        while (_input.hasNextLine()) {
            var command = _input.nextLine();
            if (command.startsWith("QUIT")) {
                return;
            } else if (command.startsWith("MOVE")) {
                processMoveCommand(Integer.parseInt(command.substring(5)));
            }
        }
    }

    private void processMoveCommand(int location) {
        try {
            _game.move(location, this);
            _output.println(MoveOptions.VALID_MOVE);
            _opponent._output.println(MoveOptions.OPPONENT_MOVED + " " + location);
        } catch (IllegalStateException e) {
            _output.println(MoveOptions.WRONG_MOVE + " " + e.getMessage());
        }
    }

    Player getOpponent(){ return  _opponent; }
}