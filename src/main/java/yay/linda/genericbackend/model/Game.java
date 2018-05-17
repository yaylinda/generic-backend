package yay.linda.genericbackend.model;

import org.springframework.data.annotation.Id;
import yay.linda.genericbackend.service.CardGeneratorUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Game {

    @Id
    private String id;
    private String player1;
    private String player2;
    private boolean player1sTurn;
    private Map<String, ArrayList<ArrayList<Cell>>> boardMap;
    private Map<String, ArrayList<ArrayList<Cell>>> previousBoardMap;
    private Map<String, Integer> pointsMap;
    private Map<String, Integer> energyMap;
    private Map<String, ArrayList<Card>> cardsMap;
    private Map<String, Integer> numTurnsMap;
    private Date createdDate;
    private Date player2JoinTime;
    private Date completedDate;
    private GameStatus status;
    private int numRows;
    private int numCols;
    private int numCardsInHand;

    public Game(int numRows, int numCols, int numCardsInHand) {
        this.boardMap = new HashMap<>();
        this.previousBoardMap = new HashMap<>();
        this.cardsMap = new HashMap<>();
        this.pointsMap = new HashMap<>();
        this.energyMap = new HashMap<>();
        this.numTurnsMap = new HashMap<>();
        this.createdDate = new Date();
        this.numRows = numRows;
        this.numCols = numCols;
        this.numCardsInHand = numCardsInHand;
    }

    /**
     *
     * @param player1
     */
    public void createGameForPlayer1(String player1) {
        this.player1 = player1;
        this.player1sTurn = true;
        this.boardMap.put(player1, this.initializeBoard(numRows, numCols));
        this.previousBoardMap.put(player1, this.initializeBoard(numRows, numCols));
        this.pointsMap.put(player1, 0);
        this.energyMap.put(player1, 1);
        this.cardsMap.put(player1, new ArrayList<>(CardGeneratorUtil.generateCards(player1, numCardsInHand)));
        this.numTurnsMap.put(player1, 0);
        this.createdDate = new Date();
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
        this.energyMap.put(player2, 2);
        this.cardsMap.put(player2, new ArrayList<>(CardGeneratorUtil.generateCards(player2, numCardsInHand)));
        this.numTurnsMap.put(player2, 0);
        this.player2JoinTime = new Date();
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
        this.boardMap.get(username).get(row).get(col).setState(CellState.OCCUPIED);
    }

    /**
     *
     * @param username
     */
    public void incrementEnergy(String username) {
        this.getEnergyMap().put(username, this.energyMap.get(username) + 1);
    }

    /**
     *
     * @param username
     */
    public void incrementNumTurns(String username) {
        this.getNumTurnsMap().put(username, this.getNumTurnsMap().get(username) + 1);
    }

    /**
     *
     * @param username
     */
    public void updatePreviousBoard(String username) {
        ArrayList<ArrayList<Cell>> board = new ArrayList<>(this.getBoardMap().get(username));
        this.getPreviousBoardMap().put(username, board);
    }

    /**
     *
     * @param username
     * @param opponentName
     */
    public void advanceTroops(String username, String opponentName) {
        ArrayList<ArrayList<Cell>> board = initializeBoard(numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Cell cell = this.getBoardMap().get(username).get(i).get(j);
                if (cell.getCard() != null) {
                    Card card = cell.getCard();
                    if (card.getType() == CardType.TROOP && card.getOwner().equals(username)) {
                        int newRow = i - cell.getCard().getMovement();
                        if (newRow < 0) {
                            this.pointsMap.put(username, this.pointsMap.get(username) + card.getMight());
                        } else {
                            Cell cellAtNewPosition = this.getBoardMap().get(username).get(newRow).get(j);
                            if (cellAtNewPosition.getCard() != null) {
                                Card cardAtNewPosition = cell.getCard();
                                if (cardAtNewPosition.getOwner().equals(username)) {
                                    if (cardAtNewPosition.getType() == card.getType()) {
                                        cell.getCard().setMight(card.getMight() + cardAtNewPosition.getMight());
                                    } else {
                                        cell.getCard().setMight(card.getMight() + cardAtNewPosition.getMight());
                                        cell.getCard().setType(CardType.WALL);
                                    }
                                } else {
                                    int mightDiff = card.getMight() - cardAtNewPosition.getMight();
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
    private ArrayList<ArrayList<Cell>> initializeBoard(int numRows, int numCols) {
        ArrayList<ArrayList<Cell>> board = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            ArrayList<Cell> row = new ArrayList<>();
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
    private ArrayList<ArrayList<Cell>> transpose(ArrayList<ArrayList<Cell>> original) {
        ArrayList<ArrayList<Cell>> transposed = new ArrayList<>();
        for (int i = original.size() - 1; i >=0; i--) {
            transposed.add(original.get(i));
        }
        return transposed;
    }

    /*-------------------------------------------------------------------------
        GETTERS / SETTERS
     -------------------------------------------------------------------------*/

    public String getId() {
        return id;
    }

    public Game setId(String id) {
        this.id = id;
        return this;
    }

    public String getPlayer1() {
        return player1;
    }

    public Game setPlayer1(String player1) {
        this.player1 = player1;
        return this;
    }

    public String getPlayer2() {
        return player2;
    }

    public Game setPlayer2(String player2) {
        this.player2 = player2;
        return this;
    }

    public boolean isPlayer1sTurn() {
        return player1sTurn;
    }

    public Game setPlayer1sTurn(boolean player1sTurn) {
        this.player1sTurn = player1sTurn;
        return this;
    }

    public Map<String, ArrayList<ArrayList<Cell>>> getBoardMap() {
        return boardMap;
    }

    public Game setBoardMap(Map<String, ArrayList<ArrayList<Cell>>> boardMap) {
        this.boardMap = boardMap;
        return this;
    }

    public Map<String, ArrayList<ArrayList<Cell>>> getPreviousBoardMap() {
        return previousBoardMap;
    }

    public Game setPreviousBoardMap(Map<String, ArrayList<ArrayList<Cell>>> previousBoardMap) {
        this.previousBoardMap = previousBoardMap;
        return this;
    }

    public Map<String, Integer> getPointsMap() {
        return pointsMap;
    }

    public Game setPointsMap(Map<String, Integer> pointsMap) {
        this.pointsMap = pointsMap;
        return this;
    }

    public Map<String, Integer> getEnergyMap() {
        return energyMap;
    }

    public Game setEnergyMap(Map<String, Integer> energyMap) {
        this.energyMap = energyMap;
        return this;
    }

    public Map<String, ArrayList<Card>> getCardsMap() {
        return cardsMap;
    }

    public Game setCardsMap(Map<String, ArrayList<Card>> cardsMap) {
        this.cardsMap = cardsMap;
        return this;
    }

    public Map<String, Integer> getNumTurnsMap() {
        return numTurnsMap;
    }

    public Game setNumTurnsMap(Map<String, Integer> numTurnsMap) {
        this.numTurnsMap = numTurnsMap;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Game setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Date getPlayer2JoinTime() {
        return player2JoinTime;
    }

    public Game setPlayer2JoinTime(Date player2JoinTime) {
        this.player2JoinTime = player2JoinTime;
        return this;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public Game setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
        return this;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Game setStatus(GameStatus status) {
        this.status = status;
        return this;
    }

    public int getNumRows() {
        return numRows;
    }

    public Game setNumRows(int numRows) {
        this.numRows = numRows;
        return this;
    }

    public int getNumCols() {
        return numCols;
    }

    public Game setNumCols(int numCols) {
        this.numCols = numCols;
        return this;
    }

    public int getNumCardsInHand() {
        return numCardsInHand;
    }

    public Game setNumCardsInHand(int numCardsInHand) {
        this.numCardsInHand = numCardsInHand;
        return this;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", player1sTurn=" + player1sTurn +
                ", boardMap=" + boardMap +
                ", previousBoardMap=" + previousBoardMap +
                ", pointsMap=" + pointsMap +
                ", energyMap=" + energyMap +
                ", cardsMap=" + cardsMap +
                ", numTurnsMap=" + numTurnsMap +
                ", createdDate=" + createdDate +
                ", player2JoinTime=" + player2JoinTime +
                ", completedDate=" + completedDate +
                ", status=" + status +
                ", numRows=" + numRows +
                ", numCols=" + numCols +
                ", numCardsInHand=" + numCardsInHand +
                '}';
    }
}
