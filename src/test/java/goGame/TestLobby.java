package goGame;

import goGame.Client.GoGameClient;
import goGame.Client.LobbyComunicator;
import goGame.GameLogic.Game;
import goGame.Server.GoGameServer;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestLobby {
    private GoGameServer goGameServer;
    private int boardSize;
    private GoGameClient goGameClient;
    @Before
    public void presetServer(){
        goGameServer = new GoGameServer();
        goGameServer.main(null);
    }
    @Before
    public void presetLobby() throws Exception {
        goGameClient = new GoGameClient();
        goGameClient.main(null);
    }
    @Test
    public void testSendingGameList() throws IOException {
        for(int i=0; i<10; i++){
            goGameServer.getGameList().add(new Game(boardSize));
        }
        assertEquals(goGameServer.getGameList().size(), 10);
        List gamesToChoose =  goGameClient.getMenuFrame().getGamesToChoose();
        goGameClient.getLobbyCommunicator().closeConnection();
        goGameServer.closeLobby();
        assertEquals(goGameServer.getGameList().size(), gamesToChoose.getItemCount());
    }
}
