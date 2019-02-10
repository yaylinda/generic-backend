package yay.linda.genericbackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Card {

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
}
