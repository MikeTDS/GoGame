package goGame.GUI;

import goGame.Client.GoGameClient;
import goGame.GameLogic.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MenuFrame extends JFrame{
    private List _gamesToChoose;
    private JButton _chooseGameButton;

    public MenuFrame(){
        initizalizeMenu();
        setVisible(true);
    }
    private void initizalizeMenu(){
        setSize(new Dimension(300,300));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2,1));
        _gamesToChoose = new List();
        add(_gamesToChoose);
        _chooseGameButton = new JButton();
        _chooseGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GoGameClient.sendChosenGame(_gamesToChoose.getSelectedIndex());
            }
        } );
        add(_chooseGameButton);
    }
    public int getItemFromList(){
        return _gamesToChoose.getSelectedIndex();
    }
    public void addGamesToList(String game){
        _gamesToChoose.add(game);
    }
    public List getGamesToChoose(){
        return _gamesToChoose;
    }
//    public void getPlayerTable(boolean[][] player){
//
//    }
}
