package goGame.GameLogic;

import com.example.websocketdemo.controller.WebController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class WebPlayer implements IPlayer {
    String _color;
    Boolean _lastMovePass;
    Game _game;
    IPlayer _opponent;
    WebController _webController;
    int points;
    WebPlayer(String color, Game game, WebController webController) {
        _color = color;
        _game = game;
        _webController = webController;
        try {
            setup();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setup() throws IOException {
        _lastMovePass=false;
        resetPoints();
        if (_color.equals("Black")) {
            _game.currentPlayer = this;
        } else if(_color.equals("White")){
            _opponent = _game.currentPlayer;
            _opponent.setOpponent(this);
            //_opponent.getOutput().println("MESSAGE Your move");
        }
    }

//    public void processCommands() {
//        while (_input.hasNextLine()) {
//            String command = _input.nextLine();
//            _lastMovePass=false;
//            if (command.equals("QUIT")) {
//                return;
//            } else if (command.equals("MOVE")) {
//                int x = Integer.parseInt(_input.nextLine());
//                int y = Integer.parseInt(_input.nextLine());
//                processMoveCommand(x, y);
//            }
//            else if(command.equals("EXIT")){
//                if(!_game.isFinished() && _opponent!=null){
//                    _output.println("SURRENDER");
//                    if(_opponent!=null)
//                        _opponent.getOutput().println("SURRENDER_WIN");
//                    _game.setFinished();
//                }
//                else{
//                    _output.println("NORMAL_EXIT");
//                    _game.setFinished();
//                }
//            }
//            else if(command.equals("PASS")){
//                if(_opponent==null || _game.getCurrentPlayer()!=this){
//                    _output.println("ERROR_PASS");
//                }
//                else{
//                    if(_opponent.getPass()){
//                        _output.println("QUIT_PASS");
//                        if (!_opponent.getSocket().equals(getSocket())){
//                            _opponent.getOutput().println("QUIT_PASS");
//                            if(_opponent.getTotalPoints()>getTotalPoints()){
//                                _opponent.getOutput().println("WINNER");
//                                getOutput().println("LOSER");
//                            }
//                            else if(_opponent.getTotalPoints()<getTotalPoints()){
//                                _opponent.getOutput().println("LOSER");
//                                getOutput().println("WINNER");
//                            }
//                            else{
//                                _opponent.getOutput().println("DRAW");
//                                getOutput().println("DRAW");
//                            }
//                        }
//                        _game.setFinished();
//                    }
//                    else{
//                        _output.println("FIRST_PASS");
//                        if (!_opponent.getSocket().equals(getSocket()))
//                            _opponent.getOutput().println("OPPONENT_PASS");
//                        _lastMovePass=true;
//                        _game.skipMove();
//                    }
//                }
//          }
//        }
//    }
//    public void processMoveCommand(int x, int y) {
//        try {
//            _game.move(x, y, this);
////            _output.println("VALID_MOVE");
////            _output.println(x);
////            _output.println(y);
////            if (!_opponent.getSocket().equals(getSocket())) {
//////                _opponent.getOutput().println("OPPONENT_MOVED");
//////                _opponent.getOutput().println(x);
//////                _opponent.getOutput().println(y);
////            }
//        } catch (IllegalStateException e) {
//            System.out.println(e.getMessage());
////            _output.println("WRONG_MOVE " + e.getMessage());
//        }
//    }

    public boolean processMoveCommand(int id){
        try{
            int x = _game.getXFromBoard(id);
            int y = _game.getYFromBoard(id);
            _game.move(x, y,this);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendOutput(String out) {
            _webController.sendKill(out);
    }

    @Override
    public IPlayer getOpponent() {
        return _opponent;
    }

    @Override
    public void setOpponent(IPlayer iPlayer) {
        _opponent = iPlayer;
    }

    @Override
    public String getColor() {
        return _color;
    }

    @Override
    public boolean getPass() {
        return false;
    }

    @Override
    public PrintWriter getOutput() {
        return null;
    }

    @Override
    public Socket getSocket() {
        return null;
    }

    @Override
    public void addKillPoints(int points) {

    }

    @Override
    public void resetPoints() {
        points=0;
    }

    @Override
    public void updatePoints() {

    }

    public void sendPoints(){
//        _output.println("POINTS");
//        _output.println(totalPoints);
//        //System.out.println(_color + " " + totalPoints);
    }

    @Override
    public int getTotalPoints() {
        return 0;
    }
}