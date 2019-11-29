package goGame.GUI.GUIcomponents.GoBoard;

import java.awt.*;

class FieldButton extends Rectangle {
    private int _x, _y, _xDraw, _yDraw,  _xSize, _ySize;
    FieldButton(int x, int y, int xsize, int ysize){
        _x=x;
        _y=y;
        _xSize=xsize;
        _ySize=ysize;
    }
    int getPosX(){
        return _x;
    }
    int getPosY(){
        return  _y;
    }
    int getXDraw(){return _xDraw;}
    int getYDraw(){return _yDraw;}
    void setXDraw(int x){this._xDraw=x;}
    void setYDraw(int y){this._yDraw=y;}
    int getXSize(){return _xSize;}
    int getYSize(){return  _ySize;}
}
