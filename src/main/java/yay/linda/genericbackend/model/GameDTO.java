package yay.linda.genericbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;
import static yay.linda.genericbackend.model.Constants.md5Hash;

@Data
@NoArgsConstructor
public class GameDTO implements Comparable<GameDTO> {

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
    private Boolean useAdvancedConfigs;
    private AdvancedGameConfigurationDTO advancedGameConfigs;
    private String currentTimestamp;
    private Boolean isAi;

    public GameDTO(Game game, boolean isPlayer1) {
        this.id = game.getId();
        this.username = isPlayer1 ? game.getPlayer1() : game.getPlayer2();
        this.opponentName = isPlayer1 ? game.getPlayer2() : game.getPlayer1();
        this.board = convertBoardToCellDTO(game.getBoardMap().get(username));
        this.transitionBoard = convertBoardToCellDTO(game.getTransitionBoardMap().get(username));
        this.previousBoard = convertBoardToCellDTO(game.getPreviousBoardMap().get(username));
        this.cards = game.getCardsMap().get(username);
        this.currentTurn = calculateCurrentTurn(isPlayer1, game.getPlayer1sTurn(), game.getStatus());
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
        this.useAdvancedConfigs = game.getUseAdvancedConfigs();
        this.advancedGameConfigs = game.getAdvancedGameConfigs();
        this.currentTimestamp = SIMPLE_DATE_FORMAT.format(Date.from(Instant.now()));
        this.isAi = game.getIsAi();
    }

    public static GameDTO gameDTOForJoinableList(Game game) {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(game.getId());
        gameDTO.setOpponentName(game.getPlayer1());
        gameDTO.setCreatedDate(SIMPLE_DATE_FORMAT.format(game.getCreatedDate()));
        gameDTO.setLastModifiedDate(SIMPLE_DATE_FORMAT.format(game.getLastModifiedDate()));
        gameDTO.setCurrentTurn(calculateCurrentTurn(false, game.getPlayer1sTurn(), game.getStatus())); // if player1 has ended turn after creating the game
        gameDTO.setOpponentPoints(0);
        gameDTO.setPoints(0);
        gameDTO.setUsername("<TBD>");
        gameDTO.setUseAdvancedConfigs(game.getUseAdvancedConfigs());
        gameDTO.setAdvancedGameConfigs(game.getAdvancedGameConfigs());
        gameDTO.setCurrentTimestamp(SIMPLE_DATE_FORMAT.format(Date.from(Instant.now())));
        return gameDTO;
    }

    public static boolean calculateCurrentTurn(boolean isPlayer1, boolean isPlayer1sTurn, GameStatus status) {
        if (status == GameStatus.COMPLETED) {
            return false;
        }
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

    @Override
    public int compareTo(GameDTO o) {
        return this.getLastModifiedDate().compareTo(o.getLastModifiedDate()) * -1;
    }
}
