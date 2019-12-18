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
    public void setupTestingClient() {
        GoGameClient.setColor(goGameClient.getServerCommunicator().getScanner().nextLine());
        GoGameClient.setOpponentColor(GoGameClient.getColor().equals("Black") ? "White" : "Black");
    }
    public void disposeFrame() {
        if(GoGameClient.getGuiFrame() != null)
            GoGameClient.getGuiFrame().dispose();
    }
    public void resetFrame() throws NoSuchFieldException, IllegalAccessException {
        Field field = GoGameClient.class.getDeclaredField("_clientFrame");
        field.setAccessible(true);
        field.set(GoGameClient.getGuiFrame(), null);
    }
}