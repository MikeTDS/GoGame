package goGame.GameLogic;

import goGame.Client.GoGameClient;
import goGame.GUI.GuiFrame;

import javax.swing.*;
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
    private Boolean _lastMovePass;

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
        _lastMovePass=false;
        if (_color.equals("Black")) {
            Game.currentPlayer = this;
            _output.println("MESSAGE Waiting for opponent to connect");
        } else if(_color.equals("White")){
            _opponent = Game.currentPlayer;
            _opponent._opponent = this;
            _opponent._output.println("MESSAGE Your move");
        }
    }

    private void processCommands() {
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
                        _opponent._output.println("SURRENDER_WIN");
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
                        _opponent._output.println("QUIT_PASS");
                        //_game._finished=true; <-pododawac
                        //_game.countPoints();
                    }
                    else{
                        _output.println("FIRST_PASS");
                        _lastMovePass=true;
                        _game.skipMove();
                    }
                }
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
            System.out.println( e.getMessage());
            _output.println("WRONG_MOVE " + e.getMessage());
        }
    }

    Player getOpponent(){ return  _opponent; }
    String getColor(){ return  _color; }
    void sendOutput(String out){ _output.println(out); }
    public boolean getPass() {return _lastMovePass;}
}