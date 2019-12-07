package goGame.Server.Bot;

import goGame.GameLogic.AbstractPlayer;
import goGame.GameLogic.Game;
import goGame.GameLogic.IPlayer;
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
        for(int i=0; i<_game.getBoardSize()*_game.getBoardSize(); i++){
            if(_game.getBoard()[i]==null){
                processMoveCommand(getXFromBoard(i), getYFromBoard(i));
            }
        }
    }

    @Override
    public void makeMove(int x, int y) {

    }

    @Override
    public void decideIfPass() {

    }
    //
    private void play(){
        while(true){
            findBestField();
        }
    }
    private int getXFromBoard(int i){
        return _game.getBoardSize()%i;
    }
    private int getYFromBoard(int i){
        return _game.getBoardSize()/i;
    }
}
