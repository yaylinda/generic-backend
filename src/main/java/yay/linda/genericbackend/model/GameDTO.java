package yay.linda.genericbackend.model;

public class GameDTO {

    private String username;
    private String opponentName;
    private GameBoard gameBoard;
    private boolean currentTurn;
    private int points;
    private int energy;

    public GameDTO(Game game, boolean isPlayer1) {
        this.username = isPlayer1 ? game.getPlayer1() : game.getPlayer2();
        this.opponentName = isPlayer1 ? game.getPlayer2() : game.getPlayer1();
        this.gameBoard = game.getGameBoardMap().get(username);
        this.currentTurn = game.getCurrentTurn().equals(this.username);
        this.points = game.getPointsMap().get(username);
        this.energy = game.getEnergyMap().get(username);
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

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public GameDTO setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
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

    public int getEnergy() {
        return energy;
    }

    public GameDTO setEnergy(int energy) {
        this.energy = energy;
        return this;
    }
}
