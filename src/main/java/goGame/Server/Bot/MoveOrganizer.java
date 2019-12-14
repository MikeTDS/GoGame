package goGame.Server.Bot;

class MoveOrganizer {
    private MoveState _moveState;
    MoveOrganizer() {
    }

    void setMoveState(MoveState moveState) {
        _moveState = moveState;
    }
    int getBestField(Bot bot) {
        return _moveState.findBestField(bot);
    }
}
