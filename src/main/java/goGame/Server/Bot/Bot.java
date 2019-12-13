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
    private int _boardsize;

    public Bot(Socket socket, String color, Game game) {
        _color = color;
        _game = game;
        _socket = socket;
        _boardsize = _game.getBoardSize();
        _pointsBoard = new int[_boardsize*_boardsize];
        setPointsSystem();
    }

    private void setPointsSystem() {
        _pointsMap.put("kill", 1300);
        _pointsMap.put("chain", 50);
        _pointsMap.put("enemyHug", 200);
        _pointsMap.put("friendHug", 15);
        _pointsMap.put("territoryExpansion", 500);
        _pointsMap.put("enemyOutnumber", 150);
        _pointsMap.put("enemyEqualization", 180);
        _pointsMap.put("enemyKillProtection", 700);
        _pointsMap.put("enemyNeighbour", -50);
        _pointsMap.put("wrongMove", -100000);
        _pointsMap.put("territoryShrink", -400);
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
        resetPoints();
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
        resetPoints();
        for(int i=0; i< _boardsize*_boardsize; i++){
            Stone allyStone = new Stone(getXFromBoard(i), getYFromBoard(i), _color);
            Stone enemyTestStone = new Stone(getXFromBoard(i), getYFromBoard(i), _opponent.getColor());
            if(checkForKill(allyStone)) _pointsBoard[i] += _pointsMap.get("kill") * _game.getSizeOfKillGroups();
            if(checkForKill(enemyTestStone)) _pointsBoard[i] += _pointsMap.get("enemyKillProtection");
            if(checkForTerritoryExpansion(allyStone)) _pointsBoard[i] += _pointsMap.get("territoryExpansion");
            if(checkForChain(allyStone)) _pointsBoard[i] += _pointsMap.get("chain");
            if(checkForFriendHug(allyStone)) _pointsBoard[i] += _pointsMap.get("friendHug");
            if(checkForEnemyOutnumber(allyStone)) _pointsBoard[i] += _pointsMap.get("enemyOutnumber");
            if(checkForEnemyEqualization(allyStone)) _pointsBoard[i] += _pointsMap.get("enemyEqualization");
            if(checkForTerritoryShrink(allyStone)) _pointsBoard[i] += _pointsMap.get("territoryShrink");
            if(!checkForCorrectMove(allyStone)) _pointsBoard[i] += _pointsMap.get("wrongMove");

            _pointsBoard[i] += _pointsMap.get("enemyHug")*countEnemyHugs(allyStone);
            _pointsBoard[i] += _pointsMap.get("enemyNeighbour")*countEnemyNeighbours(allyStone);
        }
        int choosenField = chooseBestField();
        processMoveCommand(getXFromBoard(choosenField), getYFromBoard(choosenField));
    }

    private int chooseBestField() {
        Random random = new Random();
        int max = Arrays.stream(_pointsBoard).max().getAsInt(),
            randomedField;
        List<Integer> potentialFields = new ArrayList<>();
        for(int i=0; i < _boardsize*_boardsize; i++)
            if(_pointsBoard[i] == max)
                potentialFields.add(i);
        randomedField = random.nextInt(potentialFields.size());
        return potentialFields.get(randomedField);
    }

    private int countEnemyHugs(Stone stone) {
        Stone[] neighbours = _game.getNeighbours(stone);
        Stone[] cornerNeighbours = _game.getCornerNeighbours(stone);
        int cornerJumper = 0,
            hugCounter = 0;
        for(Stone s : neighbours){
            if(s != null)
                if(s.getColor().equals(_opponent.getColor())){
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

    private int countEnemyNeighbours(Stone stone) {
        int countEnemy = 0;
        Stone[] neighbours = _game.getNeighbours(stone);
        for(Stone s : neighbours){
            if(s != null)
                if(s.getColor().equals(_opponent.getColor()))
                    countEnemy++;
        }

        return countEnemy;
    }

    private boolean checkForKill(Stone stone) { return _game.checkIfCommitedKill(stone); }
    private boolean checkForCorrectMove(Stone stone) { return _game.checkForCorrectMove(stone, this); }
    private boolean checkForChain(Stone stone) { return countStonesOfGivenColorAround(stone, _color) == 1; }
    private boolean checkForFriendHug(Stone stone) { return countStonesOfGivenColorAround(stone, _color) > 1; }
    private boolean checkForEnemyEqualization(Stone stone) { return ((countStonesOfGivenColorAround(stone, _color)) + 1)== countStonesOfGivenColorAround(stone, _opponent.getColor())
                                                             && countStonesOfGivenColorAround(stone, _color) > 0
                                                             && countStonesOfGivenColorAround(stone, _opponent.getColor()) > 0; }
    private boolean checkForEnemyOutnumber(Stone stone) { return ((countStonesOfGivenColorAround(stone, _color)) + 1) > countStonesOfGivenColorAround(stone, _opponent.getColor())
                                                          && countStonesOfGivenColorAround(stone, _opponent.getColor()) > 0 && countStonesOfGivenColorAround(stone, _opponent.getColor()) > 1; }
    private int countStonesOfGivenColorAround(Stone stone, String clr) {
        Stone[] neighbours = _game.getNeighbours(stone);
        Stone[] cornerNeighbours = _game.getCornerNeighbours(stone);
        int countClr = 0;
        for(Stone s : neighbours){
            if(s != null)
                if(s.getColor().equals(clr))
                    countClr++;
        }
        for(Stone s : cornerNeighbours){
            if(s != null)
                if(s.getColor().equals(clr))
                    countClr++;
        }
        return countClr;
    }

    private boolean checkForTerritoryExpansion(Stone stone) {
        int currentTerritory = _game.calculateTerritory(stone.getColor());
        int newTerritory = _game.calculateTerritoryWithNewStone(stone);

        return newTerritory > currentTerritory;
    }

    private boolean checkForTerritoryShrink(Stone stone) {
        int currentTerritory = _game.calculateTerritory(stone.getColor());
        int newTerritory = _game.calculateTerritoryWithNewStone(stone);

        return newTerritory < currentTerritory && newTerritory != 0;
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
