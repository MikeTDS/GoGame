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
    protected Game _game;
    protected volatile Socket _socket;

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

}
