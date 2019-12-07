package goGame.GameLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public interface IPlayer {
    void setup() throws IOException;
    void processMoveCommand(int x, int y);
    void sendOutput(String out);
    IPlayer getOpponent();
    void setOpponent(IPlayer iPlayer);
    String getColor();
    boolean getPass();
    PrintWriter getOutput();
}
