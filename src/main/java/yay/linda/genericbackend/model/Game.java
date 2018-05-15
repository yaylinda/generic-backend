package yay.linda.genericbackend.model;

import org.springframework.data.annotation.Id;

import java.util.*;

public class Game {

    @Id
    private String id;
    private String player1;
    private String player2;
    private String currentTurn;
    private Map<String, List<List<Cell>>> gameBoardMap;
    private Map<String, List<List<Cell>>> previousGameBoardMap;
    private Map<String, Integer> pointsMap;
    private Map<String, Integer> energyMap;
    private Date createdDate;
    private Date player2JoinTime;
    private Date completedDate;
    private GameStatus status;
    private int numRows;
    private int numCols;

    public Game() {
        this.gameBoardMap = new HashMap<>();
        this.pointsMap = new HashMap<>();
        this.energyMap = new HashMap<>();
        this.createdDate = new Date();
    }

    /**
     *
     * @param player1
     * @param numRows
     * @param numCols
     */
    public void createGameForPlayer1(String player1, int numRows, int numCols) {
        this.player1 = player1;
        this.currentTurn = player1;
        this.gameBoardMap.put(player1, this.initializeGameBoard());
        this.previousGameBoardMap.put(player1, this.initializeGameBoard());
        this.pointsMap.put(player1, 0);
        this.energyMap.put(player1, 1);
        this.createdDate = new Date();
        this.status = GameStatus.WAITING_PLAYER_2;
        this.numRows = numRows;
        this.numCols = numCols;
    }

    /**
     *
     * @param player2
     */
    public void addPlayer2ToGame(String player2) {
        this.player2 = player2;
        this.gameBoardMap.put(player2, this.transpose(this.gameBoardMap.get(this.player1)));
        this.previousGameBoardMap.put(player2, this.initializeGameBoard());
        this.pointsMap.put(player2, 0);
        this.energyMap.put(player2, 2);
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
        this.previousGameBoardMap.put(username, new ArrayList<>(this.gameBoardMap.get(username)));
        this.gameBoardMap.get(username).get(row).get(col).setCard(card);
        this.gameBoardMap.get(username).get(row).get(col).setState(CellState.OCCUPIED);
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    /**
     *
     * @param username
     */
    public void incrementEnergy(String username) {
        this.getEnergyMap().put(username, this.energyMap.get(username) + 1);
    }

    /**
     *
     * @return
     */
    private List<List<Cell>> initializeGameBoard() {
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
        // TODO - implement transpose method
        return original;
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

    public String getCurrentTurn() {
        return currentTurn;
    }

    public Game setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
        return this;
    }

    public Map<String, List<List<Cell>>> getGameBoardMap() {
        return gameBoardMap;
    }

    public Game setGameBoardMap(Map<String, List<List<Cell>>> gameBoardMap) {
        this.gameBoardMap = gameBoardMap;
        return this;
    }

    public Map<String, List<List<Cell>>> getPreviousGameBoardMap() {
        return previousGameBoardMap;
    }

    public Game setPreviousGameBoardMap(Map<String, List<List<Cell>>> previousGameBoardMap) {
        this.previousGameBoardMap = previousGameBoardMap;
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

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", currentTurn='" + currentTurn + '\'' +
                ", gameBoardMap=" + gameBoardMap +
                ", previousGameBoardMap=" + previousGameBoardMap +
                ", pointsMap=" + pointsMap +
                ", energyMap=" + energyMap +
                ", createdDate=" + createdDate +
                ", player2JoinTime=" + player2JoinTime +
                ", completedDate=" + completedDate +
                ", status=" + status +
                ", numRows=" + numRows +
                ", numCols=" + numCols +
                '}';
    }
}
