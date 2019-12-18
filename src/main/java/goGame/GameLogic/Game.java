package goGame.GameLogic;


import goGame.Server.Bot.Bot;

import java.net.Socket;
import java.util.ArrayList;

public class Game {
    private volatile Stone[] _board;
    private int _boardSize;
    private ArrayList<Stone> _currentCheckGroup;
    private int _currentCheckGroupBreaths;
    private ArrayList<ArrayList<Stone>> _killGroups;
    private int _currentTerritory;
    private volatile int _blockedField;
    private boolean _canBeUnlocked;
    public boolean _finished;
    private String _name;

    IPlayer currentPlayer;

    public Game(int size){
        _currentCheckGroup = new ArrayList<>();
        _killGroups = new ArrayList<>();
        _board = new Stone[size*size];
        _boardSize = size;
        _blockedField = -1;
        _finished = false;
        _name = "Default Game.";
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
        } else if (location == _blockedField){
            throw new IllegalStateException("Field is blocked");
        }

        _board[location] = newStone;

        if(checkForSuicide(newStone)){
            _board[location] = new Stone(getXFromBoard(location), getYFromBoard(location), "Empty");
            throw new IllegalStateException("Suicide move");
        }

        if(checkIfCommitedKill(newStone)){
            sendKillSignalToKillGroups(); }

        if(_canBeUnlocked){
            unlockBlockedField();
        }
        currentPlayer.updatePoints();
        currentPlayer.sendPoints();

        currentPlayer = currentPlayer.getOpponent();
    }

    private void unlockBlockedField() { _blockedField = -1; }
    public synchronized boolean checkForSuicide(Stone stone){
        if(checkIfCommitedKill(stone)){ return false; }
        return checkIfGroupIsOutOfBreaths(stone);
    }

    public boolean checkIfCommitedKillForTwo(Stone stone1, Stone stone2){
        int location = calcPos(stone1.getPosX(), stone1.getPosY());

        boolean mockChecking = false,
                commitedKill;
        if(_board[location].getColor().equals("Empty")){
            mockChecking = true;
            _board[location] = stone1;
        }
        commitedKill = checkIfCommitedKill(stone2);

        if(mockChecking)
            _board[location] = new Stone(stone1.getPosX(), stone1.getPosY(), "Empty");

        return commitedKill;
    }

    public boolean checkIfCommitedKill(Stone stone){
        _killGroups.clear();
        int location = calcPos(stone.getPosX(), stone.getPosY());
        boolean mockChecking = false,
                commitedKill = false;
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
                    if(checkIfGroupIsOutOfBreaths(s)){
                        commitedKill = true;
                        ArrayList<Stone> gck = (ArrayList<Stone>)_currentCheckGroup.clone();
                        _killGroups.add(gck);
                    }
                }
        if(mockChecking)
            _board[location] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
        return commitedKill;
    }

    public void resetCheckStatus(){
        for(Stone stone : _board){
            if(!stone.getColor().equals("Empty"))
                stone.setWasChecked(false);

            stone.setWasCheckedTerritory(false);
            stone.setIsPartOfTerritory(true);
        }
    }

    private void sendKillSignalToKillGroups(){
        sendOutputToBothPlayers("KILL");
        for(ArrayList<Stone> currentCheckGroup : _killGroups){
            for(Stone stone : currentCheckGroup){
                sendOutputToBothPlayers(String.valueOf(stone.getPosX()));
                sendOutputToBothPlayers(String.valueOf(stone.getPosY()));
                sendOutputToBothPlayers(String.valueOf(stone.getColor()));
                _board[calcPos(stone.getPosX(), stone.getPosY())] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
                currentPlayer.addKillPoints(1);
            }
            if(currentCheckGroup.size() == 1){
                _blockedField = calcPos(currentCheckGroup.get(0).getPosX(), currentCheckGroup.get(0).getPosY());
                _canBeUnlocked = false;
            }
        }
        sendOutputToBothPlayers("KILL_STOP");
    }

    private void sendOutputToBothPlayers(String out){
        currentPlayer.sendOutput(out);
        currentPlayer.getOpponent().sendOutput(out);
    }

    public boolean checkIfGroupIsOutOfBreaths(Stone stone){
        _currentCheckGroupBreaths = 0;
        _currentCheckGroup.clear();
        scoutForBreath(stone);
        return _currentCheckGroupBreaths == 0;
    }

    private void scoutForBreath(Stone stone){
        _currentCheckGroup.add(stone);
        Stone[] neighbours = getNeighbours(stone);
        stone.setWasChecked(true);

        for(Stone s : neighbours) {
            if(!s.getColor().equals("Empty")){
                if(stone.getColor().equals(s.getColor()) && s.wasntChecked()){
                    s.setWasChecked(true);
                    scoutForBreath(s);
                }
            }else{
                _currentCheckGroupBreaths++;
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

    public int calculateTerritory(String color){
        resetCheckStatus();
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

    public int getNumberOfStonesInKillGroups() {
        int counter = 0;
        for(ArrayList<Stone> group : _killGroups)
            counter += group.size();
        return counter; }

    private void resetBoard(){
        for(int i=0; i<_boardSize*_boardSize; i++){
            _board[i] = new Stone(getXFromBoard(i), getYFromBoard(i), "Empty");
        }
    }

    public void skipMove(){ currentPlayer=currentPlayer.getOpponent(); }

    public synchronized IPlayer createPlayer(Socket socket, String color){
        if(color.equalsIgnoreCase("Bot"))
            synchronized (this){
                return new Bot(socket, "White", this);
            }
        else
            return new Player(socket, color, this);
    }

    public String getName(){return _name;}
    public Stone[] getBoard() { return _board; }
    public int getBlockedField(){ return _blockedField; }
    public int getCurrentCheckGroupBreaths() { return _currentCheckGroupBreaths; }
    public int getBoardSize(){ return _boardSize; }
    public IPlayer getCurrentPlayer(){return currentPlayer;}
    private int calcPos(int x, int y){ return x + y*_boardSize; }
    private int getXFromBoard(int i){return i%_boardSize;}
    private int getYFromBoard(int i){return i/_boardSize;}
}
