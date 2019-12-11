package goGame.GameLogic;


import goGame.Server.Bot.Bot;

import java.net.Socket;
import java.util.ArrayList;

public class Game {
    private volatile Stone[] _board;
    private int _boardSize;
    private ArrayList<Stone> _currentCheckGroup;
    private int _currentTerritory;
    private int _blockedFiled;
    private boolean _canBeUnlocked;
    public boolean _finished;
    private String _name;

    public volatile IPlayer currentPlayer;

    public Game(int size){
        _currentCheckGroup = new ArrayList<>();
        _board = new Stone[size*size];
        _boardSize = size;
        _blockedFiled = -1;
        _finished = false;
        _name = "Defualt Game.";
        resetBoard();
    }

    public synchronized void move(int x, int y, IPlayer player) {
        int location = calcPos(x, y);
        Stone newStone = currentPlayer.getColor().equals("Black") ? new Stone(x, y, "Black") : new Stone(x, y, "White") ;
        _canBeUnlocked = true;

        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.getOpponent() == null) {
            throw new IllegalStateException("Opponent not present");
        } else if (!_board[location].getColor().equals("Empty")) {
            throw new IllegalStateException("Field occupied");
        } else if (location == _blockedFiled){
            throw new IllegalStateException("Field is blocked");
        }

        _board[location] = newStone;

        if(checkForSuicide(newStone)){
            _board[location] = new Stone(getXFromBoard(location), getYFromBoard(location), "Empty");
            throw new IllegalStateException("Suicide move");
        }

        if(checkIfCommitedKill(newStone)){ sendKillSignalToCurrentGroup(); }

