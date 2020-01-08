package goGame.DataBase;

public class MatchData {
    Boolean finished;
    Boolean withBot;
    int id;
    String name;
    StringBuilder whiteMoves;
    StringBuilder blackMoves;
    int[] blockedField = {-1,-1};
    public MatchData(String game, Boolean withBot){
        this.name = game;
        this.withBot=withBot;
        finished=false;
        whiteMoves = new StringBuilder();
        blackMoves = new StringBuilder();
    }
    public void addMove(String color, int x, int y){
        if(color.equalsIgnoreCase("white"))
            whiteMoves.append(x + ";" + y + "\n");
        else if(color.equalsIgnoreCase("black"))
            blackMoves.append(x + ";" + y + "\n");
    }
    public void setBlockedField(int x, int y){
        blockedField[0]=x;
        blockedField[1]=y;
    }
    public void setStatusToFinished(){
        finished=true;
    }
    public String setWhiteDataToSend(){
        return whiteMoves.toString();
    }
    public String setBlackDataToSend(){
        return blackMoves.toString();
    }
}
