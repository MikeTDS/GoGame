package goGame.GUI.GUIcomponents.GoBoard;

import goGame.Client.ServerComunicator;
import goGame.GUI.GUIcomponents.Stone.Stone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GoBoard extends JPanel implements IGoBoard, MouseListener {
    private static int _size, _xSize, _ySize, _margin, _gap, WIDTH=800, HEIGHT=800;
    private ArrayList<FieldButton> fieldButtonArrayList;
    private ArrayList<Stone> whitePlayer, blackPlayer;
    private ServerComunicator _serverComunitator;

    public GoBoard(int size){
        _size=size;
        _serverComunitator = ServerComunicator.getInstance();
        initializeBoard(WIDTH,HEIGHT);
    }

    public void initializeBoard(int x, int y){
        _xSize=x;
        _ySize=y;
        _margin=30;
        this.setLayout(new GridLayout(_size, _size));
        this.setPreferredSize(new Dimension(_xSize,_ySize));
        setButtonList(_size);
        addMouseListener(this);
        whitePlayer = new ArrayList<>();
        blackPlayer = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard();
        drawFields(_size, g);
        drawSpecialFields(_size, g);
        drawButtonFields();
        drawStones(g);
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
                        g.fillOval(fieldConverter(x)-8, fieldConverter(y)-8, 16, 16);
                    }
                }
                break;
            case 13:
                for(int x=3; x<=9; x+=6){
                    for(int y=3; y<=9; y+=6){
                        g.fillOval(fieldConverter(x)-8, fieldConverter(y)-8, 16, 16);
                    }
                }
                g.fillOval(fieldConverter(6)-8, fieldConverter(6)-8, 16, 16);//srodkowy
                break;
            case 9:
                for(int x=2; x<=6; x+=4){
                    for(int y=2; y<=6; y+=4){
                        g.fillOval(fieldConverter(x)-8, fieldConverter(y)-8, 16, 16);
                    }
                }
                g.fillOval(fieldConverter(4)-8, fieldConverter(4)-8, 16, 16);//srodkowy
                break;
        }
    }
    @Override
    public int setGaps(int size){
        _gap = ((_xSize-(2*_margin))/(size-1));
        return _gap;
    }

    public int fieldConverter(int x){
        int drawX=_margin;
        for(int i=0; i<x; i++){
            drawX+=_gap;
        }
        return drawX;
    }

    private void drawStone(int x, int y, Color clr, Graphics g){
        g.setColor(clr);
        g.fillOval(fieldConverter(x)-(_gap/3), fieldConverter(y)-(_gap/3),(_gap*2)/3, (2*_gap)/3);
    }

    private void setButtonList(int size){
        fieldButtonArrayList = new ArrayList<>();
        for(int x=0; x<size; x++){
            for(int y=0; y<size; y++){
                fieldButtonArrayList.add(new FieldButton(y,x,20,20));
            }
        }
    }
    private void drawButtonFields(){
        for(FieldButton fieldButton : fieldButtonArrayList){
            fieldButton.setXDraw(fieldConverter(fieldButton.getPosX())-10);
            fieldButton.setYDraw(fieldConverter(fieldButton.getPosY())-10);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        int xPos = mouseEvent.getX();
        int yPos = mouseEvent.getY();
        for(FieldButton fieldButton : fieldButtonArrayList){
            if(xPos>=fieldButton.getXDraw() && xPos<=fieldButton.getXDraw()+fieldButton.getXSize() && yPos>=fieldButton.getYDraw() && yPos<fieldButton.getYDraw()+fieldButton.getYSize()){
                System.out.println(fieldButton.getPosX() + ", "+fieldButton.getPosY());
                _serverComunitator.getPrintWriter().println("MOVE");
                _serverComunitator.getPrintWriter().println(fieldButton.getPosX());
                _serverComunitator.getPrintWriter().println(fieldButton.getPosY());
                return;
            }
        }
    }

    public void setStone(int x, int y, String playerColor){
            if (playerColor.equals("Black")) {
                blackPlayer.add(new Stone(x, y, playerColor));
            }
            else if(playerColor.equals("White")){
                whitePlayer.add(new Stone(x, y, playerColor));
            }
    }

    public void removeStone(int x, int y, String playerColor) {
        if (playerColor.equals("Black")) {
            for(Stone stone : blackPlayer){
                if(stone.getPosX() == x && stone.getPosY() == y){
                    blackPlayer.remove(stone);
                    break;
                }
            }
        }
        else if(playerColor.equals("White")){
            for(Stone stone : whitePlayer){
                if(stone.getPosX() == x && stone.getPosY() == y){
                    whitePlayer.remove(stone);
                    break;
                }
            }
        }
    }

    private void drawStones(Graphics g){
        for(int i=0; i<whitePlayer.size(); i++){
            drawStone(whitePlayer.get(i).getPosX(), whitePlayer.get(i).getPosY(), Color.WHITE, g);
        }
        for(int i=0; i<blackPlayer.size(); i++){
            drawStone(blackPlayer.get(i).getPosX(), blackPlayer.get(i).getPosY(), Color.BLACK, g);
        }
    }

    public int getStonesAmount(String clr){
        if(clr.equalsIgnoreCase("black")){
            return whitePlayer.size();
        }
        else if(clr.equalsIgnoreCase("white")){
            return blackPlayer.size();
        }
        return 0;
    }

    public ArrayList<FieldButton> getFieldButtonArrayList(){return fieldButtonArrayList;}
    public ArrayList<Stone> getPlayer(String color){
        if(color.equalsIgnoreCase("white")){
            return whitePlayer;
        }
        else if(color.equalsIgnoreCase("black")){
            return blackPlayer;
        }else {
            return null;
        }
    }
    public int getMargin(){return _margin;}
    public void mousePressed(MouseEvent mouseEvent) {}
    public void mouseReleased(MouseEvent mouseEvent) {}
    public void mouseEntered(MouseEvent mouseEvent) {}
    public void mouseExited(MouseEvent mouseEvent) {}
}
