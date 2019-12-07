package goGame.Server.Bot;

import goGame.GameLogic.AbstractPlayer;
import goGame.GameLogic.Game;
import goGame.GameLogic.Stone;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Bot extends AbstractPlayer implements Runnable, IBot {

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
    //Player
    public void setup() throws IOException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        _opponent = _game.getCurrentPlayer();
        _opponent.setOpponent(this);
        _opponent.getOpponent().getOutput().println("MESSAGE Your move");
    }

    //IBot
    @Override
    public void findBestField(){
        for(int i=0; i<=_game.getBoardSize()*_game.getBoardSize(); i++){
                try {
                    processMoveCommand(getXFromBoard(i), getYFromBoard(i));
                    break;
                } catch (Exception e) {
                    continue;
                }

        }
    }

    public void processMoveCommand(int x, int y) throws Exception {
        try {
            Thread.sleep(1);
            _game.move(x, y, this);
            _opponent.getOutput().println("OPPONENT_MOVED");
            _opponent.getOutput().println(x);
            _opponent.getOutput().println(y);
        } catch (IllegalStateException | InterruptedException e) {
            System.out.println( e.getMessage());
            throw new Exception();
        }
    }
    @Override
    public void sendOutput(String out) { }

    @Override
    public void makeMove(int x, int y) {

    }

    @Override
    public void decideIfPass() {

    }
    //
    private void play(){
        while(true){
            if(_game.getCurrentPlayer().equals(this)){
               findBestField();
            }
        }
    }
    private int getXFromBoard(int i){
        if(i == 0)
            return 0;
        return i%_game.getBoardSize();
    }
    private int getYFromBoard(int i){
        if(i == 0)
            return 0;
        return i/_game.getBoardSize();
    }
}
