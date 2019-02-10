package yay.linda.genericbackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Card implements Comparable<Card> {

    private String id;
    private CardType type;
    private Integer might;
    private Integer movement;
    private Double cost;
    private String owner;
    private String specialAbility; // TODO v2
    private Integer numTurnsOnBoard;

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
}
