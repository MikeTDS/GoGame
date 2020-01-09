package goGame.DataBase;

import javax.persistence.*;

@Entity
@Table(name="saved_games")
public class MatchData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="finished")
    private Boolean finished;
    @Column(name="with_bot")
    private Boolean withBot;
    @Column(name="name")
    private String name;
    @Column(name="white_moves")
    private String whiteMoves;
    @Column(name="black_moves")
    private String blackMoves;
    @Column(name="blocked_field")
    private int blockedField;
    @Column(name="board_size")
    private int boardSize;
    public MatchData(String game){
        this.name = game;
        finished=false;
        withBot=false;
        blockedField=-1;
        whiteMoves="";
        blackMoves="";
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getWithBot() {
        return withBot;
    }

    public int getId() {
        return id;
    }

    public String getWhiteMoves() {
        return whiteMoves;
    }

    public void setWhiteMoves(String whiteMoves) {
        this.whiteMoves = whiteMoves;
    }

    public String getBlackMoves() {
        return blackMoves;
    }

    public void setBlackMoves(String blackMoves) {
        this.blackMoves = blackMoves;
    }

    public String getName() {
        return name;
    }
    public void setBlockedField(int id){
        blockedField = id;
    }
    public int getBlockedField() {
        return blockedField;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public void setWithBot(Boolean withBot) {
        this.withBot = withBot;
    }
}
