package goGame.GUIcomponents.ScoreBoard;

//import goGame.ClientGUI;

import goGame.Client.GoGameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PassButton extends JButton implements ActionListener {
    public PassButton(){
        setText(("PASS"));
        setPreferredSize(new Dimension(100,50));
        addActionListener(this);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        //server.sendPass();
        JOptionPane.showMessageDialog(GoGameClient.getGuiFrame(), "Pass.");
    }
}
