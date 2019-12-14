package goGame.Server.Bot;

import goGame.GameLogic.Stone;

import java.util.*;

public abstract class MoveState implements IMoveState {

    int[] _pointsBoard;
    Map<String, Integer> _pointsMap = new HashMap<>();
    BotBrain _brain;

    MoveState(int[] pointsBoard, BotBrain brain) {
        _pointsBoard = pointsBoard;
        _brain = brain;
        resetPointsBoard();
    }

    void resetPointsBoard() {
        for(int i=0; i < _pointsBoard.length; i++)
            _pointsBoard[i] = 0;
    }


    int chooseBestField() {
        Random random = new Random();
        int max = Arrays.stream(_pointsBoard).max().getAsInt(),
                  randomedField;
        List<Integer> potentialFields = new ArrayList<>();

        for(int i=0; i < _brain.getBoardSize()*_brain.getBoardSize(); i++)
            if(_pointsBoard[i] == max)
                potentialFields.add(i);

        randomedField = random.nextInt(potentialFields.size());
        return potentialFields.get(randomedField);
    }
}
