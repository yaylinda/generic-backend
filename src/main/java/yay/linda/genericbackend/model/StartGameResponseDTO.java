package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameResponseDTO {
    private List<GameDTO> games;
    private GameDTO newGame;
}
