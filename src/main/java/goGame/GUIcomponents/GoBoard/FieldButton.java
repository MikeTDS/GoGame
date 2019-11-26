package goGame.GUIcomponents.GoBoard;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.*;

public class FieldButton extends Rectangle {
    private int _x, _y, _xDraw, _yDraw,  _xSize, _ySize;
    FieldButton(int x, int y, int xsize, int ysize){
        _x=x;
        _y=y;
        _xSize=xsize;
        _ySize=ysize;
    }
    public int getPosX(){
        return _x;
    }
    public int getPosY(){
        return  _y;
    }
    public int getXDraw(){return _xDraw;}
    public int getYDraw(){return _yDraw;}
    public void setXDraw(int x){this._xDraw=x;}
    public void setYDraw(int y){this._yDraw=y;}
    public int getXSize(){return _xSize;}
    public int getYSize(){return  _ySize;}
}
