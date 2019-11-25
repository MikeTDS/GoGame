package goGame.GUIcomponents.GoBoard;

import javax.swing.JPanel;
import java.awt.*;

public class GoBoard extends JPanel implements IGoBoard{
    private int _size, _xSize, _ySize, _margin, _gap;

    public GoBoard(int size){
        _size=size;
        initializeBoard(800,800);
    }

    private void initializeBoard(int x, int y){
        _xSize=x;
        _ySize=y;
        _margin=30;
        this.setPreferredSize(new Dimension(_xSize,_ySize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard();
        drawFields(_size, g);
        drawSpecialFields(_size, g);
        //temp
        drawPlayer(0,0, Color.BLACK, g);
        drawPlayer(2,4, Color.BLACK, g);
        drawPlayer(6,8, Color.WHITE, g);
        drawPlayer(18,18, Color.WHITE, g);

    }

    @Override
    public void drawBoard() {
        this.setBackground(Color.decode("#f2c679"));
    }

    @Override
    public void drawFields(int size, Graphics g) {
        _gap = setGaps(size);
        int x1=_margin, y1=_margin, x2=_margin, y2=_ySize-_margin;
        //pionowe
        for(int i=0; i<size; i++){
            g.drawLine(x1, y1, x2, y2);
            x1+=_gap;
            x2+=_gap;
        }
        //poziome
        x1=_margin; y1=_margin; x2=_xSize-_margin; y2=_margin;
        for(int i=0; i<size; i++){
            g.drawLine(x1, y1, x2, y2);
            y1+=_gap;
            y2+=_gap;
        }
    }

    @Override
    public void drawSpecialFields(int size, Graphics g) {
        switch (size) {
            case 19:
                for(int x=3; x<=15; x+=6){
                    for(int y=3; y<=15; y+=6){
                        g.fillOval(temporaryConverter(x)-8, temporaryConverter(y)-8, 16, 16);
                    }
                }
                break;
            case 13:
                for(int x=3; x<=9; x+=6){
                    for(int y=3; y<=9; y+=6){
                        g.fillOval(temporaryConverter(x)-8, temporaryConverter(y)-8, 16, 16);
                    }
                }
                g.fillOval(temporaryConverter(6)-8, temporaryConverter(6)-8, 16, 16);//srodkowy
                break;
            case 9:
                for(int x=2; x<=6; x+=4){
                    for(int y=2; y<=6; y+=4){
                        g.fillOval(temporaryConverter(x)-8, temporaryConverter(y)-8, 16, 16);
                    }
                }
                g.fillOval(temporaryConverter(4)-8, temporaryConverter(4)-8, 16, 16);//srodkowy
                break;
        }
    }
    @Override
    public int setGaps(int size){
        return ((_xSize-(2*_margin))/(size-1));
    }
    //ta funkcja ma byc w game contollerze
    private int temporaryConverter(int x){
        int drawX=_margin;
        for(int i=0; i<x; i++){
            drawX+=_gap;
        }
        return drawX;
    }
    //do testow, ale pewnie zostanie
    private void drawPlayer(int x, int y, Color clr, Graphics g){
        g.setColor(clr);
        g.fillOval(temporaryConverter(x)-15,temporaryConverter(y)-15,30,30);
    }
}
