package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class GameStats {
    private Integer numTurns;
    private Integer totalCardsPlayed;
    private Integer totalElixirUsed;
    private Integer totalElixirWasted;
    private Integer totalMightUsed;

    public GameStats() {
        this.numTurns = 0;
        this.totalCardsPlayed = 0;
        this.totalElixirUsed = 0;
        this.totalElixirWasted = 0;
        this.totalMightUsed = 0;
    }

    public void incrementNumTurns() {
        this.numTurns += 1;
    }

    public void incrementNumCardsPlayed() {
        this.totalCardsPlayed += 1;
    }
}
