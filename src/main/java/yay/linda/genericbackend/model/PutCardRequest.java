package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class PutCardRequest {
    private int row;
    private int col;
    private int cardIndex;
    private Card card;
}
