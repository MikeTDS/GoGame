package goGame.GUI;

import goGame.GameLogic.Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MenuFrame extends JFrame {
    private ArrayList<Game> _gamesList;
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
    public void addGamesToList(ArrayList<Game> _games){
        _gamesList = _games;
        for(int i=0; i<_games.size(); i++){
            _gamesToChoose.add(_games.get(i).getName(), i);
        }
    }
//    public void getPlayerTable(boolean[][] player){
//
//    }
}
