package goGame.Presets;

import goGame.Client.GoGameClient;
import goGame.Client.ServerCommunicator;

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
}