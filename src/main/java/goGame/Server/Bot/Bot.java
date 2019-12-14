package goGame.Server.Bot;

import goGame.GameLogic.AbstractPlayer;
import goGame.GameLogic.Game;
import goGame.GameLogic.Stone;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;


public class Bot extends AbstractPlayer implements Runnable, IBot {

    private int[] _pointsBoard;
    private Map<String, Integer> _pointsMap = new HashMap<>();
    private BotBrain _brain;

    public Bot(Socket socket, String color, Game game) {
        _color = color;
        _game = game;
        _socket = socket;
    }

    @Override
    public void run() {
        try {
            setup();
            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() throws IOException {
        _input = new Scanner(_socket.getInputStream());
        _output = new PrintWriter(_socket.getOutputStream(), true);
        _opponent = _game.getCurrentPlayer();
        _opponent.setOpponent(this);
        _opponent.getOpponent().getOutput().println("MESSAGE Your move");
        _brain = new BotBrain(_game, _game.getBoardSize(), _color, _opponent.getColor());

        _pointsBoard = new int[_brain.getBoardSize()*_brain.getBoardSize()];
        setPointsSystem();
        resetPointsBoard();
    }

    private void setPointsSystem() {
        _pointsMap.put("kill", 1300);
        _pointsMap.put("chain", 100);
        _pointsMap.put("enemyHug", 200);
        _pointsMap.put("friendHug", 15);
        _pointsMap.put("territoryExpansion", 500);
        _pointsMap.put("enemyOutnumber", 150);
        _pointsMap.put("enemyEqualization", 180);
        _pointsMap.put("enemyKillProtection", 700);
        _pointsMap.put("semiSuicideMove", -700);
        _pointsMap.put("enemyNeighbour", -50);
        _pointsMap.put("wrongMove", -100000);
        _pointsMap.put("territoryShrink", -400);
    }

    private void processMoveCommand(int x, int y) {
        try {
            Thread.sleep(1);
            _game.move(x, y, this);
            _opponent.getOutput().println("OPPONENT_MOVED");
            _opponent.getOutput().println(x);
            _opponent.getOutput().println(y);
        } catch (IllegalStateException | InterruptedException e) {
            System.out.println( e.getMessage());
        }
    }
    //IBot
    @Override
    public void findBestField(){
        resetPointsBoard();
        _brain.setBrainForRound(_game.getBoard(), _game.getBlockedField());
        for(int i=0; i< _brain.getBoardSize()*_brain.getBoardSize(); i++){
            Stone allyStone = new Stone(getXFromBoard(i), getYFromBoard(i), _color);
            Stone enemyTestStone = new Stone(getXFromBoard(i), getYFromBoard(i), _opponent.getColor());
            if(_brain.checkForKill(allyStone)) _pointsBoard[i] += _pointsMap.get("kill") * _game.getSizeOfKillGroups();
            if(_brain.checkForKill(enemyTestStone)) _pointsBoard[i] += _pointsMap.get("enemyKillProtection") * _game.getSizeOfKillGroups();;
            if(_brain.checkForTerritoryExpansion(allyStone)){
                _pointsBoard[i] += _pointsMap.get("territoryExpansion") * (_brain.calculateTerritoryWithNewStone(allyStone) - _game.calculateTerritory(_color));
            }
            if(_brain.checkForChain(allyStone)) _pointsBoard[i] += _pointsMap.get("chain");
            if(_brain.checkForFriendHug(allyStone)) _pointsBoard[i] += _pointsMap.get("friendHug");
            if(_brain.checkForEnemyOutnumber(allyStone)) _pointsBoard[i] += _pointsMap.get("enemyOutnumber");
            if(_brain.checkForEnemyEqualization(allyStone)) _pointsBoard[i] += _pointsMap.get("enemyEqualization");
            if(_brain.checkForTerritoryShrink(allyStone)) _pointsBoard[i] += _pointsMap.get("territoryShrink") * (_game.calculateTerritory(_color) - _brain.calculateTerritoryWithNewStone(allyStone));
            if(!_brain.checkForCorrectMove(allyStone, this)) _pointsBoard[i] += _pointsMap.get("wrongMove");
            //if(_brain.checkForSemiSuicuidalMove(allyStone)) _pointsBoard[i] += _pointsMap.get("semiSuicideMove");

            _pointsBoard[i] += _pointsMap.get("enemyHug")*_brain.countEnemyHugs(allyStone);
            _pointsBoard[i] += _pointsMap.get("enemyNeighbour")*_brain.countEnemyNeighbours(allyStone);
        }
        int choosenField = chooseBestField();
        processMoveCommand(getXFromBoard(choosenField), getYFromBoard(choosenField));
    }

    private void resetPointsBoard() {
        for(int i=0; i < _pointsBoard.length; i++)
            _pointsBoard[i] = 0;
    }

    private int chooseBestField() {
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

    @Override
    public void sendOutput(String out) { }

    @Override
    public void makeMove(int x, int y) {

    }

    @Override
    public void decideIfPass() {

    }

    private void play(){
        while(true){
            if(_game.getCurrentPlayer().equals(this)){
                updatePoints();
               findBestField();
            }
        }
    }
    private int getXFromBoard(int i){
        if(i == 0)
            return 0;
        else
            return i%_game.getBoardSize();
    }
    private int getYFromBoard(int i){
        if(i == 0)
            return 0;
        else
            return i/_game.getBoardSize();
    }

    private void setPointsBoard(){

    }
    private void calculateBoard(){

    }
}
