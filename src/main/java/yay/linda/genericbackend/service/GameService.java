package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.config.GameProperties;
import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.Game;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.GameStatus;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.model.PutCardStatus;
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
        LOGGER.info("Obtained username={} from sessionToken", username);

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

    public GameDTO getGameById(String sessionToken, String gameId) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        Game game = getGameById(gameId);
        return new GameDTO(game, game.getPlayer1().equals(username));
    }

    public List<GameDTO> getJoinableGames(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        return getWaitingGames(username).stream()
                .map(GameDTO::gameDTOForJoinableList)
                .collect(Collectors.toList());
    }

    public GameDTO joinGame(String sessionToken, String gameId) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        Game gameToJoin;
        if (StringUtils.isEmpty(gameId)) {
            List<Game> waitingGames = getWaitingGames(username);
            if (waitingGames.isEmpty()) {
                throw new NotFoundException("There are no waiting games to join");
            }
            gameToJoin = waitingGames.get(0);
        } else {
            gameToJoin = getGameById(gameId);
        }

        gameToJoin.addPlayer2ToGame(username);

        this.messagingTemplate.convertAndSend("/topic/player2Joined/" + gameToJoin.getPlayer1(), gameToJoin.getId());

        gameRepository.save(gameToJoin);
        return new GameDTO(gameToJoin, false);
    }

    public GameDTO createGame(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        Game newGame = new Game(this.gameProperties.getNumRows(), this.gameProperties.getNumCols(), this.gameProperties.getNumCardsInHand());

        newGame.createGameForPlayer1(username);

        this.messagingTemplate.convertAndSend("/topic/gameCreated", username);

        gameRepository.save(newGame);
        return new GameDTO(newGame, true);
    }

    public PutCardResponse putCard(String sessionToken, String gameId, PutCardRequest putCardRequest) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        Game game = getGameById(gameId);
        game.setLastModifiedDate(new Date());
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
        String message = validatePutCardRequest(game, username, putCardRequest);

        if (StringUtils.isEmpty(message)) {
            LOGGER.info("PutCard request passed validation...");
            putCardStatus = PutCardStatus.SUCCESSFUL;

            game.putCardOnBoard(username, putCardRequest.getRow(), putCardRequest.getCol(), putCardRequest.getCard());
            game.incrementNumCardsPlayed(username);
            game.decrementEnergyForPutCard(username, putCardRequest.getCard().getCost());
            if (game.getStatus() == GameStatus.IN_PROGRESS) {
                int opponentRow = (this.gameProperties.getNumRows() - 1) - putCardRequest.getRow();
                game.putCardOnBoard(opponentName, opponentRow, putCardRequest.getCol(), putCardRequest.getCard());
            }

            drawCard(username, game, putCardRequest.getCardIndex());

            gameRepository.save(game);
            this.messagingTemplate.convertAndSend("/topic/opponentPutCard/" + opponentName, gameId);
        } else {
            LOGGER.info("PutCard request failed with message: '{}'", message);
            putCardStatus = PutCardStatus.INVALID;
        }

        return PutCardResponse.builder()
                .game(new GameDTO(game, isPlayer1))
                .status(putCardStatus)
                .message(message)
                .build();
    }

    public GameDTO endTurn(String sessionToken, String gameId, boolean discardHand) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        Game game = getGameById(gameId);
        game.setLastModifiedDate(new Date());
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
            IntStream.range(0, game.getNumCardsInHand()).boxed()
                    .forEach(i -> drawCard(username, game, i));
        }

        game.updatePreviousBoard(username);
        game.updateTransitionalBoard(username);
        game.updateCurrentBoard(username, opponentName);
        game.incrementNumTurns(username);
        game.incrementEnergyForEndTurn(username);

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            game.updatePreviousBoard(opponentName);
            game.advanceTroopsForOpponent(username, opponentName);
            this.messagingTemplate.convertAndSend("/topic/opponentEndedTurn/" + opponentName, gameId);
        }

        if (game.getPointsMap().get(username) >= gameProperties.getMaxPoints()) {
            game.setStatus(GameStatus.COMPLETED);
            game.setCompletedDate(new Date());
            game.setWinner(username);
        }

        gameRepository.save(game);
        return new GameDTO(game, isPlayer1);
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    private Game getGameById(String gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            throw NotFoundException.gameNotFound(gameId);
        }
        return optionalGame.get();
    }

    private List<Game> getWaitingGames(String username) {
        List<Game> waitingGames = gameRepository.findGamesByStatusOrderByCreatedDate(GameStatus.WAITING_PLAYER_2.name())
                .stream()
                .filter(g -> !g.getPlayer1().equals(username))
                .collect(Collectors.toList());
        LOGGER.info("Obtained {} waiting games (not created by {})", waitingGames.size(), username);
        return waitingGames;
    }

    private String validatePutCardRequest(Game currentGame, String username, PutCardRequest request) {
        // check enough energy
        if (currentGame.getEnergyMap().get(username) < request.getCard().getCost()) {
            return String.format(
                    "Not enough energy [%.1f] to place card with cost [%.1f]",
                    currentGame.getEnergyMap().get(username),
                    request.getCard().getCost());
        }
        // check row col is empty
        if (!currentGame.getBoardMap().get(username).get(request.getRow()).get(request.getCol()).isAvailable()) {
            return "Card must be placed in an empty Cell";
        }
        // check row is within limit
        if (request.getRow() < currentGame.getMinTerritoryRowNum()) {
            return "Card must be placed on your Territory";
        }

        return "";
    }

    private void drawCard(String username, Game game, int cardIndex) {
        Card newCard = CardGeneratorUtil.generateCard(username);
        LOGGER.info("Generated new card at index={}: {}", cardIndex, newCard);
        game.getCardsMap().get(username).set(cardIndex, newCard);
    }
}
