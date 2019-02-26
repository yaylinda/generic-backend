package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

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

    protected Card(String username) {
        this.id = UUID.randomUUID().toString();
        this.might = determineMightStat();
        this.owner = username;
        this.numTurnsOnBoard = 0;
    }

    public static Card generateCard(String username) {
        Random rand = new Random();
        int r = rand.nextInt(11) + 1;
        if (r == 1 || r == 2) {
            return new WallCard(username);
        } else if (r == 3 || r == 4 || r == 5) {
            return new HorizontalTroopCard(username);
        } else {
            return new VerticalTroopCard(username);
        }
    }

    public static List<Card> generateCards(String username, Integer num) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < num; i++ ) {
            cards.add(generateCard(username));
        }
        return cards;
    }

    public void incrementNumTurnsOnBoard(String username) {
        if (this.owner.equals(username)) {
            this.numTurnsOnBoard += 1;
        }
    }

    public boolean isQualifiedToAdvance(String username) {
        return this.getType() == CardType.TROOP
                && this.owner.equals(username)
                && this.getNumTurnsOnBoard() > 0;
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

    protected static int determineMightStat() {
        Random r = new Random();
        return r.nextInt(11) + 1;
    }

    protected static double calculateCostStat(int might, int move) {
        return (might + move) / 2.0;
    }
}
