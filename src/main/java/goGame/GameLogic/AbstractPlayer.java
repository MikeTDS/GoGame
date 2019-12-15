package goGame.GameLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AbstractPlayer implements IPlayer{
    protected IPlayer _opponent;
    protected Scanner _input;
    protected PrintWriter _output;
    protected String _color;
    protected boolean _lastMovePass;
    protected volatile Game _game;
    protected volatile Socket _socket;
    protected int killPoints;
    protected int territoryPoints;
    protected int totalPoints;

    @Override
    public void sendOutput(String out) {
        _output.println(out);
    }
    @Override
    public IPlayer getOpponent() {
        return _opponent;
    }
    public void setOpponent(IPlayer iPlayer){
        this._opponent=iPlayer;
    }
    @Override
    public String getColor() {
        return _color;
    }
    @Override
    public boolean getPass() {
        return _lastMovePass;
    }
    @Override
    public PrintWriter getOutput(){
        return  this._output;
    }
    @Override
    public Socket getSocket() { return _socket; }
    @Override
    public void addKillPoints(int points){killPoints+=points;}
    @Override
    public void resetPoints(){
        killPoints=0;
        territoryPoints=0;
        totalPoints=0;
    }
    @Override
    public void updatePoints(){
        territoryPoints = _game.calculateTerritory(_color);
        totalPoints = killPoints+territoryPoints;
    }
    @Override
    public void sendPoints(){}

    @Override
    public int getTotalPoints(){
        return totalPoints;
    }
}
