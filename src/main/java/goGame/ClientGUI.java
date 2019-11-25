package goGame;
import goGame.GUIcomponents.GoBoard.GoBoard;

import javax.swing.JFrame;
import java.awt.*;

public class ClientGUI extends JFrame{
    private int _xSize, _ySize;
    private GoBoard goBoard;

    ClientGUI(int xSize, int ySize){
        _xSize=xSize;
        _ySize=ySize;
        initializeComponents();
        initializeGUI();
        addComponents();
    }

    private void initializeComponents(){
        goBoard = new GoBoard(19);
    }

    private void initializeGUI(){
        setSize(new Dimension(_xSize, _ySize));
        setLayout(new GridBagLayout());
        setTitle("GoGame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void addComponents(){
        this.add(goBoard);
    }
}
