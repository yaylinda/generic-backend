package yay.linda.genericbackend.model;

public class VerticalTroopCard extends Card {

    public VerticalTroopCard(String username) {
        super(username);
        this.movement = 1;
        this.movementAxis = MovementAxis.VERTICAL;
        this.movementDirection = MovementDirection.FORWARDS;
        this.cost = calculateCostStat(this.might, this.movement);
    }
}
