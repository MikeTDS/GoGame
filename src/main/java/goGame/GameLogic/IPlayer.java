package goGame.GameLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public interface IPlayer {
    void sendOutput(String out);
    IPlayer getOpponent();
    void setOpponent(IPlayer iPlayer);
    String getColor();
    boolean getPass();
    PrintWriter getOutput();
    Socket getSocket();
    void addKillPoints(int points);
    void resetPoints();
    void updatePoints();
    void sendPoints();
    int getTotalPoints();
}
