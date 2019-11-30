package goGame.GameLogic;

public class Stone {
    private int _x,_y;
    private String _color;
    private int _breaths;

    Stone(int x, int y, String clr){ this(x, y, clr, 4); }
    public Stone(int x, int y, String clr, int breaths){
        _x = x;
        _y = y;
        _color = clr;
        _breaths = breaths;
    }
    public int getPosX(){return _x;}
    public int getPosY(){return _y;}
    public int getBreaths(){return _breaths;}
    public void setBreaths(int breaths){ _breaths = breaths;}
}

