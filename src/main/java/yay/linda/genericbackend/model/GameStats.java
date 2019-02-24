package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class GameStats {
    private Integer numTurns;
    private Integer totalCardsPlayed;
    private Integer totalEnergyUsed; // sum of energy used each turn
    private Integer totalEnergyWasted; // sum of leftover energy at end of turn
    private Integer totalMightUsed; // sum of might of cards placed on board
    private Integer totalAdvancementPoints; // sum of points gained from troop advancement

    public GameStats() {
        this.numTurns = 0;
        this.totalCardsPlayed = 0;
        this.totalEnergyUsed = 0;
        this.totalEnergyWasted = 0;
        this.totalMightUsed = 0;
        this.totalAdvancementPoints = 0;
    }

    public void incrementNumTurns() {
        this.numTurns += 1;
    }

    public void incrementNumCardsPlayed() {
        this.totalCardsPlayed += 1;
    }
}
