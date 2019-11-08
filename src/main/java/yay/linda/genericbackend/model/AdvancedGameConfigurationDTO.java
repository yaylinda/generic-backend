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
        dropRates.put(CardType.WALL, 0.2);
        dropRates.put(CardType.DEFENSE, 0.3);
        dropRates.put(CardType.TROOP, 0.5);
        return dropRates;
    }

    public static Integer DEFAULT_MAX_CARDS_PER_CELL = 1;
}
