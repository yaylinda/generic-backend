package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvancedGameConfigurationDTO {
    private Map<CardType, Double> dropRates;
    private Integer maxCardsPerCell;

    public static Map<CardType, Double> DEFAULT_DROP_RATES() {
        Map<CardType, Double> dropRates = new HashMap<>();
        dropRates.put(CardType.WALL, 20.0);
        dropRates.put(CardType.DEFENSE, 30.0);
        dropRates.put(CardType.TROOP, 50.0);
        return dropRates;
    }
}
