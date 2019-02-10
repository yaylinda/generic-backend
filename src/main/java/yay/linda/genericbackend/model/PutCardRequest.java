package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class PutCardRequest {
    private Integer row;
    private Integer col;
    private Integer cardIndex;
    private Card card;
}
