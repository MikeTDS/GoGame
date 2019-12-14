package goGame.Server.Bot;

import goGame.GameLogic.AbstractPlayer;
import goGame.GameLogic.Game;
import goGame.GameLogic.Stone;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;


public class Bot extends AbstractPlayer implements Runnable {

    private MoveOrganizer _moveOrganizer;
    private BotBrain _botBrain;

    public Bot(Socket socket, String color, Game game) {
        _color = color;
        _game = game;
        _socket = socket;
    }

    @Override
    public void run() {
        try {
            setup();
            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() throws IOException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        _opponent = _game.getCurrentPlayer();
        _opponent.setOpponent(this);
        _opponent.getOpponent().getOutput().println("MESSAGE Your move");
        _moveOrganizer = new MoveOrganizer();
        _botBrain = new BotBrain(_game, _color, _opponent.getColor());
        _moveOrganizer.setMoveState(new EarlyMoveState(new int[_game.getBoardSize()*_game.getBoardSize()], _botBrain));
    }

    private void processMoveCommand(int x, int y) {
        try {
            Thread.sleep(1);
            _game.move(x, y, this);
            _opponent.getOutput().println("OPPONENT_MOVED");
            _opponent.getOutput().println(x);
            _opponent.getOutput().println(y);
        } catch (IllegalStateException | InterruptedException e) {
            System.out.println( e.getMessage());
        }
    }

    @Override
    public void sendOutput(String out) { }

    private void play(){
        while(true){
            if(_game.getCurrentPlayer().equals(this)){
                updatePoints();
                _botBrain.setBrainForRound();
                if(_botBrain.calculateStonesOnTheBoard() == 7)
                    _moveOrganizer.setMoveState(new MidMoveState(new int[_game.getBoardSize()*_game.getBoardSize()], _botBrain));

                int choosenField = _moveOrganizer.getBestField(this);
                processMoveCommand(_botBrain.getXFromBoard(choosenField), _botBrain.getYFromBoard(choosenField));
                System.out.println(totalPoints);
            }
        }
    }
}
