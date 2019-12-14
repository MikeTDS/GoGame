package goGame.Server.Bot;

import goGame.GameLogic.Stone;

class EarlyMoveState extends MoveState{

    EarlyMoveState(int[] pointsBoard, BotBrain botBrain) {

        super(pointsBoard, botBrain);
        setPointsSystem();
    }

    @Override
    public void setPointsSystem() {
        _pointsMap.put("goodMove", 1000000);
        _pointsMap.put("killProtection", 100);
        _pointsMap.put("friendNeighbour", -50);
        _pointsMap.put("wrongMove", -1000000);
    }

    @Override
    public int findBestField(Bot player){
        resetPointsBoard();
        for(int i=0; i< _brain.getBoardSize()*_brain.getBoardSize(); i++){
            int x = _brain.getXFromBoard(i),
                y = _brain.getYFromBoard(i),
                divisor = _brain.getBoardSize()/4;

            Stone enemyTestStone = new Stone(x, y, player.getOpponent().getColor());
            Stone allyStone = new Stone(x, y, player.getColor());
            if(_brain.checkForWrongMove(allyStone, player)) _pointsBoard[i] += _pointsMap.get("wrongMove");
            if(_brain.countStonesOfGivenColorAround(allyStone, allyStone.getColor()) > 0) _pointsBoard[i] += _pointsMap.get("friendNeighbour");
            if(x > divisor && x < _brain.getBoardSize() - divisor && y > divisor && y < _brain.getBoardSize() - divisor) _pointsBoard[i] += _pointsMap.get("goodMove");
            if(_brain.checkForKill(enemyTestStone)) _pointsBoard[i] += _pointsMap.get("killProtection");
        }
        return chooseBestField();
    }
}
