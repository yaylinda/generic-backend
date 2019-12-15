package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Card implements Comparable<Card> {

    /*
        Ideas for future cards:
        - spell card to reverse movement
        - spell card to take away movement
     */

    protected String id;
    protected CardType type;
    protected Integer might;
    protected Integer movement;
    protected MovementAxis movementAxis;
    protected MovementDirection movementDirection;
    protected Double cost;
    protected String owner;
    protected Integer numTurnsOnBoard;
    protected Boolean shouldAdvance;
    protected Boolean clicked;

    private Card() {
        this.shouldAdvance = true;
        this.clicked = false;
    }

    public static Card generateCard(String username, Map<CardType, Double> cardDropRates) {
        double rand = Math.random();
        if (rand >= 0 && rand < cardDropRates.get(CardType.WALL)) {
            return createWallCard(username);
        } else if (rand >= cardDropRates.get(CardType.WALL) && rand < cardDropRates.get(CardType.WALL) + cardDropRates.get(CardType.DEFENSE)) {
            return createHorizontalTroopCard(username);
        } else {
            return createVerticalTroopCard(username);
        }
    }

    public static Card createVerticalTroopCard(String username) {
        Card card = new Card();
        card.id = UUID.randomUUID().toString();
        card.might = determineMightStat();
        card.owner = username;
        card.numTurnsOnBoard = 0;
        card.type = CardType.TROOP;
        card.movement = 1;
        card.movementAxis = MovementAxis.VERTICAL;
        card.movementDirection = MovementDirection.FORWARDS;
        card.cost = calculateCostStat(card.might, card.movement);
        return card;
    }

    public static Card createWallCard(String username) {
        Card card = new Card();
        card.id = UUID.randomUUID().toString();
        card.might = determineMightStat();
        card.owner = username;
        card.numTurnsOnBoard = 0;
        card.type = CardType.WALL;
        card.cost = card.might / 2.0;
        card.movement = 0;
        card.movementAxis = MovementAxis.NONE;
        card.movementDirection = MovementDirection.NONE;
        return card;
    }

    public static Card createHorizontalTroopCard(String username) {
        Card card = new Card();
        card.id = UUID.randomUUID().toString();
        card.might = determineMightStat();
        card.owner = username;
        card.numTurnsOnBoard = 0;
        card.type = CardType.DEFENSE;
        card.movement = 1;
        card.movementAxis = MovementAxis.HORIZONTAL;
        card.movementDirection = randomizeDirection();
        card.cost = calculateCostStat(card.might, card.movement);
        return card;
    }

    public static List<Card> generateCards(String username, Integer num, Map<CardType, Double> cardDropRates) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            cards.add(generateCard(username, cardDropRates));
        }
        return cards;
    }

    private static int determineMightStat() {
        Random r = new Random();
        return r.nextInt(11) + 1;
    }

    private static double calculateCostStat(int might, int move) {
        return (might + move) / 2.0;
    }

    private static MovementDirection randomizeDirection() {
        Random r = new Random();
        return r.nextInt(2) == 1 ? MovementDirection.RIGHT : MovementDirection.LEFT;
    }

    public void incrementNumTurnsOnBoard(String username) {
        if (this.owner.equals(username)) {
            this.numTurnsOnBoard += 1;
        }
    }

    public boolean isQualifiedToAdvance(String username) {
        return this.getType() != CardType.WALL
                && this.owner.equals(username)
                && this.getNumTurnsOnBoard() > 0
                && this.shouldAdvance;
    }

    @Override
    public int compareTo(Card o) {
        // order is TROOPS first, then WALLS. if same type, compare might; smaller might first
        if (this.type == CardType.TROOP && o.getType() == CardType.WALL) {
            return -1;
        } else if (this.type == CardType.WALL && o.getType() == CardType.TROOP) {
            return 1;
        } else {
            if (this.might > o.getMight()) {
                return 1;
            } else if (this.might < o.getMight()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
