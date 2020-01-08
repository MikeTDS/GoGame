package TestLogic;
import goGame.Client.GoGameClient;
import goGame.GameLogic.Game;
import goGame.GameLogic.Stone;
import goGame.Presets.TestingClient;
import goGame.Presets.TestingServer;
import goGame.Server.Bot.Bot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.Assert.*;

public class TestLogic {
    private TestingServer testingServer;
    private Thread serverThread;
    private TestingClient testingClient;
    private Thread clientThread;

    @Before
    public void setTestingServer() {
        testingServer = new TestingServer();
        serverThread  = new Thread(testingServer);
        serverThread.start();
    }

    @Before
    public void presetClientToTests(){
        testingClient = new TestingClient();
        clientThread = new Thread(testingClient);
        clientThread.start();
    }

    @After
    public void closeSocket() throws IOException {
        testingServer.closeSocket();
        testingServer = null;
    }


    int getXFromBoard(int i, int boardSize){
        if(i == 0)
            return 0;
        else
            return i % boardSize;
    }
    int getYFromBoard(int i, int boardSize) {
        if (i == 0)
            return 0;
        else
            return i / boardSize;
    }

    private void setTestGameWithBot() throws InterruptedException {
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        testingClient.disposeFrame();
        testingClient.getServerCommunicator().getPrintWriter().println("PLAY_WITH_BOT");
        testingClient.setupTestingClient();
        Thread.sleep(100);
    }
    private void setTestGameWithoutBot() throws InterruptedException {
        GoGameClient.connectToServer();
        testingClient.getServerCommunicator().getPrintWriter().println("NEW_GAME");
        GoGameClient.initializeGame();
        testingClient.disposeFrame();
        testingClient.getServerCommunicator().getPrintWriter().println("DONT_PLAY_WITH_BOT");
        testingClient.setupTestingClient();
        Thread.sleep(100);
    }

    @Test
    public void testCreatingGame() throws InterruptedException {
        setTestGameWithoutBot();
        assertEquals(testingServer.getGameList().size(),1 );
    }

    @Test
    public void testCreatingGameWithBot() throws InterruptedException {
        setTestGameWithBot();
        assert(testingServer.getGameList().get(0).getCurrentPlayer().getOpponent().getClass().equals(Bot.class));
    }

    @Test
    public void testRandomMove() throws InterruptedException {
        Random random = new Random();

        setTestGameWithBot();
        int x = random.nextInt(testingServer.getGameList().get(0).getBoardSize());
        int y = random.nextInt(testingServer.getGameList().get(0).getBoardSize());

        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer());
        assert(testingServer.getGameList().get(0).getCurrentPlayer().getClass().equals(Bot.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testCoordinatesOutOfBoardBounds() throws InterruptedException {
        setTestGameWithBot();
        int x = -1;
        int y = testingServer.getGameList().get(0).getBoardSize();

        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer());
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongPlayerTurn() throws InterruptedException {
        setTestGameWithBot();
        int x = 0,
            y = 0;

        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer().getOpponent());
    }

    @Test(expected = IllegalStateException.class)
    public void testTwoMovesInRow() throws InterruptedException {
        setTestGameWithBot();
        int x = 0,
            y = 0;

        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer());
        x = y = 1;
        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer().getOpponent());
    }

    @Test(expected = IllegalStateException.class)
    public void testOpponentNotPresent() throws InterruptedException {
        setTestGameWithoutBot();
        int x = 0,
            y = 0;

        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer());
    }

    @Test(expected = IllegalStateException.class)
    public void testPlaceOnBlockedField() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        setTestGameWithBot();

        Field field = Game.class.getDeclaredField("_blockedField");
        field.setAccessible(true);
        field.set(testingServer.getGameList().get(0), 1);

        int x = 1,
            y = 0;

        testingServer.getGameList().get(0).move(x,y, testingServer.getGameList().get(0).getCurrentPlayer());
    }

    @Test
    public void testKillStone() throws InterruptedException {
        setTestGameWithBot();
        Stone[] board = testingServer.getGameList().get(0).getBoard();
        board[1] = new Stone(1, 0, "Black");
        board[19] = new Stone(0, 1, "Black");
        board[20] = new Stone(1, 1, "White");
        board[21] = new Stone(2, 1, "Black");
        testingServer.getGameList().get(0).setBoard(board);

        testingServer.getGameList().get(0).move(1,2, testingServer.getGameList().get(0).getCurrentPlayer());

        assertEquals(testingServer.getGameList().get(0).getBoard()[1].getColor(), "Black");
        assertEquals(testingServer.getGameList().get(0).getBoard()[19].getColor(), "Black");
        assertEquals(testingServer.getGameList().get(0).getBoard()[21].getColor(), "Black");
        assertEquals(testingServer.getGameList().get(0).getBoard()[39].getColor(), "Black");
        assertEquals(testingServer.getGameList().get(0).getBoard()[20].getColor(), "Empty");
    }

    @Test
    public void testKillGroup() throws InterruptedException {
        setTestGameWithBot();
        int boardSize = testingServer.getGameList().get(0).getBoardSize();
        Stone[] board = testingServer.getGameList().get(0).getBoard();
        for (int i = 0; i < boardSize*boardSize; i++) {
            int x = getXFromBoard(i, boardSize),
                y = getYFromBoard(i, boardSize);
            if(x == 0 || x == boardSize - 1 || y == 0 || y == boardSize - 1)
                board[i] = new Stone(x, y, "Black");
            else
                board[i] = new Stone(x, y, "White");
        }
        board[1] = new Stone(1, 0, "Empty");
        testingServer.getGameList().get(0).setBoard(board);

        assertEquals(testingServer.getGameList().get(0).getBoard()[1].getColor(), "Empty");
        for (int i = 0; i < boardSize*boardSize; i++) {
            if(i == 1)
                continue;

            int x = getXFromBoard(i, boardSize),
                y = getYFromBoard(i, boardSize);
            if(x == 0 || x == boardSize - 1 || y == 0 || y == boardSize - 1)
                assertEquals(testingServer.getGameList().get(0).getBoard()[i].getColor(), "Black");
            else
                assertEquals(testingServer.getGameList().get(0).getBoard()[i].getColor(), "White");
        }

        testingServer.getGameList().get(0).move(1,0, testingServer.getGameList().get(0).getCurrentPlayer());

        for (int i = 0; i < boardSize*boardSize; i++) {
            int x = getXFromBoard(i, boardSize),
                y = getYFromBoard(i, boardSize);
            if(x == 0 || x == boardSize - 1 || y == 0 || y == boardSize - 1)
                assertEquals(testingServer.getGameList().get(0).getBoard()[i].getColor(), "Black");
            else
                assertEquals(testingServer.getGameList().get(0).getBoard()[i].getColor(), "Empty");
        }
    }

    @Test
    public void testTerritoryCalc() throws InterruptedException {
        setTestGameWithBot();
        testingServer.getGameList().get(0).move(2, 0, testingServer.getGameList().get(0).getCurrentPlayer());
        Thread.sleep(100);
        testingServer.getGameList().get(0).move(0, 1, testingServer.getGameList().get(0).getCurrentPlayer());
        Thread.sleep(100);
        testingServer.getGameList().get(0).move(1, 1, testingServer.getGameList().get(0).getCurrentPlayer());
        Thread.sleep(100);
        assertEquals(testingServer.getGameList().get(0).getCurrentPlayer().getTotalPoints(), 2);
    }
}
