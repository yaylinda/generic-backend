package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.model.AdvancedGameConfigurationDTO;
import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.Cell;
import yay.linda.genericbackend.model.Game;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.GameStatus;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.repository.GameRepository;

import java.util.List;
import java.util.Random;

@Service
public class GameAIPlayer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameAIPlayer.class);

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final Random random = new Random();

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    public GameDTO nextMove(String gameId, String realUsername) {

        String aiUsername = "SimpleWarAI_" + randomStringGenerator(6);

        LOGGER.info("{} will play in gameId={}, against real player: {}", aiUsername, gameId, realUsername);

        Game game = gameService.getGameById(gameId);

        // join game, if game is not in progress
        if (game.getStatus() == GameStatus.WAITING_PLAYER_2) {
            LOGGER.info("gameId={} is WAITING_PLAYER_2, AI is joining...");

            game.addPlayer2ToGame(aiUsername, AdvancedGameConfigurationDTO.DEFAULT_DROP_RATES()); // TODO handle advanced configs

            gameRepository.save(game);
            LOGGER.info("Updated gameId={} with player2={}", gameId, aiUsername);
        }

        // calculate put card request
        PutCardRequest putCardRequest = calculatePutCardRequest(
                game.getBoardMap().get(aiUsername),
                game.getCardsMap().get(aiUsername));

        // do put card
        PutCardResponse putCardResponse = gameService.putCardHelper(game, aiUsername, realUsername, false, putCardRequest);

        return putCardResponse.getGame();
    }

    /**
     *
     * @param gameboard
     * @param cards
     * @return
     */
    private PutCardRequest calculatePutCardRequest(List<List<Cell>> gameboard, List<Card> cards) {

        // TODO - implement

        return new PutCardRequest();
    }

    /**
     *
     * @param length
     * @return
     */
    private String randomStringGenerator(Integer length) {

        StringBuilder acc = new StringBuilder();

        for (int i = 0; i < length; i ++) {
            acc.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return acc.toString();
    }
}