        if(_canBeUnlocked){
            unlockBlockedField();
        }
        System.out.println("Territory: " + currentPlayer.getColor() + " " + calculateTerritory(currentPlayer.getColor()));
        currentPlayer = currentPlayer.getOpponent();
    }

    public synchronized boolean checkForCorrectMove(Stone newStone, IPlayer player) {
        int location = calcPos(newStone.getPosX(), newStone.getPosY());

        if (player != currentPlayer || player.getOpponent() == null ||
            !_board[location].getColor().equals("Empty") || location == _blockedFiled )
                return false;

        _board[location] = newStone;

        if(checkForSuicide(newStone)){
            _board[location] = new Stone(getXFromBoard(location), getYFromBoard(location), "Empty");
            return false;
        }

        _board[location] = new Stone(getXFromBoard(location), getYFromBoard(location), "Empty");
        return true;
    }

    private void unlockBlockedField() { _blockedFiled = -1; }
    public synchronized boolean checkForSuicide(Stone stone){
        if(checkIfCommitedKill(stone)){ return false; }
        if(checkIsGroupIsOutOfBreaths(stone)){ return true; }

        return false;
    }

    public boolean checkIfCommitedKill(Stone stone){
        int location = calcPos(stone.getPosX(), stone.getPosY());
        boolean mockChecking = false;
        if(_board[location].getColor().equals("Empty")){
            mockChecking = true;
            _board[location] = stone;
        }

        resetCheckStatus();
        Stone[] neighbours = getNeighbours(stone);
        for(Stone s : neighbours)
            if(!s.getColor().equals("Empty"))
                if(s.wasntChecked() && !s.getColor().equals("Wall") && !s.getColor().equals(stone.getColor())){
                    resetCheckStatus();
                    if(checkIsGroupIsOutOfBreaths(s)){
                        if(mockChecking)
                            _board[location] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
                        return true;
                    }
                }
        if(mockChecking)
            _board[location] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
        return false;
    }

    private void resetCheckStatus(){
        for(Stone stone : _board){
            if(!stone.getColor().equals("Empty")){
                stone.setWasChecked(false);
                stone.setIsSafe(false);
            }
            stone.setWasCheckedTerritory(false);
            stone.setIsPartOfTerritory(true);
        }
    }

    private void sendKillSignalToCurrentGroup(){
        sendOutputToBothPlayers("KILL");
        for(Stone stone : _currentCheckGroup){
            sendOutputToBothPlayers(String.valueOf(stone.getPosX()));
            sendOutputToBothPlayers(String.valueOf(stone.getPosY()));
            sendOutputToBothPlayers(String.valueOf(stone.getColor()));
            _board[calcPos(stone.getPosX(), stone.getPosY())] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
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
        boolean test = scoutForBreath(stone);

        return  !test;
    }

    private boolean scoutForBreath(Stone stone){
        _currentCheckGroup.add(stone);
        Stone[] neighbours = getNeighbours(stone);
        stone.setWasChecked(true);
        boolean foundBreath = false;

        for(Stone s : neighbours) {
            if(!s.getColor().equals("Empty")){
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
            if(!s.getColor().equals("Empty")){
                if(stone.getColor().equals(s.getColor()) && !s.isSafe()){
                    s.setIsSafe(true);
                    setNeighboursToSafe(s);
                }
            }
        }
    }

    public Stone[] getNeighbours(Stone stone){
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

    public Stone[] getCornerNeighbours(Stone stone){
        Stone[] neighbours = new Stone[4];
        int x = stone.getPosX(),
                y = stone.getPosY();
        if(y+1 >= _boardSize || x-1 < 0) neighbours[1] = new Stone(x-1, y+1, "Wall");
        else neighbours[0] = _board[calcPos(x-1, y+1)];

        if(x-1 < 0 || y-1 < 0) neighbours[0] = new Stone(x-1, y-1, "Wall");
        else neighbours[1] = _board[calcPos(x-1, y-1)];

        if(y-1 < 0 || x+1 >= _boardSize) neighbours[1] = new Stone(x+1, y-1, "Wall");
        else neighbours[2] = _board[calcPos(x+1, y-1)];

        if(x+1 >= _boardSize || y+1 >= _boardSize) neighbours[2] = new Stone(x+1, y+1, "Wall");
        else neighbours[3] = _board[calcPos(x+1, y+1)];

        return neighbours;
    }

    public int calculateTerritoryWithNewStone(Stone stone){
        int location = calcPos(stone.getPosX(), stone.getPosY()),
            newTerritory = 0;
        if(_board[location].getColor().equals("Empty")){
            _board[location] = stone;
            newTerritory = calculateTerritory(stone.getColor());
            _board[location] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
        }

        return newTerritory;
    }

    public int calculateTerritory(String color){
        int wholeTerritory=0;
        _currentTerritory=0;
        for(Stone s : _board){
            if(!s.wasCheckedTerritory()){
                findTerritory(s, color);
                if(s.isPartOfTerritory())
                    wholeTerritory+=_currentTerritory;
                _currentTerritory=0;
            }
        }
        return wholeTerritory;
    }

    private void findTerritory(Stone stone, String color){
        stone.setWasCheckedTerritory(true);
        if(stone.getColor().equals("Empty")){
            _currentTerritory++;
            Stone[] neighbours = getNeighbours(stone);
            for(Stone ns : neighbours){
                if(ns.getColor().equals("Empty")){
                    if(!ns.wasCheckedTerritory())
                        findTerritory(ns, color);
                }
                else if(!ns.getColor().equals(color) && !ns.getColor().equals("Wall")){
                    stone.setIsPartOfTerritory(false);
                    setNeighboursThatTheyAreActuallyNotAPartOfTheirTerritoryUnfortunately(stone);
                    return;
                }
            }
        }
    }

    private void setNeighboursThatTheyAreActuallyNotAPartOfTheirTerritoryUnfortunately(Stone stone){
        Stone[] neighbours = getNeighbours(stone);
        for(Stone s : neighbours) {
            if(s.getColor().equals("Empty")){
                if(s.isPartOfTerritory()){
                    s.setIsPartOfTerritory(false);
                    setNeighboursThatTheyAreActuallyNotAPartOfTheirTerritoryUnfortunately(s);
                }
            }
        }
    }

    private int calcPos(int x, int y){ return x + y*_boardSize; }
    public int getBoardSize(){ return _boardSize; }
    public IPlayer getCurrentPlayer(){return currentPlayer;}
    private int getXFromBoard(int i){return i%_boardSize;}
    private int getYFromBoard(int i){return i/_boardSize;}
    public String getName(){return _name;}
    private void resetBoard(){
        for(int i=0; i<_boardSize*_boardSize; i++){
            _board[i] = new Stone(getXFromBoard(i), getYFromBoard(i), "Empty");
        }
    }
    public IPlayer createPlayer(Socket socket, String color){
        if(color.equalsIgnoreCase("Bot"))
            return new Bot(socket, "White", this);
        else
            return new Player(socket, color, this);
    }
    public void skipMove(){
        currentPlayer=currentPlayer.getOpponent();
    }
}
