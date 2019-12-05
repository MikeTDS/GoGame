package goGame.Server.Bot;

import goGame.GameLogic.Game;
import goGame.GameLogic.IPlayer;
import goGame.GameLogic.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Bot implements Runnable, IPlayer, IBot {

    private String _color;
    private Player _opponent;
    private Scanner _input;
    private PrintWriter _output;
    private Game _game;
    private Boolean _lastMovePass;

    Bot(String color, Game game) {
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
        }
    }
    //Player
    @Override
    public void setup() throws IOException {

    }

    @Override
    public void processCommands() {

    }

    @Override
    public void processMoveCommand(int x, int y) {

    }

    @Override
    public void sendOutput(String out) {

    }

    @Override
    public Player getOpponent() {
        return null;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public boolean getPass() {
        return false;
    }
    //IBot
    @Override
    public void findBestField() {

    }

    @Override
    public void makeMove(int x, int y) {

    }

    @Override
    public void decideIfPass() {

    }
}
