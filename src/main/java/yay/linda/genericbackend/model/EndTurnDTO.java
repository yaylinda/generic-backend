package yay.linda.genericbackend.model;

public class EndTurnDTO {

    private String gameId;
    private String username;
    private String opponentName;
    private Move move;

    public EndTurnDTO() {
    }

    public String getGameId() {
        return gameId;
    }

    public EndTurnDTO setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public EndTurnDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public EndTurnDTO setOpponentName(String opponentName) {
        this.opponentName = opponentName;
        return this;
    }

    public Move getMove() {
        return move;
    }

    public EndTurnDTO setMove(Move move) {
        this.move = move;
        return this;
    }
}
