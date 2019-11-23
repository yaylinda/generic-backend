package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutCardRequest {
    private Integer row;
    private Integer col;
    private Integer cardIndex;
    private Card card;
}
