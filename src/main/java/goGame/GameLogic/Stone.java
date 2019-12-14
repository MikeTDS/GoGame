package goGame.GameLogic;

public class Stone {
    private int _x,_y;
    private String _color;
    private boolean _wasChecked;
    private boolean _wasCheckedTerritory;
    private boolean _isPartOfTerritory;

    public Stone(int x, int y, String clr){
        _x = x;
        _y = y;
        _color = clr;
        _wasChecked = false;
        _wasCheckedTerritory=false;
        _isPartOfTerritory=true;
    }
    public int getPosX(){ return _x; }
    public int getPosY(){ return _y; }
    public String getColor(){ return _color; }
    boolean wasntChecked(){ return !_wasChecked; }
    void setWasChecked(boolean bool){ _wasChecked = bool; }
    boolean wasCheckedTerritory(){return _wasCheckedTerritory;}
    void setWasCheckedTerritory(boolean wc){_wasCheckedTerritory=wc;}
    boolean isPartOfTerritory(){return  _isPartOfTerritory;}
    void setIsPartOfTerritory(boolean inpot){_isPartOfTerritory=inpot;}
}

