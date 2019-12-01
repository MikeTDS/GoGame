package goGame.GameLogic;


import java.net.Socket;
import java.util.ArrayList;

public class Game {
    private static Stone[] _board;
    private static int _size;
    private static ArrayList<Stone> _currentCheckGroup;
    private static int _blockedFiled;
    private static boolean _canBeUnlocked;

    static Player currentPlayer;

    public Game(int size){
        _currentCheckGroup = new ArrayList<>();
        _board = new Stone[size*size];
        _size = size;
        _blockedFiled = -1;
    }

    synchronized void move(int x, int y, Player player) {
        int location = calcPos(x, y);
        Stone newStone = currentPlayer.getColor().equals("Black") ? new Stone(x, y, "Black") : new Stone(x, y, "White") ;
        _canBeUnlocked = true;

        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.getOpponent() == null) {
            throw new IllegalStateException("Opponent not present");
        } else if (_board[location] != null) {
            throw new IllegalStateException("Field occupied");
        } else if (location == _blockedFiled){
            throw new IllegalStateException("Field is blocked");
        }

        _board[location] = newStone;
        if(checkForSuicide(newStone)){
            _board[location] = null;
            throw new IllegalStateException("Suicide move");
        }

        if(checkAllForKill()){
            resetCheckStatus();
            sendKillSignalToCurrentGroup();
        }

        if(_canBeUnlocked){
            unlockBlockedField();
        }

        currentPlayer = currentPlayer.getOpponent();
    }

    private static void unlockBlockedField() { _blockedFiled = -1; }
    private boolean checkForSuicide(Stone stone){
        boolean commitedKill = false;
        Stone[] neighbours = getNeighbours(stone);
        for(Stone s : neighbours)
            if(s != null)
                if(s.wasntChecked() && !s.getColor().equals("Wall") && !s.getColor().equals(stone.getColor())){
                    resetCheckStatus();
                    if(checkIsGroupIsOutOfBreaths(s)){
                        commitedKill = true;
                        sendKillSignalToCurrentGroup();
                    }
                }

        resetCheckStatus();
        if(commitedKill){ return false; }
        if(checkAllForKill()){ return true; }

        return false;
    }

    private static void resetCheckStatus(){
        for(Stone stone : _board){
            if(stone != null) stone.setWasChecked(false);
        }
    }

    private static void sendKillSignalToCurrentGroup(){
        sendOutputToBothPlayers("KILL");
        for(Stone stone : _currentCheckGroup){
            sendOutputToBothPlayers(String.valueOf(stone.getPosX()));
            sendOutputToBothPlayers(String.valueOf(stone.getPosY()));
            sendOutputToBothPlayers(String.valueOf(stone.getColor()));
            _board[calcPos(stone.getPosX(), stone.getPosY())] = null;
        }
        sendOutputToBothPlayers("KILL_STOP");

        if(_currentCheckGroup.size() == 1){
            _blockedFiled = calcPos(_currentCheckGroup.get(0).getPosX(), _currentCheckGroup.get(0).getPosY());
            _canBeUnlocked = false;
        }
    }

    private static void sendOutputToBothPlayers(String out){
        currentPlayer.sendOutput(out);
        currentPlayer.getOpponent().sendOutput(out);
    }

    private static boolean checkAllForKill(){
        boolean commitedKill = false;
        for(Stone stone : _board)
            if(stone != null)
                if(stone.wasntChecked())
                    commitedKill = commitedKill || checkIsGroupIsOutOfBreaths(stone);

        resetCheckStatus();
        return commitedKill;
    }

    private static boolean checkIsGroupIsOutOfBreaths(Stone stone){
        _currentCheckGroup.clear();
        return  !scoutForBreath(stone);
    }

    private static boolean scoutForBreath(Stone stone){
        _currentCheckGroup.add(stone);
        Stone[] neighbours = getNeighbours(stone);

        boolean foundBreath = false;

        for(Stone s : neighbours) {
            if(s != null){
                if(stone.getColor().equals(s.getColor()) && s.wasntChecked()){
                    s.setWasChecked(true);
                    foundBreath = foundBreath || scoutForBreath(s);
                }

            }else{
                setNeighboursToChecked(stone);
                return true;
            }
        }

        return foundBreath;
    }

    private static void setNeighboursToChecked(Stone stone){
        Stone[] neighbours = getNeighbours(stone);

        for(Stone s : neighbours) {
            if(s != null){
                if(stone.getColor().equals(s.getColor()) && s.wasntChecked()){
                    s.setWasChecked(true);
                    setNeighboursToChecked(s);
                }
            }
        }
    }

    private static Stone[] getNeighbours(Stone stone){
        Stone[] neighbours = new Stone[4];
        int x = stone.getPosX(),
            y = stone.getPosY();

        if(x-1 < 0) neighbours[0] = new Stone(x-1, y, "Wall");
        else neighbours[0] = _board[calcPos(x-1, y)];

        if(y-1 < 0) neighbours[1] = new Stone(x, y-1, "Wall");
        else neighbours[1] = _board[calcPos(x, y-1)];

        if(x+1 >= _size) neighbours[2] = new Stone(x+1, y, "Wall");
        else neighbours[2] = _board[calcPos(x+1, y)];

        if(y+1 >= _size) neighbours[3] = new Stone(x, y+1, "Wall");
        else neighbours[3] = _board[calcPos(x, y+1)];

        return neighbours;
    }

    private static int calcPos(int x, int y){ return x + y*_size; }
    static int getBoardSize(){ return _size; }
    public Player createPlayer(Socket socket, String color){ return new Player(socket, color, this); }
}
