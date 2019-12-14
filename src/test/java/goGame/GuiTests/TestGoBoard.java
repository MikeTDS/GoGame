package goGame.GuiTests;
import goGame.GUI.GUIcomponents.GoBoard.FieldButton;
import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GUI.GUIcomponents.Stone.Stone;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class TestGoBoard {
    GoBoard goBoard;
    ArrayList<Stone> blackStones;
    ArrayList<Stone> whiteStones;
    ArrayList<FieldButton> buttons;
    int WIDTH=800, HEIGHT=800;
    int size = 19;
    @Before
    public void presetGoBoard(){
        goBoard = new GoBoard(19);
        testInitialization();
    }
    @Test
    public void testInitialization(){
        goBoard.initializeBoard(WIDTH, HEIGHT);
        blackStones = goBoard.getPlayer("black");
        whiteStones = goBoard.getPlayer("white");
        buttons = goBoard.getFieldButtonArrayList();
        assert(blackStones!=null && whiteStones!=null && buttons!=null);
        assert(goBoard.getStonesAmount("Black")==0 && goBoard.getStonesAmount("White")==0);
        assert(buttons.size()==size*size);
    }
    @Test
    public void testSettingAndRemovingStones(){
        goBoard.setStone(0, size-1, "White");
        goBoard.setStone(size-1,0, "Black");
        assert(goBoard.getStonesAmount("Black")==1 && goBoard.getStonesAmount("White")==1);
        assert(blackStones.get(0).getPosX()==size-1 && blackStones.get(0).getPosY()==0);
        assert(whiteStones.get(0).getPosX()==0 && whiteStones.get(0).getPosY()==size-1);
        goBoard.removeStone(0, size-1, "White");
        goBoard.removeStone(size-1, 0, "Black");
        assert(goBoard.getStonesAmount("Black")==0);
        assert(goBoard.getStonesAmount("White")==0);
    }
    @Test
    public void testSettingGapAndConverter(){
        int gap = goBoard.setGaps(size);
        assert(gap>0 && gap<WIDTH && gap<HEIGHT);
        int margin = goBoard.getMargin();
        int c1 = goBoard.fieldConverter(0);
        int c2 = goBoard.fieldConverter(size-1);
        int c3 = goBoard.fieldConverter(7);
        assert(c1==margin && c2==(size-1)*gap+margin && c3==gap*7+margin);
    }

}
