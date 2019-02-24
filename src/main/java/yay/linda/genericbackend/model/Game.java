package yay.linda.genericbackend.model;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import yay.linda.genericbackend.service.CardGeneratorUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    @Id
    private String id;
    private String player1;
    private String player2;
    private Boolean player1sTurn;
    private Map<String, List<List<Cell>>> boardMap;
    private Map<String, List<List<Cell>>> transitionBoardMap;
    private Map<String, List<List<Cell>>> previousBoardMap;
    private Map<String, Integer> pointsMap;
    private Map<String, Double> energyMap;
    private Map<String, List<Card>> cardsMap;
    private Date createdDate;
    private Date lastModifiedDate;
    private Date player2JoinTime;
    private Date completedDate;
    private GameStatus status;
    private Integer numRows;
    private Integer numCols;
    private Integer numCardsInHand;
    private Integer minTerritoryRowNum;
    private String winner;
    private Map<String, GameStats> gameStatsMap;
    private Map<String, List<Card>> endzoneMap;

    public Game(int numRows, int numCols, int numCardsInHand, int numTerritoryRows) {
        this.boardMap = new HashMap<>();
        this.transitionBoardMap = new HashMap<>();
        this.previousBoardMap = new HashMap<>();
        this.cardsMap = new HashMap<>();
        this.pointsMap = new HashMap<>();
        this.energyMap = new HashMap<>();
        this.createdDate = new Date();
        this.numRows = numRows;
        this.numCols = numCols;
        this.numCardsInHand = numCardsInHand;
        this.minTerritoryRowNum = numRows - numTerritoryRows;
        this.gameStatsMap = new HashMap<>();
        this.endzoneMap = new HashMap<>();
    }

    /**
     *
     * @param player1
     */
    public void createGameForPlayer1(String player1) {
        this.player1 = player1;
        this.player2 = "<TBD>";
        this.player1sTurn = true;
        this.boardMap.put(player1, initializeBoard(numRows, numCols));
        this.transitionBoardMap.put(player1, initializeBoard(numRows, numCols));
        this.previousBoardMap.put(player1, initializeBoard(numRows, numCols));
        this.pointsMap.put(player1, 0);
        this.energyMap.put(player1, 1.0);
        this.cardsMap.put(player1, new ArrayList<>(CardGeneratorUtil.generateCards(player1, numCardsInHand)));
        this.gameStatsMap.put(player1, new GameStats());
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.status = GameStatus.WAITING_PLAYER_2;
        this.endzoneMap.put(player1, new ArrayList<>());
    }

    /**
     *
     * @param player2
     */
    public void addPlayer2ToGame(String player2) {
        this.player2 = player2;
        this.boardMap.put(player2, transpose(this.boardMap.get(this.player1)));
        this.transitionBoardMap.put(player2, initializeBoard(numRows, numCols));
        this.previousBoardMap.put(player2, initializeBoard(numRows, numCols));
        this.pointsMap.put(player2, 0);
        this.energyMap.put(player2, 2.0);
        this.cardsMap.put(player2, new ArrayList<>(CardGeneratorUtil.generateCards(player2, numCardsInHand)));
        this.gameStatsMap.put(player2, new GameStats());
        this.player2JoinTime = new Date();
        this.lastModifiedDate = new Date();
        this.status = GameStatus.IN_PROGRESS;
        this.endzoneMap.put(player2, new ArrayList<>());
    }

    /**
     *
     * @param username
     * @param row
     * @param col
     * @param card
     */
    public void putCardOnBoard(String username, int row, int col, Card card) {
        this.previousBoardMap.put(username, new ArrayList<>(this.boardMap.get(username)));
        this.boardMap.get(username).get(row).get(col).addCard(card);
    }

    /**
     *
     * @param username
     */
    public void incrementEnergyForEndTurn(String username) {
        this.getEnergyMap().put(username, Math.min(1.0 + this.gameStatsMap.get(username).getNumTurns(), 10));
    }

    public void decrementEnergyForPutCard(String username, Double cost) {
        this.getEnergyMap().put(username, this.getEnergyMap().get(username) - cost);
    }

    public void incrementEnergyUsed(String username, Double used) {
        this.gameStatsMap.get(username).incrementEnergyUsed(used);
    }

    /**
     *
     * @param username
     */
    public void incrementNumTurns(String username) {
        // increment num turns of player
        this.gameStatsMap.get(username).incrementNumTurns();

        // increment num turns on board for each card on the board
        boardMap.get(username).forEach(rows ->
                rows.forEach(cell ->
                        cell.incrementCardsNumTurnsOnBoard(username)));
    }

    public void incrementNumCardsPlayed(String username) {
        this.gameStatsMap.get(username).incrementNumCardsPlayed();
    }

    public void incrementMightPlaced(String username, Integer might) {
        this.gameStatsMap.get(username).incrementMightPlaced(might);
    }

    public void incrementAdvancementPoints(String username, Integer advancementPoints) {
        this.gameStatsMap.get(username).incrementAdvancementPoints(advancementPoints);
    }

    /**
     *
     * @param username
     */
    public void updatePreviousBoard(String username) {
        List<List<Cell>> board = new ArrayList<>(this.getBoardMap().get(username));
        LOGGER.info("updating previous board for {} to {}", username, board);
        this.getPreviousBoardMap().put(username, board);
    }

    /**
     *
     * @param username
     */
    public void updateTransitionalBoard(String username) {
        // TODO implement logic for card with side to side movemet
        List<List<Cell>> board = new ArrayList<>(this.getBoardMap().get(username));
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Cell cell = board.get(i).get(j);
                for (Card card : cell.getCards()) {
                    if (card.isQualifiedToAdvance(username)) {
                        int newRow = i - card.getMovement();
                        if (newRow < 0) { // card has moved into endzone and points have been scored
                            this.pointsMap.put(username, this.pointsMap.get(username) + card.getMight());
                            this.endzoneMap.get(username).add(card);
                        } else {
                            Cell cellAtNewRow = board.get(newRow).get(j);
                            cellAtNewRow.addCard(card);
                        }
                        cell.removeCard(card);
                    }
                }
            }
        }
        LOGGER.info("updating transition board for {} to {}", username, board);
        this.getTransitionBoardMap().put(username, board);
    }

    public void updateCurrentBoard(String username, String opponentName) {
        List<List<Cell>> board = new ArrayList<>(this.getTransitionBoardMap().get(username));
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Cell cell = board.get(i).get(j);
                if (cell.getCards().size() > 1) {
                    int advancementPoints = 0;
                    cell.handleClash(username, opponentName, advancementPoints);
                    this.incrementAdvancementPoints(username, advancementPoints);
                }
            }
        }
        LOGGER.info("updating current board for {} to {}", username, board);
        this.getBoardMap().put(username, board);
    }

    /**
     *
     * @param username
     * @param opponentName
     */
    public void updateOpponentBoard(String username, String opponentName) {
        this.previousBoardMap.put(opponentName, transpose(this.previousBoardMap.get(username)));
        this.transitionBoardMap.put(opponentName, transpose(this.transitionBoardMap.get(username)));
        this.boardMap.put(opponentName, transpose(this.boardMap.get(username)));
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    /**
     *
     * @return
     */
    private static List<List<Cell>> initializeBoard(int numRows, int numCols) {
        List<List<Cell>> board = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                row.add(new Cell());
            }
            board.add(row);
        }
        return board;
    }

    /**
     *
     * @param original
     * @return
     */
    private static List<List<Cell>> transpose(List<List<Cell>> original) {
        List<List<Cell>> transposed = new ArrayList<>();
        for (int i = original.size() - 1; i >=0; i--) {
            transposed.add(original.get(i));
        }
        return transposed;
    }
}
