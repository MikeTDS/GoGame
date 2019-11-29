package goGame.GUIcomponents.ScoreBoard;

import goGame.GUIcomponents.GoBoard.GoBoard;
import javax.swing.*;
import java.awt.*;

public class ScoreBoard extends JPanel{
    private static ScoreLabel scoreLabel;
    private static StoneAmountLabel stoneAmountLabel;
    private static PassButton passButton;
    private static ExitButton exitButton;
    public ScoreBoard(){
        initializeScoreBoard();
        initializeComponents();
        addComponents();
    }

    private void initializeScoreBoard(){
        setPreferredSize(new Dimension(300,800));
        setBackground(Color.decode("#d9b962"));
        setLayout(new GridLayout(4,1));
    }
    private void initializeComponents(){
        scoreLabel = new ScoreLabel();
        scoreLabel.showScore(0);
        stoneAmountLabel = new StoneAmountLabel();
        stoneAmountLabel.showStoneAmount(0,0);
        passButton = new PassButton();
        exitButton = new ExitButton();
    }
    private void addComponents(){
        this.add(scoreLabel);
        this.add(stoneAmountLabel);
        this.add(passButton);
        this.add(exitButton);
    }
    public static void showPoints(int ps){
        scoreLabel.showScore(ps);
    }
    public static void showStones(int w, int b){
        stoneAmountLabel.showStoneAmount(w,b);
    }
}
