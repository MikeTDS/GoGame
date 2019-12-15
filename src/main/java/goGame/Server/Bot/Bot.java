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
    private static int _midTrigger, _lateTrigger;

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

    private void setup() throws IOException, InterruptedException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        while (_opponent == null){
            _opponent = _game.getCurrentPlayer();
            Thread.sleep(10);
        }
        _opponent.setOpponent(this);
        _opponent.getOpponent().getOutput().println("MESSAGE Your move");
        _moveOrganizer = new MoveOrganizer();
        _botBrain = new BotBrain(_game, _color, _opponent.getColor());
        _moveOrganizer.setMoveState(new EarlyMoveState(new int[_game.getBoardSize()*_game.getBoardSize()], _botBrain));

        _midTrigger = 7;
        _lateTrigger = _botBrain.getBoardSize() * _botBrain.getBoardSize() * 50 / 100;
        if(_lateTrigger % 2 == 0)
            _lateTrigger++;
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
                if(_botBrain.calculateStonesOnTheBoard() == _midTrigger)
                    _moveOrganizer.setMoveState(new MidMoveState(new int[_game.getBoardSize()*_game.getBoardSize()], _botBrain));
                if(_botBrain.calculateStonesOnTheBoard() == _lateTrigger)
                    _moveOrganizer.setMoveState(new LateMoveState(new int[_game.getBoardSize()*_game.getBoardSize()], _botBrain));

                int chosenField = _moveOrganizer.getBestField(this);
                if(chosenField == -1){
                    if(_opponent==null || _game.getCurrentPlayer()!=this){
                        System.out.println("ERROR_PASS");
                    }
                    else{
                        if(_opponent.getPass()){
                            _opponent.getOutput().println("QUIT_PASS");
                            _game._finished=true;
                        }
                        else{
                            _opponent.getOutput().println("OPPONENT_PASS");
                            _lastMovePass=true;
                            _game.skipMove();
                        }
                    }
                }
                else {
                    processMoveCommand(_botBrain.getXFromBoard(chosenField), _botBrain.getYFromBoard(chosenField));
                    System.out.println(totalPoints);
                }
            }
        }
    }
}
