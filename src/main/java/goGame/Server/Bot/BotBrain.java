package goGame.Server.Bot;

import goGame.GameLogic.Game;
import goGame.GameLogic.IPlayer;
import goGame.GameLogic.Stone;

public class BotBrain {
    private Game _game;
    private Stone[] _board;
    private int _blockedField;
    private int _boardSize;
    private String _color, _opponentColor;

    BotBrain(Game game, String color, String opponentColor){
        _game = game;
        _boardSize = _game.getBoardSize();
        _color = color;
        _opponentColor = opponentColor;
    }

    void setBrainForRound(){
        _board = _game.getBoard();
        _blockedField = _game.getBlockedField();
    }

    boolean checkForWrongMove(Stone newStone, IPlayer player) {
        int location = calcPos(newStone.getPosX(), newStone.getPosY());

        if (player != _game.getCurrentPlayer() || player.getOpponent() == null ||
                !_board[location].getColor().equals("Empty") || location == _blockedField )
            return true;

        _board[location] = newStone;

        if(_game.checkForSuicide(newStone)){
            _board[location] = new Stone(newStone.getPosX(), newStone.getPosY(), "Empty");
            return true;
        }

        _board[location] = new Stone(newStone.getPosX(), newStone.getPosY(), "Empty");
        return false;
    }

    private Stone[] getCornerNeighbours(Stone stone){
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

    int countEnemyHugs(Stone stone) {
        Stone[] neighbours = _game.getNeighbours(stone);
        Stone[] cornerNeighbours = getCornerNeighbours(stone);
        int cornerJumper = 0,
                hugCounter = 0;
        for(Stone s : neighbours){
            if(s != null)
                if(s.getColor().equals(_opponentColor)){
                    if(cornerNeighbours[cornerJumper%4] != null)
                        if(cornerNeighbours[cornerJumper%4].getColor().equals(_color))
                            hugCounter++;
                    if(cornerNeighbours[(cornerJumper+1)%4] != null)
                        if(cornerNeighbours[(cornerJumper+1)%4].getColor().equals(_color))
                            hugCounter++;
                }
            cornerJumper++;
        }

        return hugCounter;
    }

    int countEnemyNeighbours(Stone stone) {
        int countEnemy = 0;
        Stone[] neighbours = _game.getNeighbours(stone);
        for(Stone s : neighbours){
            if(s != null)
                if(s.getColor().equals(_opponentColor))
                    countEnemy++;
        }

        return countEnemy;
    }

    boolean checkForKill(Stone stone) { return _game.checkIfCommitedKill(stone); }
    boolean checkForChain(Stone stone) { return countStonesOfGivenColorAround(stone, _color) == 1; }
    boolean checkForFriendHug(Stone stone) { return countStonesOfGivenColorAround(stone, _color) > 1; }
    boolean checkForEnemyEqualization(Stone stone) {
            return ((countStonesOfGivenColorAround(stone, _color)) + 1)== countStonesOfGivenColorAround(stone,_opponentColor)
            && countStonesOfGivenColorAround(stone, _color) > 0
            && countStonesOfGivenColorAround(stone, _opponentColor) > 0; }
    boolean checkForEnemyOutnumber(Stone stone) {
            return ((countStonesOfGivenColorAround(stone, _color)) + 1) > countStonesOfGivenColorAround(stone,_opponentColor)
            && countStonesOfGivenColorAround(stone, _opponentColor) > 0; }
    private int countStonesOfGivenColorAround(Stone stone, String clr) {
        Stone[] neighbours = _game.getNeighbours(stone);
        Stone[] cornerNeighbours = getCornerNeighbours(stone);
        int countClr = 0;
        for (int i = 0; i < neighbours.length; i++) {
            Stone s = neighbours[i];
            if (s != null)
                if (s.getColor().equals(clr))
                    countClr++;
        }
        for(Stone s : cornerNeighbours){
            if(s != null)
                if(s.getColor().equals(clr))
                    countClr++;
        }
        return countClr;
    }

    boolean checkForTerritoryExpansion(Stone stone) {
        int currentTerritory = _game.calculateTerritory(stone.getColor());
        int newTerritory = calculateTerritoryWithNewStone(stone);

        return newTerritory > currentTerritory;
    }

    boolean checkForTerritoryShrink(Stone stone) {
        int currentTerritory = _game.calculateTerritory(stone.getColor());
        int newTerritory = calculateTerritoryWithNewStone(stone);

        return newTerritory < currentTerritory && newTerritory != 0;
    }

    boolean checkForSemiSuicidalMove(Stone stone) {
        Stone[] neighbours = _game.getNeighbours(stone);
        for(Stone nei : neighbours)
            if(nei.getColor().equals("Empty")){
                Stone mock = new Stone(nei.getPosX(), nei.getPosY(), _opponentColor);
                if(_game.checkIfCommitedKillForTwo(stone, mock))
                    return true;
            }

        return false;
    }

    int getXFromBoard(int i){
        if(i == 0)
            return 0;
        else
            return i%_game.getBoardSize();
    }
    int getYFromBoard(int i) {
        if (i == 0)
            return 0;
        else
            return i / _game.getBoardSize();
    }

    private int calcPos(int x, int y){ return x + y*_boardSize; }
    int getBoardSize() { return _boardSize; }
    int getSizeOfKillGroups() { return _game.getSizeOfKillGroups(); }
    int calculateTerritory(String color) { return _game.calculateTerritory(color); }
    int calculateStonesOnTheBoard(){
        int counter = 0;
        for(Stone stone : _board)
            if(!stone.getColor().equals("Empty"))
                counter++;

        return counter;
    }
}
