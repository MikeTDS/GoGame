package goGame.DataBase;

import goGame.GameLogic.Stone;

public class MatchDataManager {
    MatchData matchData;
    public MatchDataManager(String game, int boardSize){
        matchData = new MatchData(game);
        matchData.setBoardSize(boardSize);
    }
    public void update(String color, int id, int blockedField){
        matchData.setBlockedField(blockedField);
    }
    public void setWithBot(){
        matchData.setWithBot(true);
    }
    public void setBoard(Stone[] board){
        String white="", black="";
        for(Stone stone : board){
            int pos = stone.getPosX() + stone.getPosY()*matchData.getBoardSize();
            switch (stone.getColor()){
                case "White":
                    white+=pos+";";
                    break;
                case "Black":
                    black+=pos+";";
                    break;
            }
        }
        matchData.setWhiteMoves(white);
        matchData.setBlackMoves(black);
    }
    public void finish(){
        matchData.setFinished(true);
        sendData();
    }
    private void sendData(){
        try {
            HibernateManager.getSessionFactory().openSession();
            HibernateManager.getSessionFactory().getCurrentSession().beginTransaction();
            HibernateManager.getSessionFactory().getCurrentSession().save(matchData);
            HibernateManager.getSessionFactory().getCurrentSession().getTransaction().commit();
            HibernateManager.getSessionFactory().getCurrentSession().close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
