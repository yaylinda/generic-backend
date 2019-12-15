package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameConfiguration {

    private Map<CardType, Double> dropRates;
    private Integer maxCardsPerCell;
    private Integer pointsToWin;
    private Integer numRows;
    private Integer numCols;
    private Integer numCardsInHand;
    private Integer numTerritoryRows;
    private Integer minTerritoryRow;
    private Double maxEnergy;
    private Double energyGrowthRate;
    private Double startingEnergy;
    private Boolean resetEnergyPerTurn;
    private Boolean isAdvanced;

    public static GameConfiguration DEFAULT() {

        Map<CardType, Double> dropRates = new HashMap<>();
        dropRates.put(CardType.WALL, 0.2);
        dropRates.put(CardType.DEFENSE, 0.3);
        dropRates.put(CardType.TROOP, 0.5);

        return new GameConfigurationBuilder()
                .dropRates(dropRates)
                .maxCardsPerCell(1)
                .pointsToWin(2)
                .numRows(5)
                .numCols(4)
                .numCardsInHand(4)
                .numTerritoryRows(2)
                .minTerritoryRow(3)
                .maxEnergy(10.0)
                .energyGrowthRate(1.0)
                .startingEnergy(1.0)
                .isAdvanced(false)
                .resetEnergyPerTurn(true)
                .build();
    }
}
