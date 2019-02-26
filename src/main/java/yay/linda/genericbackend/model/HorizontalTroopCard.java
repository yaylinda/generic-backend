package yay.linda.genericbackend.model;

import java.util.Random;

public class HorizontalTroopCard extends Card {

    public HorizontalTroopCard(String username) {
        super(username);
        this.movement = 1;
        this.movementAxis = MovementAxis.HORIZONTAL;
        this.movementDirection = randomizeDirection();
        this.cost = calculateCostStat(this.might, this.movement);
    }

    private static MovementDirection randomizeDirection() {
        Random r = new Random();
        return r.nextInt(2) == 0 ? MovementDirection.LEFT : MovementDirection.RIGHT;
    }
}
