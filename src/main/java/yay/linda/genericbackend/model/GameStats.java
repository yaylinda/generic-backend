package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class GameStats {
    private Integer numTurns;
    private Integer totalCardsPlayed;
    private Double totalEnergyUsed; // sum of energy used each turn
    private Integer totalMightPlaced; // sum of might of cards placed on board
    private Integer totalAdvancementPoints; // sum of points gained from troop advancement

    public GameStats() {
        this.numTurns = 0;
        this.totalCardsPlayed = 0;
        this.totalEnergyUsed = 0.0;
        this.totalMightPlaced = 0;
        this.totalAdvancementPoints = 0;
    }

    public void incrementNumTurns() {
        this.numTurns += 1;
    }

    public void incrementNumCardsPlayed() {
        this.totalCardsPlayed += 1;
    }

    public void incrementEnergyUsed(Double used) {
        this.totalEnergyUsed += used;
    }

    public void incrementMightPlaced(Integer might) {
        this.totalMightPlaced += might;
    }

    public void incrementAdvancementPoints(Integer advancementPoints) {
        this.totalAdvancementPoints += advancementPoints;
    }
}
