package goGame.GameLogic;

class Stone {
    private int _x,_y;
    private String _color;
    private boolean _wasChecked;

    Stone(int x, int y, String clr){
        _x = x;
        _y = y;
        _color = clr;
        _wasChecked = false;
    }
    int getPosX(){ return _x; }
    int getPosY(){ return _y; }
    String getColor(){ return _color; }
    boolean wasntChecked(){ return !_wasChecked; }
    void setWasChecked(boolean bool){ _wasChecked = bool; }
}

