package goGame.GuiTests;

import goGame.GUI.MenuFrame;
import org.junit.Before;
import org.junit.Test;

public class TestMenuLobby {
    MenuFrame menu;
    @Before
    public void presetMenu(){
        menu = new MenuFrame();
    }
    @Test
    public void addingGamesTest(){
        menu.addGamesToList("myGame1");
        menu.addGamesToList("myGame2");
        assert(menu.getGamesList().getItemCount()==2);
        assert(menu.getGamesList().getItem(1).equals("myGame2"));
    }
}
