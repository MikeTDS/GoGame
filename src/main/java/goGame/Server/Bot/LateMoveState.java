package goGame.Server.Bot;

class LateMoveState extends MoveState {

    LateMoveState(int[] pointsBoard, BotBrain botBrain) {
        super(pointsBoard, botBrain);
        setPointsSystem();
    }

    @Override
    public int findBestField(Bot player) {
        return 0;
    }

    @Override
    public void setPointsSystem() {

    }
}
