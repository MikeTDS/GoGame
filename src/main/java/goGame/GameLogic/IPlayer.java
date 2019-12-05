package goGame.GameLogic;

import java.io.IOException;

public interface IPlayer {
    void setup() throws IOException;
    void processCommands();
    void processMoveCommand(int x, int y);
    void sendOutput(String out);
    Player getOpponent();
    String getColor();
    boolean getPass();
}
