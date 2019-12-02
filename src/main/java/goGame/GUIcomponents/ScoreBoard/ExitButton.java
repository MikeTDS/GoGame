package goGame.GUIcomponents.ScoreBoard;

//import goGame.ClientGUI;

import goGame.Client.GoGameClient;
import goGame.Client.ServerComunitator;
import goGame.GUI.GUIcomponents.GoBoard.GoBoard;
import goGame.GameLogic.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.YES_OPTION;

public class ExitButton extends JButton implements ActionListener {
    public ExitButton(){
        setText(("EXIT"));
        setPreferredSize(new Dimension(100,50));
        addActionListener(this);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        int returnValue=0;
        returnValue = JOptionPane.showConfirmDialog(GoGameClient.getGuiFrame(), "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION);
        if(returnValue==YES_OPTION){
            ServerComunitator.getPrintWriter().println("EXIT");
        }
    }
}
