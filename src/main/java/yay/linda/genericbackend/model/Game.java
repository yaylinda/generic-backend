package yay.linda.genericbackend.model;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import java.time.Instant;
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
    private String winner;
    private Map<String, GameStats> gameStatsMap;
    private Map<String, List<Card>> endzoneMap;
    private GameConfiguration gameConfig;
    private Boolean isAi;

    /**
     *
     * @param gameConfiguration
     * @param isAi
     */
    public Game(GameConfiguration gameConfiguration, Boolean isAi) {
        this.boardMap = new HashMap<>();
        this.transitionBoardMap = new HashMap<>();
        this.previousBoardMap = new HashMap<>();
        this.cardsMap = new HashMap<>();
        this.pointsMap = new HashMap<>();
        this.energyMap = new HashMap<>();
        this.createdDate = Date.from(Instant.now());
        this.gameStatsMap = new HashMap<>();
        this.endzoneMap = new HashMap<>();
        this.gameConfig = gameConfiguration;
        this.isAi = isAi;
    }

    /**
     * @param player1
     */
    public void createGameForPlayer1(String player1) {
        this.player1 = player1;
        this.player2 = "<TBD>";
        this.player1sTurn = true;
        this.boardMap.put(player1, initializeBoard(gameConfig.getNumRows(), gameConfig.getNumCols()));
        this.transitionBoardMap.put(player1, initializeBoard(gameConfig.getNumRows(), gameConfig.getNumCols()));
        this.previousBoardMap.put(player1, initializeBoard(gameConfig.getNumRows(), gameConfig.getNumCols()));
        this.pointsMap.put(player1, 0);
        this.energyMap.put(player1, gameConfig.getStartingEnergy());
        this.cardsMap.put(player1, new ArrayList<>(Card.generateCards(player1, gameConfig.getNumCardsInHand(), gameConfig.getDropRates())));
        this.gameStatsMap.put(player1, new GameStats());
        this.createdDate = Date.from(Instant.now());
        this.lastModifiedDate = Date.from(Instant.now());
        this.status = GameStatus.WAITING_PLAYER_2;
        this.endzoneMap.put(player1, new ArrayList<>());
    }

    /**
     * @param player2
     */
    public void addPlayer2ToGame(String player2) {
        this.player2 = player2;
        this.boardMap.put(player2, transpose(this.boardMap.get(this.player1)));
        this.transitionBoardMap.put(player2, initializeBoard(gameConfig.getNumRows(), gameConfig.getNumCols()));
        this.previousBoardMap.put(player2, initializeBoard(gameConfig.getNumRows(), gameConfig.getNumCols()));
        this.pointsMap.put(player2, 0);
        this.energyMap.put(player2, gameConfig.getStartingEnergy());
        this.cardsMap.put(player2, new ArrayList<>(Card.generateCards(player2, gameConfig.getNumCardsInHand(), gameConfig.getDropRates())));
        this.gameStatsMap.put(player2, new GameStats());
        this.player2JoinTime = Date.from(Instant.now());
        this.lastModifiedDate = Date.from(Instant.now());
        this.status = GameStatus.IN_PROGRESS;
        this.endzoneMap.put(player2, new ArrayList<>());
    }

    /**
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
     * @param username
     */
    public void incrementEnergyForEndTurn(String username) {
        if (gameConfig.getResetEnergyPerTurn()) {
            this.getEnergyMap().put(username, Math.min(gameConfig.getStartingEnergy() + this.gameStatsMap.get(username).getNumTurns() * gameConfig.getEnergyGrowthRate(), gameConfig.getMaxEnergy()));
        } else {
            this.getEnergyMap().put(username, Math.min(this.getEnergyMap().get(username) + gameConfig.getEnergyGrowthRate(), gameConfig.getMaxEnergy()));
        }
    }

    /**
     *
     * @param username
     * @param cost
     */
    public void decrementEnergyForPutCard(String username, Double cost) {
        this.getEnergyMap().put(username, this.getEnergyMap().get(username) - cost);
    }

    /**
     *
     * @param username
     * @param used
     */
    public void incrementEnergyUsed(String username, Double used) {
        this.gameStatsMap.get(username).incrementEnergyUsed(used);
    }

    /**
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

    /**
     *
     * @param username
     */
    public void incrementNumCardsPlayed(String username) {
        this.gameStatsMap.get(username).incrementNumCardsPlayed();
    }

    /**
     *
     * @param username
     * @param might
     */
    public void incrementMightPlaced(String username, Integer might) {
        this.gameStatsMap.get(username).incrementMightPlaced(might);
    }

    /**
     *
     * @param username
     * @param advancementPoints
     */
    public void incrementAdvancementPoints(String username, Integer advancementPoints) {
        this.gameStatsMap.get(username).incrementAdvancementPoints(advancementPoints);
    }

    /**
     * @param username
     */
    public void updatePreviousBoard(String username) {
        List<List<Cell>> board = new ArrayList<>(this.getBoardMap().get(username));
        LOGGER.info("updating previous board for {} to {}", username, board);
        this.getPreviousBoardMap().put(username, board);
    }

    /**
     * @param username
     */
    public void updateTransitionalBoard(String username) {
        List<List<Cell>> board = new ArrayList<>(this.getBoardMap().get(username));
        for (int i = 0; i < gameConfig.getNumRows(); i++) {
            for (int j = 0; j < gameConfig.getNumCols(); j++) {
                Cell cell = board.get(i).get(j);
                for (Card card : cell.getCards()) {
                    if (card.isQualifiedToAdvance(username)) {
                        if (card.getMovementAxis() == MovementAxis.VERTICAL) {
                            LOGGER.info("advancing for vertical... at {},{}", i, j);
                            int newRow = i - card.getMovement();
                            LOGGER.info("\tnewRow is now {}", newRow);
                            if (newRow < 0) { // card has moved into endzone and points have been scored
                                this.pointsMap.put(username, this.pointsMap.get(username) + card.getMight());
                                this.endzoneMap.get(username).add(card);
                            } else {
                                Cell cellAtNewRow = board.get(newRow).get(j);
                                cellAtNewRow.addCard(card);
                            }
                            cell.removeCard(card);
                        } else if (card.getMovementAxis() == MovementAxis.HORIZONTAL) {
                            LOGGER.info("advancing for horizontal... at {},{}", i, j);
                            int newCol;

                            if (card.getMovementDirection() == MovementDirection.RIGHT) {
                                LOGGER.info("\tmovement dir is RIGHT");
                                if (j == gameConfig.getNumCols() - 1) {
                                    LOGGER.info("\tcard is at edge of RIGHT side. going LEFT instead");
                                    newCol = j - card.getMovement();
                                    card.setMovementDirection(MovementDirection.LEFT);
                                } else {
                                    newCol = j + card.getMovement();
                                }
                            } else {
                                LOGGER.info("\tmovement dir is LEFT");
                                if (j == 0) {
                                    LOGGER.info("\tcard is at edge of LEFT side. going RIGHT instead");
                                    newCol = j + card.getMovement();
                                    card.setMovementDirection(MovementDirection.RIGHT);
                                } else {
                                    newCol = j - card.getMovement();
                                }
                            }
                            LOGGER.info("\tnewCol is now {}", newCol);

                            card.setShouldAdvance(false);
                            Cell cellAtNewCol = board.get(i).get(newCol);
                            cellAtNewCol.addCard(card);
                            cell.removeCard(card);
                        }
                    }
                }
            }
        }

        // reset shouldAdvance for each card
        board.forEach(r -> r.forEach(c -> c.getIdToCardMap().values().forEach(cc -> cc.setShouldAdvance(true))));

        LOGGER.info("updating transition board for {} to {}", username, board);
        this.getTransitionBoardMap().put(username, board);
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    /**
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
     * @param original
     * @return
     */
    private static List<List<Cell>> transpose(List<List<Cell>> original) {
        List<List<Cell>> transposed = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            transposed.add(original.get(i));
        }
        return transposed;
    }

    /**
     *
     * @param username
     * @param opponentName
     */
    public void updateCurrentBoard(String username, String opponentName) {
        List<List<Cell>> board = new ArrayList<>(this.getTransitionBoardMap().get(username));
        for (int i = 0; i < gameConfig.getNumRows(); i++) {
            for (int j = 0; j < gameConfig.getNumCols(); j++) {
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
     * @param username
     * @param opponentName
     */
    public void updateOpponentBoard(String username, String opponentName) {
        this.previousBoardMap.put(opponentName, transpose(this.previousBoardMap.get(username)));
        this.transitionBoardMap.put(opponentName, transpose(this.transitionBoardMap.get(username)));
        this.boardMap.put(opponentName, transpose(this.boardMap.get(username)));
    }
}
