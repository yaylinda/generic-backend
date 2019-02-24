package yay.linda.genericbackend.service;

import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.CardType;
import yay.linda.genericbackend.model.MovementAxis;
import yay.linda.genericbackend.model.MovementDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CardGeneratorUtil {

    public static Card generateCard(String username) {
        CardType cardType = determineCardType();
        int might = determineMightStat();
        int move = determineMoveStat(cardType);
        MovementAxis axis = determineMovementAxis(cardType);
        MovementDirection direction = determineMovementDirection(cardType, axis);
        return Card.builder()
                .id(UUID.randomUUID().toString())
                .type(cardType)
                .owner(username)
                .might(might)
                .movement(move)
                .movementAxis(axis)
                .movementDirection(direction)
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

    private static CardType determineCardType() {
        int randomInt = getRandomNumberInRange(0, 100);
        return randomInt < 25 ? CardType.WALL : CardType.TROOP;
    }

    private static int determineMightStat() {
        return getRandomNumberInRange(1, 10);
    }

    private static int determineMoveStat(CardType cardType) {
        return cardType.equals(CardType.TROOP) ? 1 : 0;
    }

    private static MovementAxis determineMovementAxis(CardType cardType) {
        // TODO - implement
        return MovementAxis.NONE;
    }

    private static MovementDirection determineMovementDirection(CardType cardType, MovementAxis movementAxis) {
        // TODO - implement
        return MovementDirection.NONE;
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
