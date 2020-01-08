package goGame.DataBase;

public class MatchData {
    StringBuilder data;
    public MatchData(String game){
        addMove(game + "\n");
    }
    public void addMove(String move){
        data.append(move);
    }
    public String setDataToSend(){
        return data.toString();
    }
}
