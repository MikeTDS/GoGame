package goGame.TestServer;

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class TestLobby {
    private int boardSize;
    private GoGameClient goGameClient;
    private GoGameServer goGameServer;
    @Before
    public void presetServer() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        goGameServer = new GoGameServer();
        Method setBoardSize = GoGameServer.class.getDeclaredMethod("setBoardSize", int.class);
        setBoardSize.setAccessible(true);
        setBoardSize.invoke(goGameServer, 9);
        Method initializeServer = GoGameServer.class.getDeclaredMethod("initializeServer");
        initializeServer.setAccessible(true);
        initializeServer.invoke(GoGameServer.class);
        Method listenForClients = GoGameServer.class.getDeclaredMethod("listenForClients");
        listenForClients.setAccessible(true);
        listenForClients.invoke(GoGameServer.class);
    }
    @Before
    public void presetLobby() throws Exception {
        goGameClient = new GoGameClient();
        Method connectToServer = GoGameClient.class.getDeclaredMethod("connectToServer");
        connectToServer.setAccessible(true);
        connectToServer.invoke(goGameClient);
        System.out.println("Connected");
    }
    @Test
    public void testSendingGames() throws Exception {

    }
}
