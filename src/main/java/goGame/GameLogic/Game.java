package goGame.GameLogic;


import java.net.Socket;

public class Game {
    private static Stone[] _board;
    private static int _size;

    Player currentPlayer;

    public Game(int size){
        _board = new Stone[size*size];
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

        _board[location] = currentPlayer.getColor().equals("Black") ? new Stone(x, y, "Black") : new Stone(x, y, "White") ;
        currentPlayer = currentPlayer.getOpponent();
    }

    private static int calcPos(int x, int y){ return x + y*_size; }
    static int getBoardSize(){ return _size; }
    public Player createPlayer(Socket socket, String color){
        return new Player(socket, color, this);
    }
}
