package goGame.GUIcomponents.ScoreBoard;

import goGame.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExitButton extends JButton implements ActionListener {
    public ExitButton(){
        setText(("EXIT"));
        setPreferredSize(new Dimension(100,50));
        addActionListener(this);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        //server.sendExit();
        JOptionPane.showMessageDialog(ClientGUI.getGoBoard(), "Exit.");
        //server.showStartMenu();
    }
}
