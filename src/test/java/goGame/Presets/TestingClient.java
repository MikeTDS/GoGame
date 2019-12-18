package goGame.Presets;

import goGame.Client.GoGameClient;
import goGame.Client.ServerCommunicator;
import goGame.GUI.GuiFrame;

import java.lang.reflect.Field;

public class TestingClient implements Runnable{
    GoGameClient goGameClient;
    @Override
    public void run() {
        try {
            setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setup(){
        goGameClient = new GoGameClient();
    }
    public ServerCommunicator getServerCommunicator(){
        return goGameClient.getServerCommunicator();
    }
    public void setupClient() { GoGameClient.setupClient(); }
    public void setupTestingClient() {
        goGameClient.setColor(goGameClient.getServerCommunicator().getScanner().nextLine());
        goGameClient.setOpponentColor(goGameClient.getColor().equals("Black") ? "White" : "Black");
    }
    public void disposeFrame() { goGameClient.getGuiFrame().dispose(); }
}