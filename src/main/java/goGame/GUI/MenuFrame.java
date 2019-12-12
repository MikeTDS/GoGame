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
        setSize(new Dimension(300,300));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setGamesList();
        setButton();
    }
    public void addGamesToList(String game){
        _gamesToChoose.add(game);
    }
    private void setButton(){
        _chooseGameButton = new JButton();
        _chooseGameButton.setText("Connect");
        _chooseGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GoGameClient.sendChosenGame(_gamesToChoose.getSelectedIndex());
            }
        } );
        _chooseGameButton.setPreferredSize(new Dimension(300,50));
        add(_chooseGameButton, BorderLayout.PAGE_END);
    }
    private void setGamesList(){
        _gamesToChoose = new List();
        _gamesToChoose.setSize(new Dimension(600, 300));
        add(_gamesToChoose, BorderLayout.PAGE_START);
    }
}
