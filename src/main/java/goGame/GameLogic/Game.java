package goGame.GameLogic;


import goGame.Server.Bot.Bot;

import java.net.Socket;
import java.util.ArrayList;

public class Game {
    private Stone[] _board;
    private int _boardSize;
    private ArrayList<Stone> _currentCheckGroup;
    private int _blockedFiled;
    private boolean _canBeUnlocked;
    public boolean _finished;

    public volatile IPlayer currentPlayer;

    public Game(int size){
        _currentCheckGroup = new ArrayList<>();
        _board = new Stone[size*size];
        _boardSize = size;
        _blockedFiled = -1;
        _finished = false;
    }

    public synchronized void move(int x, int y, IPlayer player) {
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

        if(_canBeUnlocked){
            unlockBlockedField();
        }

        currentPlayer = currentPlayer.getOpponent();
    }

    private void unlockBlockedField() { _blockedFiled = -1; }
    public synchronized boolean checkForSuicide(Stone stone){
        resetCheckStatus();
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

        if(commitedKill){ return false; }
        if(checkIsGroupIsOutOfBreaths(stone)){ return true; }

        return false;
    }

    private void resetCheckStatus(){
        for(Stone stone : _board){
            if(stone != null){
                stone.setWasChecked(false);
                stone.setIsSafe(false);
            }
        }
    }

    private void sendKillSignalToCurrentGroup(){
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

    private void sendOutputToBothPlayers(String out){
        currentPlayer.sendOutput(out);
        currentPlayer.getOpponent().sendOutput(out);
    }

    private boolean checkIsGroupIsOutOfBreaths(Stone stone){
        _currentCheckGroup.clear();

        return  !scoutForBreath(stone);
    }

    private boolean scoutForBreath(Stone stone){
        _currentCheckGroup.add(stone);
        Stone[] neighbours = getNeighbours(stone);
        stone.setWasChecked(true);
        boolean foundBreath = false;

        for(Stone s : neighbours) {
            if(s != null){
                boolean test = s.wasntChecked();
                if(stone.getColor().equals(s.getColor()) && s.wasntChecked()){
                    s.setWasChecked(true);
                    foundBreath = foundBreath || scoutForBreath(s);
                }

            }else{
                stone.setIsSafe(true);
                setNeighboursToSafe(stone);
                return true;
            }
        }
        if(foundBreath){
            stone.setIsSafe(true);
            setNeighboursToSafe(stone);
        }
        return foundBreath;
    }

    private void setNeighboursToSafe(Stone stone){
        Stone[] neighbours = getNeighbours(stone);
        for(Stone s : neighbours) {
            if(s != null){
                if(stone.getColor().equals(s.getColor()) && !s.isSafe()){
                    s.setIsSafe(true);
                    setNeighboursToSafe(s);
                }
            }
        }
    }

    private Stone[] getNeighbours(Stone stone){
        Stone[] neighbours = new Stone[4];
        int x = stone.getPosX(),
            y = stone.getPosY();

        if(x-1 < 0) neighbours[0] = new Stone(x-1, y, "Wall");
        else neighbours[0] = _board[calcPos(x-1, y)];

        if(y-1 < 0) neighbours[1] = new Stone(x, y-1, "Wall");
        else neighbours[1] = _board[calcPos(x, y-1)];

        if(x+1 >= _boardSize) neighbours[2] = new Stone(x+1, y, "Wall");
        else neighbours[2] = _board[calcPos(x+1, y)];

        if(y+1 >= _boardSize) neighbours[3] = new Stone(x, y+1, "Wall");
        else neighbours[3] = _board[calcPos(x, y+1)];

        return neighbours;
    }

    private int calcPos(int x, int y){ return x + y*_boardSize; }
    public int getBoardSize(){ return _boardSize; }
    public IPlayer createPlayer(Socket socket, String color){
        if(color.equalsIgnoreCase("Bot"))
            return new Bot(socket, "White", this);
        else
            return new Player(socket, color, this);
    }

    public void skipMove(){
        currentPlayer=currentPlayer.getOpponent();
    }
    public IPlayer getCurrentPlayer(){return currentPlayer;}
    public Stone[] getBoard(){return _board;}
}
