package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.model.*;
import yay.linda.genericbackend.repository.GameRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepository;

    public List<GameDTO> getGamesByUsername(String username) {

        List<GameDTO> gameDTOs = new ArrayList<>();

        List<Game> games1 = gameRepository.findGamesByPlayer1(username);
        gameDTOs.addAll(games1.stream().map(g ->
                new GameDTO(g, true))
                .collect(Collectors.toList()));

        List<Game> games2 = gameRepository.findGamesByPlayer2(username);
        gameDTOs.addAll(games2.stream().map(g ->
                new GameDTO(g, false))
                .collect(Collectors.toList()));

        LOGGER.info("{} has {} active games", username, gameDTOs.size());
        return gameDTOs;
    }

    public GameDTO startGame(String username) {

        List<Game> waitingGames = gameRepository.findGamesByStatusOrderByCreatedDate(GameStatus.WAITING_PLAYER_2.name());
        waitingGames = waitingGames.stream().filter(g -> !g.getPlayer1().equals(username)).collect(Collectors.toList());

        Game newGame;
        boolean isPlayer1;

        if (waitingGames.size() == 0) {
            newGame = new Game().createGameForPlayer1(username);
            isPlayer1 = true;
            LOGGER.info("No waiting games... created new one: {}", newGame);
        } else {
            newGame = waitingGames.get(0).addPlayer2ToGame(username);
            isPlayer1 = false;
            LOGGER.info("Found waiting game... joined: {}", newGame);
        }

        gameRepository.save(newGame);
        return new GameDTO(newGame, isPlayer1);
    }

    public GameDTO endTurn(String gameId, String username) {

        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            LOGGER.info("Got game: {}", game);
            boolean isPlayer1;
            String opponentName;
            if (game.getPlayer1().equals(username)) {
                isPlayer1 = true;
                opponentName = game.getPlayer2();
            } else {
                isPlayer1 = false;
                opponentName = game.getPlayer1();
            }
            game.setCurrentTurn(opponentName);
            game.incrementEnergy(opponentName);
            // TODO - update gameboard for opponent
            gameRepository.save(game);
            return new GameDTO(game, isPlayer1);
        } else {
            LOGGER.warn("Could not find game matching gameId={}, username={}", gameId, username);
            return null;
        }
    }
}
