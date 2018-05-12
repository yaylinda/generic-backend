package yay.linda.genericbackend.model;

import java.util.List;

public class StartGameResponseDTO {

    private List<GameDTO> games;
    private GameDTO newGame;

    public List<GameDTO> getGames() {
        return games;
    }

    public StartGameResponseDTO setGames(List<GameDTO> games) {
        this.games = games;
        return this;
    }

    public GameDTO getNewGame() {
        return newGame;
    }

    public StartGameResponseDTO setNewGame(GameDTO newGame) {
        this.newGame = newGame;
        return this;
    }
}
