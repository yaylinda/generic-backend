package yay.linda.genericbackend.service;

import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.CardType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardGeneratorUtil {

    public static Card generateCard(String username) {
        CardType cardType = randomizeCardType();
        int might = randomizeMightStat();
        int move = randomizeMoveStat(cardType);
        return new Card()
                .setType(cardType)
                .setOwner(username)
                .setMight(might)
                .setMovement(move)
                .setCost(calculateCostStat(might, move))
                .setSpecialAbility("TBD"); // TODO v2 implement special ability
    }

    public static List<Card> generateCards(String username, int numCards) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCards; i ++) {
            cards.add(generateCard(username));
        }
        return cards;
    }

    private static CardType randomizeCardType() {
        int index = getRandomNumberInRange(0, CardType.values().length - 1);
        return CardType.values()[index];
    }

    private static int randomizeMightStat() {
        return getRandomNumberInRange(1, 10);
    }

    private static int randomizeMoveStat(CardType cardType) {
        int movement = 0;
        if (cardType.equals(CardType.TROOP)) {
            movement = 1;
        }
        return movement;
    }

    private static double calculateCostStat(int might, int move) {
        // TODO v2: add special ability chance to card, if card has special ability, round the cost up
        return (might + move * 1.00) / 2;
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
