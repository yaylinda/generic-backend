package yay.linda.genericbackend.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardTest {

    @Test
    public void testGenerateCard() {
        Map<CardType, Double> dropRates = new HashMap<>();
        dropRates.put(CardType.WALL, 0.25);
        dropRates.put(CardType.DEFENSE, 0.25);
        dropRates.put(CardType.TROOP, 0.5);

        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            cards.add(Card.generateCard("test", dropRates));
        }

        int troopCount = 0;
        int defenseCount = 0;
        int wallCount = 0;

        for (Card card : cards) {
            switch (card.type) {
                case TROOP:
                    troopCount += 1;
                    break;
                case DEFENSE:
                    defenseCount += 1;
                    break;
                case WALL:
                    wallCount += 1;
                    break;
            }
        }

        System.out.println(troopCount * 1.0 / 1000);
        System.out.println(defenseCount * 1.0 / 1000);
        System.out.println(wallCount * 1.0 / 1000);
    }
}
