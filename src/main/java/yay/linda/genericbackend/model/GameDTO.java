package yay.linda.genericbackend.model;

import lombok.Data;

import java.util.List;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;
import static yay.linda.genericbackend.model.Constants.md5Hash;

@Data
public class GameDTO {

    private String id;
    private String username;
    private String opponentName;
    private List<List<Cell>> board;
    private List<List<Cell>> previousBoard;
    private List<Card> cards;
    private boolean currentTurn;
    private int points;
    private double energy;
    private GameStatus status;
    private int numTurns;
    private int opponentPoints;
    private int numCardsPlayed;
    private int numRows;
    private int numCols;
    private String md5Hash;
    private String createdDate;
    private String lastModifiedDate;
    private String player2JoinDate;
    private String completedDate;

    public GameDTO(Game game, boolean isPlayer1) {
        this.id = game.getId();
        this.username = isPlayer1 ? game.getPlayer1() : game.getPlayer2();
        this.opponentName = isPlayer1 ? game.getPlayer2() : game.getPlayer1();
        this.board = game.getBoardMap().get(username);
        this.previousBoard = game.getPreviousBoardMap().get(username);
        this.cards = game.getCardsMap().get(username);
        this.currentTurn = calculateCurrentTurn(isPlayer1, game.isPlayer1sTurn());
        this.points = game.getPointsMap().get(username);
        this.energy = game.getEnergyMap().get(username);
        this.status = game.getStatus();
        this.numTurns = game.getNumTurnsMap().get(username);
        this.numCardsPlayed = game.getNumCardsPlayedMap().get(username);
        this.opponentPoints = !this.opponentName.equals("<TBD>") ? game.getPointsMap().get(this.opponentName) : 0;
        this.numRows = this.board.size();
        this.numCols = this.board.get(0).size();
        this.md5Hash = md5Hash(game);
        this.createdDate = SIMPLE_DATE_FORMAT.format(game.getCreatedDate());
        this.lastModifiedDate = SIMPLE_DATE_FORMAT.format(game.getLastModifiedDate());
        this.player2JoinDate = game.getPlayer2JoinTime() != null ? SIMPLE_DATE_FORMAT.format(game.getPlayer2JoinTime()) : null;
        this.completedDate = game.getCompletedDate() != null ? SIMPLE_DATE_FORMAT.format(game.getCompletedDate()) : null;
    }

    private boolean calculateCurrentTurn(boolean isPlayer1, boolean isPlayer1sTurn) {
        if (isPlayer1) {
            return isPlayer1sTurn;
        } else {
            return !isPlayer1sTurn;
        }
    }
}
