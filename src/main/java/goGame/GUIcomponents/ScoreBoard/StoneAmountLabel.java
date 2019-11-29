package goGame.GUIcomponents.ScoreBoard;

import javax.swing.JLabel;
import java.awt.*;

public class StoneAmountLabel extends JLabel {
    public StoneAmountLabel() {
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setFont(new Font(getName(), Font.PLAIN, 24));
        setVisible(true);
    }
    public void showStoneAmount(int w, int b){
        setText("<html>Black: " + b + "<br/>" + "White: " + w + "<html/>");
    }

}
