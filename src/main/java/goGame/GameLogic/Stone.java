package goGame.GameLogic;

public class Stone {
    private int _x,_y;
    private String _color;
    private boolean _wasChecked;
    private boolean _isSafe;
    private boolean _wasCheckedTerritory;
    private boolean _isPartOfTerritory;

    public Stone(int x, int y, String clr){
        _x = x;
        _y = y;
        _color = clr;
        _wasChecked = false;
        _isSafe = false;
        _wasCheckedTerritory=false;
        _isPartOfTerritory=true;
    }
    int getPosX(){ return _x; }
    int getPosY(){ return _y; }
    String getColor(){ return _color; }
    boolean wasntChecked(){ return !_wasChecked; }
    void setWasChecked(boolean bool){ _wasChecked = bool; }
    boolean isSafe(){ return _isSafe; }
    void setIsSafe(boolean bool){ _isSafe = bool; }
    boolean wasCheckedTerritory(){return _wasCheckedTerritory;}
    void setWasCheckedTerritory(boolean wc){_wasCheckedTerritory=wc;}
    boolean isPartOfTerritory(){return  _isPartOfTerritory;}
    void setIsPartOfTerritory(boolean inpot){_isPartOfTerritory=inpot;}
}

