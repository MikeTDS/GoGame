package goGame;

import com.sun.tools.javac.Main;
import goGame.Client.GoGameClient;
import goGame.GameLogic.Game;
import goGame.Server.GoGameServer;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;

public class TestLobby {
    private int boardSize;
    private GoGameClient goGameClient;
    @Before
    public void presetServer(){

    }
    @Before
    public void presetLobby() throws Exception {
        goGameClient = new GoGameClient();
        goGameClient.main(null);
    }
    @Test
    public void testSendingGames() throws Exception {

    }
}
