package goGame.Server.Bot;

import goGame.GameLogic.Player;

public interface IMoveState {
    int findBestField(Bot player);
    void setPointsSystem();
}
