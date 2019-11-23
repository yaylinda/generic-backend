package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PutCardRequest {
    private Integer row;
    private Integer col;
    private Integer cardIndex;
    private Card card;
}
