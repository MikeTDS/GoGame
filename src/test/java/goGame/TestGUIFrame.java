package goGame;

import goGame.GUI.GuiFrame;
import org.junit.Before;
import org.junit.Test;

public class TestGUIFrame {
    GuiFrame guiFrame;
    int WIDTH=1200, HEIGHT=900;
    @Before
    public void presetGui(){
        guiFrame = new GuiFrame(WIDTH, HEIGHT);
    }
    @Test
    public void testChooseOpponent(){
        int result = guiFrame.getOption();
        assert(result==0 || result==1 || result==-1);
        if(result==0){
            System.out.println("Chosen: Yes");
        }
        else if(result==1){
            System.out.println("Chosen: No");
        }
        else{
            System.out.println("Exited");
        }
    }
}
