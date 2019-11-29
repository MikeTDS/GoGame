package goGame.GUIcomponents.ScoreBoard;

import javax.swing.JLabel;
import java.awt.*;

public class ScoreLabel extends JLabel {
    public ScoreLabel(){
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setFont(new Font(getName(), Font.PLAIN, 30));
        setVisible(true);
    }
    public void showScore(int playerScore){
        this.setText("Score: " + playerScore);
    }
}
