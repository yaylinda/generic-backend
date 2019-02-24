package yay.linda.genericbackend.service;

import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.CardType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CardGeneratorUtil {

    public static Card generateCard(String username) {
        CardType cardType = randomizeCardType();
        int might = randomizeMightStat();
        int move = randomizeMoveStat(cardType);
        return Card.builder()
                .id(UUID.randomUUID().toString())
                .type(cardType)
                .owner(username)
                .might(might)
                .movement(move)
                .cost(calculateCostStat(might, move))
                .numTurnsOnBoard(0)
                .build(); // TODO v2 implement special ability
    }

    public static List<Card> generateCards(String username, int numCards) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCards; i ++) {
            cards.add(generateCard(username));
        }
        return cards;
    }

    private static CardType randomizeCardType() {
        Random random = new Random();
        int randomInt = random.nextInt(100);
        return randomInt <= 25 ? CardType.DEFENSE : CardType.OFFENSE;
    }

    private static int randomizeMightStat() {
        return getRandomNumberInRange(1, 10);
    }

    private static int randomizeMoveStat(CardType cardType) {
        int movement = 0;
        if (cardType.equals(CardType.OFFENSE)) {
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
