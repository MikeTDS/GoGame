package goGame.GUI;

import goGame.GameLogic.Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MenuFrame extends JFrame {
    private List _gamesToChoose;

    public MenuFrame(){
        initizalizeMenu();
        setVisible(true);
    }
    private void initizalizeMenu(){
        setSize(new Dimension(300,300));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        _gamesToChoose = new List();
        add(_gamesToChoose);
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
