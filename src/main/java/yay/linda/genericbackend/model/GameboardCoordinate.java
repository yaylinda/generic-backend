package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameboardCoordinate {
    private int row;
    private int col;
    private double placeTroopThreat;
    private double placeWallThreat;
    private double combinedThreat;
}
