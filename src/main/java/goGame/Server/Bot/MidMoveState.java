package goGame.Server.Bot;

import goGame.GameLogic.Stone;

class MidMoveState extends MoveState{

    MidMoveState(int[] pointsBoard, BotBrain botBrain) {
        super(pointsBoard, botBrain);
        setPointsSystem();
    }

    @Override
    public void setPointsSystem() {
        _pointsMap.put("kill", 1000);
        _pointsMap.put("chain", 100);
        _pointsMap.put("enemyHug", 200);
        _pointsMap.put("friendHug", -100);
        _pointsMap.put("territoryExpansion", 200);
        _pointsMap.put("enemyOutnumber", 220);
        _pointsMap.put("enemyEqualization", 290);
        _pointsMap.put("enemyKillProtection", 780);
        _pointsMap.put("semiSuicideMove", -850);
        _pointsMap.put("enemyNeighbour", -25);
        _pointsMap.put("plug", -50);
        _pointsMap.put("wrongMove", -100000);
        _pointsMap.put("territoryShrink", -400);
    }

    @Override
    public int findBestField(Bot player){
        resetPointsBoard();
        for(int i=0; i< _brain.getBoardSize()*_brain.getBoardSize(); i++){
            Stone allyStone = new Stone(_brain.getXFromBoard(i), _brain.getYFromBoard(i), player.getColor());
            Stone enemyTestStone = new Stone(_brain.getXFromBoard(i), _brain.getYFromBoard(i),  player.getOpponent().getColor());
            if(_brain.checkForTerritoryExpansion(allyStone)) _pointsBoard[i] += _pointsMap.get("territoryExpansion") * (_brain.calculateTerritoryWithNewStone(allyStone) - _brain.calculateTerritory(player.getColor()));
            if(_brain.checkForChain(allyStone)) _pointsBoard[i] += _pointsMap.get("chain");
            if(_brain.checkForFriendHug(allyStone)) _pointsBoard[i] += _pointsMap.get("friendHug");
            if(_brain.checkForEnemyOutnumber(allyStone)) _pointsBoard[i] += _pointsMap.get("enemyOutnumber");
            if(_brain.checkForEnemyEqualization(allyStone)) _pointsBoard[i] += _pointsMap.get("enemyEqualization");
            if(_brain.checkForTerritoryShrink(allyStone)) _pointsBoard[i] += _pointsMap.get("territoryShrink") * (_brain.calculateTerritory(player.getColor()) - _brain.calculateTerritoryWithNewStone(allyStone));
            if(_brain.checkForWrongMove(allyStone, player)) _pointsBoard[i] += _pointsMap.get("wrongMove");
            if(_brain.checkForPlug(allyStone)) _pointsBoard[i] += _pointsMap.get("plug");
            if(_brain.checkForWrongMove(enemyTestStone, player.getOpponent())) _pointsBoard[i] += _pointsMap.get("wrongMove");
            if(_brain.checkForKill(allyStone)) _pointsBoard[i] += _pointsMap.get("kill") * _brain.getNumberOfStonesInKillGroups();
            if(_brain.checkForKill(enemyTestStone)){
                int killingSpreeSize = _brain.getNumberOfStonesInKillGroups();
                _pointsBoard[i] += _pointsMap.get("enemyKillProtection") * killingSpreeSize;
                if(_brain.checkForSemiSuicidalMove(allyStone, allyStone))
                    _pointsBoard[i] += _pointsMap.get("semiSuicideMove") * killingSpreeSize;
            }else if(_brain.checkForSemiSuicidalMove(allyStone, allyStone)) _pointsBoard[i] += _pointsMap.get("semiSuicideMove");

            _pointsBoard[i] += _pointsMap.get("enemyHug")*_brain.countEnemyHugs(allyStone);
            _pointsBoard[i] += _pointsMap.get("enemyNeighbour")*_brain.countEnemyNeighbours(allyStone);
        }
        return chooseBestField();
    }
}
