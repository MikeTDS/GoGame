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
        _pointsMap.put("wrongMove", -1000000);
    }

    @Override
    public int findBestField(Bot player){
        resetPointsBoard();
        for(int i=0; i< _brain.getBoardSize()*_brain.getBoardSize(); i++){
            int x = _brain.getXFromBoard(i),
                    y = _brain.getYFromBoard(i),
                    divisor = _brain.getBoardSize()/4;

            Stone allyStone = new Stone(_brain.getXFromBoard(i), _brain.getYFromBoard(i), player.getColor());
            if(_brain.checkForWrongMove(allyStone, player)) _pointsBoard[i] += _pointsMap.get("wrongMove");
            if(x > divisor && x < _brain.getBoardSize() - divisor && y > divisor && y < _brain.getBoardSize() - divisor) _pointsBoard[i] += _pointsMap.get("goodMove");
        }
        return chooseBestField();
    }
}
