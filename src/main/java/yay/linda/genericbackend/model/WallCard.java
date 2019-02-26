package yay.linda.genericbackend.model;

public class WallCard extends Card {

    public WallCard(String username) {
        super(username);
        this.type = CardType.WALL;
        this.cost = this.might / 2.0;
        this.movement = 0;
        this.movementAxis = MovementAxis.NONE;
        this.movementDirection = MovementDirection.NONE;
    }
}
