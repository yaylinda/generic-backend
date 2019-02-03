package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yay.linda.genericbackend.config.GameProperties;
import yay.linda.genericbackend.model.*;
import yay.linda.genericbackend.repository.GameRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private GameProperties gameProperties;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<GameDTO> getGames(String sessionToken) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        List<Game> games1 = gameRepository.findGamesByPlayer1(username);
        List<GameDTO> gameDTOs = games1.stream()
                .map(g -> new GameDTO(g, true))
                .collect(Collectors.toList());

        List<Game> games2 = gameRepository.findGamesByPlayer2(username);
        gameDTOs.addAll(games2.stream()
                .map(g -> new GameDTO(g, false))
                .collect(Collectors.toList()));

        LOGGER.info("{} has {} active games", username, gameDTOs.size());
        return gameDTOs;
    }

    public GameDTO startGame(String sessionToken) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        List<Game> waitingGames = gameRepository.findGamesByStatusOrderByCreatedDate(GameStatus.WAITING_PLAYER_2.name());
        waitingGames = waitingGames.stream()
                .filter(g -> !g.getPlayer1().equals(username))
                .collect(Collectors.toList());

        Game newGame;
        boolean isPlayer1;

        if (waitingGames.size() == 0) {
            newGame = new Game(this.gameProperties.getNumRows(), this.gameProperties.getNumCols(), this.gameProperties.getNumCardsInHand());
            newGame.createGameForPlayer1(username);
            isPlayer1 = true;
            LOGGER.info("No waiting games... created new one: {}", newGame);
        } else {
            newGame = waitingGames.get(0);
            newGame.addPlayer2ToGame(username);
            isPlayer1 = false;
            this.messagingTemplate.convertAndSend("/topic/player2Joined/" + newGame.getPlayer1(), newGame.getId());
            LOGGER.info("Found waiting game... joined: {}", newGame);
        }

        gameRepository.save(newGame);
        return new GameDTO(newGame, isPlayer1);
    }

    public PutCardResponseDTO putCard(String sessionToken, String gameId, PutCardDTO putCardDTO) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        Game game = getGameById(gameId);
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

        PutCardStatus putCardStatus;
        String message = validatePutCardRequest(game, username, putCardDTO);

        if (StringUtils.isEmpty(message)) {
            LOGGER.info("PutCard request passed validation...");
            putCardStatus = PutCardStatus.SUCCESSFUL;

            game.putCardOnBoard(username, putCardDTO.getRow(), putCardDTO.getCol(), putCardDTO.getCard());
            game.getNumCardsPlayedMap().put(username, game.getNumCardsPlayedMap().get(username) + 1);
            game.getEnergyMap().put(username, game.getEnergyMap().get(username) - putCardDTO.getCard().getCost());
            if (game.getStatus() == GameStatus.IN_PROGRESS) {
                int opponentRow = (this.gameProperties.getNumRows() - 1) - putCardDTO.getRow();
                game.putCardOnBoard(opponentName, opponentRow, putCardDTO.getCol(), putCardDTO.getCard());
            }

            gameRepository.save(game);
            this.messagingTemplate.convertAndSend("/topic/opponentPutCard/" + opponentName, gameId);
        } else {
            LOGGER.info("PutCard request failed with message: '{}'", message);
            putCardStatus = PutCardStatus.INVALID;
        }

        return PutCardResponseDTO.builder()
                .game(new GameDTO(game, isPlayer1))
                .status(putCardStatus)
                .message(message)
                .build();
    }

    public GameDTO endTurn(String sessionToken, String gameId, boolean discardHand) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        Game game = getGameById(gameId);
        LOGGER.info("Got game: {}", game);

        boolean isPlayer1;
        String opponentName;

        if (game.getPlayer1().equals(username)) {
            isPlayer1 = true;
            opponentName = game.getPlayer2();
            game.setPlayer1sTurn(false);
        } else {
            isPlayer1 = false;
            opponentName = game.getPlayer1();
            game.setPlayer1sTurn(true);
        }

        if (discardHand) {
            List<Card> newCards = IntStream.range(0, game.getNumCardsInHand()).boxed()
                    .map(i -> drawCard(username, gameId, i))
                    .collect(Collectors.toList());

            LOGGER.info("Generated new cards for {}: {}", username, newCards);

            game.getCardsMap().put(username, newCards);
        }

        game.updatePreviousBoard(username);
        game.advanceTroops(username, opponentName);
        game.incrementNumTurns(username);
        game.incrementEnergy(username);

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            game.updatePreviousBoard(opponentName);
            game.advanceTroopsForOpponent(username, opponentName);
            this.messagingTemplate.convertAndSend("/topic/opponentEndedTurn/" + opponentName, gameId);
        }

        if (game.getPointsMap().get(username) >= gameProperties.getMaxPoints()) {
            game.setStatus(GameStatus.COMPLETED);
            game.setCompletedDate(new Date());
        }

        gameRepository.save(game);
        return new GameDTO(game, isPlayer1);
    }

    public GameDTO getGameById(String sessionToken, String gameId) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        Game game = getGameById(gameId);
        return new GameDTO(game, game.getPlayer1().equals(username));
    }

    public Card drawCard(String sessionToken, String gameId, int usedCardIndex) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        Card newCard = CardGeneratorUtil.generateCard(username);
        LOGGER.info("Generated new card: {}", newCard);

        Game game = getGameById(gameId);
        LOGGER.info("Got game: {}", game);

        game.getCardsMap().get(username).set(usedCardIndex, newCard);

        gameRepository.save(game);

        return newCard;
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    private Game getGameById(String gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            LOGGER.info("Got game: {}", game);
            return game;
        } else {
            LOGGER.warn("Could not find game matching gameId={}", gameId);
            return null;
        }
    }

    private String validatePutCardRequest(Game currentGame, String sessionToken, PutCardDTO request) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        // check enough energy
        if (currentGame.getEnergyMap().get(username) < request.getCard().getCost()) {
            return String.format(
                    "Not enough energy [%f] to place card with cost [%f]",
                    currentGame.getEnergyMap().get(username),
                    request.getCard().getCost());
        }
        // check row col is empty
        if (currentGame.getBoardMap().get(username).get(request.getRow()).get(request.getCol()).getState() == CellState.OCCUPIED) {
            return "Card must be placed in an empty Cell";
        }
        // check row is within limit
        if (request.getRow() < currentGame.getMinTerritoryRowNum()) {
            return "Card must be placed on your Territory";
        }

        return "";
    }

}
