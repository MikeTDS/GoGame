package goGame.GuiTests;

import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GUI.GuiFrame;
import goGame.GUIcomponents.ScoreBoard.ScoreBoard;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

public class TestGUIimplementation {
    GuiFrame guiFrame;
    GoBoard goBoard;
    ScoreBoard scoreBoard;
    int WIDTH=1200, HEIGHT=900, size=19;
    @Before
    public void presetGUI(){
        guiFrame = new GuiFrame(WIDTH, HEIGHT);
        goBoard = new GoBoard(size);
        scoreBoard = new ScoreBoard();
        testAssemblingComponents();
    }
    @Test
    public void testAssemblingComponents(){
        guiFrame.add(goBoard);
        guiFrame.add(scoreBoard);
        assert(goBoard.isVisible());
        assert(scoreBoard.isVisible());
    }
}
