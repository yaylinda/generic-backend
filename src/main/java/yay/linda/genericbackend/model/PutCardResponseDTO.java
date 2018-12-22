package yay.linda.genericbackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutCardResponseDTO {
    private GameDTO game;
    private PutCardStatus status;
    private String message;
}
