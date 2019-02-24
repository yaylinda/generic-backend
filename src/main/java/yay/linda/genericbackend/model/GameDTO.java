package yay.linda.genericbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;
import static yay.linda.genericbackend.model.Constants.md5Hash;

@Data
@NoArgsConstructor
public class GameDTO {

    private String id;
    private String username;
    private String opponentName;
    private List<List<CellDTO>> board;
    private List<List<CellDTO>> transitionBoard;
    private List<List<CellDTO>> previousBoard;
    private List<Card> cards;
    private Boolean currentTurn;
    private Integer points;
    private Double energy;
    private GameStatus status;
    private Integer opponentPoints;
    private Integer numRows;
    private Integer numCols;
    private String md5Hash;
    private String createdDate;
    private String lastModifiedDate;
    private String player2JoinDate;
    private String completedDate;
    private String winner;
    private GameStats gameStats;
    private List<Card> endzone;
    private List<Card> opponentEndzone;

    public GameDTO(Game game, boolean isPlayer1) {
        this.id = game.getId();
        this.username = isPlayer1 ? game.getPlayer1() : game.getPlayer2();
        this.opponentName = isPlayer1 ? game.getPlayer2() : game.getPlayer1();
        this.board = convertBoardToCellDTO(game.getBoardMap().get(username));
        this.transitionBoard = convertBoardToCellDTO(game.getTransitionBoardMap().get(username));
        this.previousBoard = convertBoardToCellDTO(game.getPreviousBoardMap().get(username));
        this.cards = game.getCardsMap().get(username);
        this.currentTurn = calculateCurrentTurn(isPlayer1, game.getPlayer1sTurn());
        this.points = game.getPointsMap().get(username);
        this.energy = game.getEnergyMap().get(username);
        this.status = game.getStatus();
        this.opponentPoints = !this.opponentName.equals("<TBD>") ? game.getPointsMap().get(this.opponentName) : 0;
        this.numRows = this.board.size();
        this.numCols = this.board.get(0).size();
        this.md5Hash = md5Hash(game);
        this.createdDate = SIMPLE_DATE_FORMAT.format(game.getCreatedDate());
        this.lastModifiedDate = SIMPLE_DATE_FORMAT.format(game.getLastModifiedDate());
        this.player2JoinDate = game.getPlayer2JoinTime() != null ? SIMPLE_DATE_FORMAT.format(game.getPlayer2JoinTime()) : null;
        this.completedDate = game.getCompletedDate() != null ? SIMPLE_DATE_FORMAT.format(game.getCompletedDate()) : null;
        this.winner = game.getWinner();
        this.gameStats = game.getGameStatsMap().get(username);
        this.endzone = game.getEndzoneMap().getOrDefault(username, new ArrayList<>());
        this.opponentEndzone = game.getEndzoneMap().getOrDefault(opponentName, new ArrayList<>());
    }

    public static GameDTO gameDTOForJoinableList(Game game) {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(game.getId());
        gameDTO.setOpponentName(game.getPlayer1());
        gameDTO.setCreatedDate(SIMPLE_DATE_FORMAT.format(game.getCreatedDate()));
        gameDTO.setCurrentTurn(calculateCurrentTurn(false, game.getPlayer1sTurn())); // if player1 has ended turn after creating the game
        return gameDTO;
    }

    private static boolean calculateCurrentTurn(boolean isPlayer1, boolean isPlayer1sTurn) {
        if (isPlayer1) {
            return isPlayer1sTurn;
        } else {
            return !isPlayer1sTurn;
        }
    }

    private static List<List<CellDTO>> convertBoardToCellDTO(List<List<Cell>> original) {
        List<List<CellDTO>> result = new ArrayList<>();
        for (List<Cell> row : original) {
            List<CellDTO> cellDTOs = new ArrayList<>();
            for (Cell cell : row) {
                cellDTOs.add(new CellDTO(cell));
            }
            result.add(cellDTOs);
        }
        return result;
    }
}
