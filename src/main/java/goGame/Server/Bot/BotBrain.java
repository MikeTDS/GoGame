package goGame.Server.Bot;

import goGame.GameLogic.Game;
import goGame.GameLogic.IPlayer;
import goGame.GameLogic.Stone;

public class BotBrain {
    private Game _game;
    private Stone[] _board;
    private int _blockedField;
    private int _boardSize;

    BotBrain(Game game, int boardsize){
        _game = game;
        _boardSize = boardsize;
    }

    void setBrainForRound(Stone[] board, int blockedFiled){
        _board = board;
        _blockedField = blockedFiled;
    }

    boolean checkForCorrectMove(Stone newStone, IPlayer player) {
        int location = calcPos(newStone.getPosX(), newStone.getPosY());

        if (player != _game.getCurrentPlayer() || player.getOpponent() == null ||
                !_board[location].getColor().equals("Empty") || location == _blockedField )
            return false;

        _board[location] = newStone;

        if(_game.checkForSuicide(newStone)){
            _board[location] = new Stone(newStone.getPosX(), newStone.getPosY(), "Empty");
            return false;
        }

        _board[location] = new Stone(newStone.getPosX(), newStone.getPosY(), "Empty");
        return true;
    }

    Stone[] getCornerNeighbours(Stone stone){
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

    int calculateTerritoryWithNewStone(Stone stone){
        int location = calcPos(stone.getPosX(), stone.getPosY()),
                newTerritory = 0;
        if(_board[location].getColor().equals("Empty")){
            _board[location] = stone;
            newTerritory = _game.calculateTerritory(stone.getColor());
            _board[location] = new Stone(stone.getPosX(), stone.getPosY(), "Empty");
        }

        return newTerritory;
    }

    private int calcPos(int x, int y){ return x + y*_boardSize; }
    public int getBoardSize() { return _boardSize; }
}
