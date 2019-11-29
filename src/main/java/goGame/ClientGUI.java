package goGame;
import goGame.GUIcomponents.GoBoard.GoBoard;
import goGame.GUIcomponents.ScoreBoard.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ClientGUI extends JFrame {
    private int _xSize, _ySize;
    private static GoBoard goBoard;
    private static ScoreBoard scoreBoard;

    ClientGUI(int xSize, int ySize){
        _xSize=xSize;
        _ySize=ySize;
        initializeComponents();
        initializeGUI();
        addComponents();
    }

    private void initializeComponents(){
        goBoard = new GoBoard(19);
        scoreBoard = new ScoreBoard();
    }

    private void initializeGUI(){
        setSize(new Dimension(_xSize, _ySize));
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setTitle("GoGame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void addComponents(){
        this.add(goBoard);
        this.add(scoreBoard);
    }

    public static GoBoard getGoBoard() {
        return goBoard;
    }

    public static ScoreBoard getScoreBoard() {
        return scoreBoard;
    }
}
