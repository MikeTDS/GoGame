package goGame.GUI.GUIcomponents.Stone;

import java.awt.*;

public class Stone {
    private int _x,_y;
    private String _color;
    public Stone(int x, int y, String clr){
        _x = x;
        _y = y;
        _color = clr;
    }
    public int getPosX(){return _x;}
    public int getPosY(){return _y;}
}
