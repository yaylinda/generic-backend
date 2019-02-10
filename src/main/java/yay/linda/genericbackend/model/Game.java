package yay.linda.genericbackend.model;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import yay.linda.genericbackend.service.CardGeneratorUtil;

import java.util.*;

@Data
public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    @Id
    private String id;
    private String player1;
    private String player2;
    private boolean player1sTurn;
    private Map<String, List<List<Cell>>> boardMap;
    private Map<String, List<List<Cell>>> previousBoardMap;
    private Map<String, Integer> pointsMap;
    private Map<String, Double> energyMap;
    private Map<String, List<Card>> cardsMap;
    private Map<String, Integer> numTurnsMap;
    private Map<String, Integer> numCardsPlayedMap;
    private Date createdDate;
    private Date lastModifiedDate;
    private Date player2JoinTime;
    private Date completedDate;
    private GameStatus status;
    private int numRows;
    private int numCols;
    private int numCardsInHand;
    private int minTerritoryRowNum;
    private String winner;

    public Game(int numRows, int numCols, int numCardsInHand) {
        this.boardMap = new HashMap<>();
        this.previousBoardMap = new HashMap<>();
        this.cardsMap = new HashMap<>();
        this.pointsMap = new HashMap<>();
        this.energyMap = new HashMap<>();
        this.numTurnsMap = new HashMap<>();
        this.numCardsPlayedMap = new HashMap<>();
        this.createdDate = new Date();
        this.numRows = numRows;
        this.numCols = numCols;
        this.numCardsInHand = numCardsInHand;
        this.minTerritoryRowNum = numRows - 2; // TODO: 2 rows hardcoded for now
    }

    /**
     *
     * @param player1
     */
    public void createGameForPlayer1(String player1) {
        this.player1 = player1;
        this.player2 = "<TBD>";
        this.player1sTurn = true;
        this.boardMap.put(player1, this.initializeBoard(numRows, numCols));
        this.previousBoardMap.put(player1, this.initializeBoard(numRows, numCols));
        this.pointsMap.put(player1, 0);
        this.energyMap.put(player1, 1.0);
        this.cardsMap.put(player1, new ArrayList<>(CardGeneratorUtil.generateCards(player1, numCardsInHand)));
        this.numCardsPlayedMap.put(player1, 0);
        this.numTurnsMap.put(player1, 0);
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.status = GameStatus.WAITING_PLAYER_2;
    }

    /**
     *
     * @param player2
     */
    public void addPlayer2ToGame(String player2) {
        this.player2 = player2;
        this.boardMap.put(player2, this.transpose(this.boardMap.get(this.player1)));
        this.previousBoardMap.put(player2, this.initializeBoard(numRows, numCols));
        this.pointsMap.put(player2, 0);
        this.energyMap.put(player2, 2.0);
        this.cardsMap.put(player2, new ArrayList<>(CardGeneratorUtil.generateCards(player2, numCardsInHand)));
        this.numCardsPlayedMap.put(player2, 0);
        this.numTurnsMap.put(player2, 0);
        this.player2JoinTime = new Date();
        this.lastModifiedDate = new Date();
        this.status = GameStatus.IN_PROGRESS;
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
        this.boardMap.get(username).get(row).get(col).setCard(card);
    }

    /**
     *
     * @param username
     */
    public void incrementEnergy(String username) {
        double baseEnergy;
        if (username.equalsIgnoreCase(this.player1)) {
            baseEnergy = 1;
        } else {
            baseEnergy = 2;
        }
        this.getEnergyMap().put(username, Math.min(baseEnergy + this.getNumTurnsMap().get(username), 10));
    }

    /**
     *
     * @param username
     */
    public void incrementNumTurns(String username) {
        this.getNumTurnsMap().put(username, this.getNumTurnsMap().get(username) + 1);
        boardMap.get(username).forEach(rows ->
                rows.forEach(cell -> {
                    if (cell.getCard() != null && cell.getCard().getOwner().equals(username)) {
                        cell.getCard().setNumTurnsOnBoard(cell.getCard().getNumTurnsOnBoard() + 1);
                    }
                }));
    }

    /**
     *
     * @param username
     */
    public void updatePreviousBoard(String username) {
        List<List<Cell>> board = new ArrayList<>(this.getBoardMap().get(username));
        this.getPreviousBoardMap().put(username, board);
    }

    /**
     *
     * @param username
     * @param opponentName
     */
    public void advanceTroops(String username, String opponentName) {
        LOGGER.info("advancing troops for {}", username);
        List<List<Cell>> board = initializeBoard(numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Cell cell = this.getBoardMap().get(username).get(i).get(j);
                if (cell.getCard() != null) {
                    Card card = cell.getCard();
                    if (card.getType() == CardType.TROOP && card.getOwner().equals(username) && card.getNumTurnsOnBoard() > 0) {
                        int newRow = i - cell.getCard().getMovement();
                        if (newRow < 0) {
                            this.pointsMap.put(username, this.pointsMap.get(username) + card.getMight());
                        } else {
                            Cell cellAtNewPosition = this.getBoardMap().get(username).get(newRow).get(j);
                            if (cellAtNewPosition.getCard() != null) {
                                LOGGER.info("clash!");
                                LOGGER.info("user's card: {}", card);
                                Card cardAtNewPosition = cellAtNewPosition.getCard();
                                LOGGER.info("card at new position: {}", cardAtNewPosition);
                                if (cardAtNewPosition.getOwner().equals(username)) {
                                    if (cardAtNewPosition.getType() == card.getType()) {
                                        cell.getCard().setMight(card.getMight() + cardAtNewPosition.getMight());
                                    } else {
                                        cell.getCard().setMight(card.getMight() + cardAtNewPosition.getMight());
                                        cell.getCard().setType(CardType.WALL);
                                    }
                                } else {
                                    int mightDiff = card.getMight() - cardAtNewPosition.getMight();
                                    LOGGER.info("mightDiff: " + mightDiff);
                                    if (mightDiff > 0) {
                                        cell.getCard().setMight(mightDiff);
                                    } else if (mightDiff < 0) {
                                        cell.getCard().setMight(mightDiff * -1);
                                        cell.getCard().setOwner(opponentName);
                                    } else {
                                        cell.setCard(null);
                                    }
                                }
                            }
                            board.get(newRow).set(j, cell);
                        }
                    } else {
                        board.get(i).set(j, cell);
                    }
                }
            }
        }
        this.getBoardMap().put(username, board);
    }

    /**
     *
     * @param username
     * @param opponentName
     */
    public void advanceTroopsForOpponent(String username, String opponentName) {
        this.getBoardMap().put(opponentName, this.transpose(this.getBoardMap().get(username)));
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    /**
     *
     * @return
     */
    private List<List<Cell>> initializeBoard(int numRows, int numCols) {
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
    private List<List<Cell>> transpose(List<List<Cell>> original) {
        List<List<Cell>> transposed = new ArrayList<>();
        for (int i = original.size() - 1; i >=0; i--) {
            transposed.add(original.get(i));
        }
        return transposed;
    }
}
