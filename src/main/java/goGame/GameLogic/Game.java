package goGame.GameLogic;

import java.net.Socket;
import java.util.ArrayList;

public class Game {
    private Player[] _board;
    private int _size;

    Player currentPlayer;

    public Game(){ this(9); }
    public Game(int size){
        _board = new Player[size*size];
        _size = size;
    }

    synchronized void move(int location, Player player) {
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

    public Player createPlayer(Socket socket, char mark){
        return new Player(socket, mark);
    }
}
