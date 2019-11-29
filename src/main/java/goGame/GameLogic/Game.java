package goGame.GameLogic;

import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;

public class Game {
    private static Player[] _board;
    private static int _size;

    Player currentPlayer;

    public Game(int size){
        _board = new Player[size*size];
        _size = size;
    }

    synchronized void move(int x, int y, Player player) {
        int location = calcPos(x, y);

        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.getOpponent() == null) {
            throw new IllegalStateException("Opponent not present");
        } else if (_board[location] != null) {
            throw new IllegalStateException("Field occupied");
        }
        _board[location] = currentPlayer;
        currentPlayer = currentPlayer.getOpponent();
    }

    private static int calcPos(int x, int y){ return x + y*_size; }
    static int getBoardSize(){ return _size; }
    public Player createPlayer(Socket socket, String color){
        return new Player(socket, color, this);
    }
}
