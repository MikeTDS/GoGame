package goGame.Server.Bot;

public interface IBot{
    void findBestField();
    void makeMove(int x, int y);
    void decideIfPass();
}
