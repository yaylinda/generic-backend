package yay.linda.genericbackend.model;

import java.util.ArrayList;

public class GameDTO {

    private String id;
    private String username;
    private String opponentName;
    private ArrayList<ArrayList<Cell>> board;
    private ArrayList<ArrayList<Cell>> previousBoard;
    private ArrayList<Card> cards;
    private boolean currentTurn;
    private int points;
    private double energy;
    private GameStatus status;
    private int numTurns;
    private int opponentPoints;
    private int numCardsPlayed;

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
        if (this.opponentName != null) {
            this.opponentPoints = game.getPointsMap().get(this.opponentName);
        }
    }

    private boolean calculateCurrentTurn(boolean isPlayer1, boolean isPlayer1sTurn) {
        if (isPlayer1) {
            return isPlayer1sTurn;
        } else {
            return !isPlayer1sTurn;
        }
    }

    public String getId() {
        return id;
    }

    public GameDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public GameDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public GameDTO setOpponentName(String opponentName) {
        this.opponentName = opponentName;
        return this;
    }

    public ArrayList<ArrayList<Cell>> getBoard() {
        return board;
    }

    public GameDTO setBoard(ArrayList<ArrayList<Cell>> board) {
        this.board = board;
        return this;
    }

    public ArrayList<ArrayList<Cell>> getPreviousBoard() {
        return previousBoard;
    }

    public GameDTO setPreviousBoard(ArrayList<ArrayList<Cell>> previousBoard) {
        this.previousBoard = previousBoard;
        return this;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public GameDTO setCards(ArrayList<Card> cards) {
        this.cards = cards;
        return this;
    }

    public boolean isCurrentTurn() {
        return currentTurn;
    }

    public GameDTO setCurrentTurn(boolean currentTurn) {
        this.currentTurn = currentTurn;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public GameDTO setPoints(int points) {
        this.points = points;
        return this;
    }

    public double getEnergy() {
        return energy;
    }

    public GameDTO setEnergy(double energy) {
        this.energy = energy;
        return this;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameDTO setStatus(GameStatus status) {
        this.status = status;
        return this;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public GameDTO setNumTurns(int numTurns) {
        this.numTurns = numTurns;
        return this;
    }

    public int getOpponentPoints() {
        return opponentPoints;
    }

    public GameDTO setOpponentPoints(int opponentPoints) {
        this.opponentPoints = opponentPoints;
        return this;
    }

    public int getNumCardsPlayed() {
        return numCardsPlayed;
    }

    public GameDTO setNumCardsPlayed(int numCardsPlayed) {
        this.numCardsPlayed = numCardsPlayed;
        return this;
    }

    @Override
    public String toString() {
        return "GameDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", opponentName='" + opponentName + '\'' +
                ", board=" + board +
                ", previousBoard=" + previousBoard +
                ", cards=" + cards +
                ", currentTurn=" + currentTurn +
                ", points=" + points +
                ", energy=" + energy +
                ", status=" + status +
                ", numTurns=" + numTurns +
                ", opponentPoints=" + opponentPoints +
                ", numCardsPlayed=" + numCardsPlayed +
                '}';
    }
}
