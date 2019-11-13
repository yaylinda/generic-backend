package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJoinGameResponseDTO {
    private GameDTO game;
    private String createOrJoin;
}
