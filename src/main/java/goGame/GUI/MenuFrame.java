package goGame.GUI;

import goGame.Client.GoGameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuFrame extends JFrame{
    private List _gamesToChoose;
    private JButton _chooseGameButton;

    public MenuFrame(){
        initializeMenu();
        setVisible(true);
    }
    private void initializeMenu(){
        setTitle("Game of Go : Lobby");
        setSize(new Dimension(600,600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        _gamesToChoose = new List();
        add(_gamesToChoose);
        setButton();
    }
    public void addGamesToList(String game){
        _gamesToChoose.add(game);
    }
    private void setButton(){
        _chooseGameButton = new JButton();
        _chooseGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GoGameClient.sendChosenGame(_gamesToChoose.getSelectedIndex());
            }
        } );
        setPreferredSize(new Dimension(100,500));
        add(_chooseGameButton);
    }
}
