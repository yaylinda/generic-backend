package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yay.linda.genericbackend.api.error.AdvGameConfigException;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.config.GameProperties;
import yay.linda.genericbackend.model.AdvancedGameConfigurationDTO;
import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.CreateJoinGameResponseDTO;
import yay.linda.genericbackend.model.Game;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.GameStatus;
import yay.linda.genericbackend.model.InviteToGameDTO;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.model.PutCardStatus;
import yay.linda.genericbackend.model.UserActivity;
import yay.linda.genericbackend.repository.GameRepository;

import java.time.Instant;
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
    private UserService userService;

    @Autowired
    private GameProperties gameProperties;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Get one Game by gameId
     *
     * @param sessionToken
     * @param gameId
     * @return
     */
    public GameDTO getGameById(String sessionToken, String gameId) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.GET_GAME_BY_ID);

        Game game = getGameById(gameId);
        return new GameDTO(game, game.getPlayer1().equals(username));
    }

    /**
     * Get list of games that a user is part of
     *
     * @param sessionToken
     * @return
     */
    public List<GameDTO> getGames(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        List<Game> games1 = gameRepository.findGamesByPlayer1(username);
        List<GameDTO> gameDTOs = games1.stream()
                .map(g -> new GameDTO(g, true))
                .collect(Collectors.toList());

        LOGGER.info("Obtained {} games where {} is Player1", games1.size(), username);

        List<Game> games2 = gameRepository.findGamesByPlayer2(username);
        gameDTOs.addAll(games2.stream()
                .map(g -> new GameDTO(g, false))
                .collect(Collectors.toList()));

        LOGGER.info("Obtained {} games where {} is Player2", games2.size(), username);

        return gameDTOs;
    }

    /**
     * Get list of games that are waiting for player2
     *
     * @param sessionToken
     * @return
     */
    public List<GameDTO> getJoinableGames(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        return getWaitingGames(username).stream()
                .map(GameDTO::gameDTOForJoinableList)
                .collect(Collectors.toList());
    }

    /**
     * Create a new game (if no other games are available), or join an existing one
     *
     * @param sessionToken
     * @return
     */
    public CreateJoinGameResponseDTO createOrJoinGame(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);

        CreateJoinGameResponseDTO response = new CreateJoinGameResponseDTO();

        GameDTO gameDTO;

        List<Game> waitingGames = getWaitingGames(username);
        if (waitingGames.isEmpty()) {
            LOGGER.info("No available games for {} to join... creating...", username);
            gameDTO = createGame(sessionToken, false, new AdvancedGameConfigurationDTO());
            response.setCreateOrJoin("CREATE");
        } else {
            LOGGER.info("Found available game {} to join, gameId={}", username, waitingGames.get(0).getId());
            gameDTO = joinGame(sessionToken, waitingGames.get(0).getId());
            response.setCreateOrJoin("JOIN");
        }

        response.setGame(gameDTO);

        return response;
    }

    public GameDTO joinGame(String sessionToken, String gameId) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.JOIN_GAME);
        userService.incrementNumGames(username);

        Game gameToJoin = getGameById(gameId);

        gameToJoin.addPlayer2ToGame(username, gameToJoin.getUseAdvancedConfigs() ? gameToJoin.getAdvancedGameConfigs().getDropRates() : AdvancedGameConfigurationDTO.DEFAULT_DROP_RATES());

        gameRepository.save(gameToJoin);
        LOGGER.info("Found game with id={} for {} to join", gameId, username);
        this.messagingTemplate.convertAndSend("/topic/player2Joined/" + gameToJoin.getPlayer1(), gameToJoin.getId());

        return new GameDTO(gameToJoin, false);
    }

    public GameDTO createGame(String sessionToken, Boolean useAdvancedConfigs, AdvancedGameConfigurationDTO advancedGameConfigurationDTO) {

        if (useAdvancedConfigs) {
            this.validateAdvancedGameConfigurations(advancedGameConfigurationDTO);
        }

        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.CREATE_GAME);
        userService.incrementNumGames(username);

        Game newGame = new Game(
                this.gameProperties.getNumRows(),
                this.gameProperties.getNumCols(),
                this.gameProperties.getNumCardsInHand(),
                this.gameProperties.getNumTerritoryRows(),
                useAdvancedConfigs,
                advancedGameConfigurationDTO);

        newGame.createGameForPlayer1(username, useAdvancedConfigs ? advancedGameConfigurationDTO.getDropRates() : AdvancedGameConfigurationDTO.DEFAULT_DROP_RATES());

        gameRepository.save(newGame);
        LOGGER.info("Created new game for {} with gameId={}", username, newGame.getId());
        this.messagingTemplate.convertAndSend("/topic/gameCreated", username);

        return new GameDTO(newGame, true);
    }


    public GameDTO inviteToGame(String sessionToken, InviteToGameDTO inviteToGameDTO) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.INVITE_TO_GAME);

        Game newGame = new Game(
                this.gameProperties.getNumRows(),
                this.gameProperties.getNumCols(),
                this.gameProperties.getNumCardsInHand(),
                this.gameProperties.getNumTerritoryRows(),
                false,
                null);

        newGame.createGameForPlayer1(username, AdvancedGameConfigurationDTO.DEFAULT_DROP_RATES());
        newGame.addPlayer2ToGame(inviteToGameDTO.getPlayer2(), AdvancedGameConfigurationDTO.DEFAULT_DROP_RATES());

        gameRepository.save(newGame);

        this.messagingTemplate.convertAndSend("/topic/gameCreated", username);

        return new GameDTO(newGame, true);
    }

    public PutCardResponse putCard(String sessionToken, String gameId, PutCardRequest putCardRequest) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.PUT_CARD);

        Game game = getGameById(gameId);
        game.setLastModifiedDate(Date.from(Instant.now()));
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
        String message = validatePutCardRequest(game, username, isPlayer1, putCardRequest);

        if (StringUtils.isEmpty(message)) {
            LOGGER.info("PutCard request passed validation...");
            putCardStatus = PutCardStatus.SUCCESSFUL;

            game.putCardOnBoard(username, putCardRequest.getRow(), putCardRequest.getCol(), putCardRequest.getCard());
            game.incrementNumCardsPlayed(username);
            game.decrementEnergyForPutCard(username, putCardRequest.getCard().getCost());
            game.incrementEnergyUsed(username, putCardRequest.getCard().getCost());
            game.incrementMightPlaced(username, putCardRequest.getCard().getMight());

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
        userService.updateActivity(username, UserActivity.END_TURN);

        Game game = getGameById(gameId);
        game.setLastModifiedDate(Date.from(Instant.now()));
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

        game.updatePreviousBoard(username); // sets current board state to previous board state
        game.updateTransitionalBoard(username); // moves all "my" troops forward
        game.updateCurrentBoard(username, opponentName); // performs clash on cells with multiple cards

        game.incrementNumTurns(username);
        game.incrementEnergyForEndTurn(username);

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            game.updateOpponentBoard(username, opponentName);
            this.messagingTemplate.convertAndSend("/topic/opponentEndedTurn/" + opponentName, gameId);
        }

        if (game.getPointsMap().get(username) >= gameProperties.getMaxPoints()) {
            game.setStatus(GameStatus.COMPLETED);
            game.setCompletedDate(Date.from(Instant.now()));
            game.setWinner(username);
            userService.incrementNumWins(username);
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
                .filter(g -> !g.getPlayer1sTurn())
                .collect(Collectors.toList());
        LOGGER.info("Obtained {} waiting games (not created by {})", waitingGames.size(), username);
        return waitingGames;
    }

    private String validatePutCardRequest(Game currentGame, String username, boolean isPlayer1, PutCardRequest request) {
        // check current turn
        if (!GameDTO.calculateCurrentTurn(isPlayer1, currentGame.getPlayer1sTurn(), currentGame.getStatus())) {
            return "Cannot place card when it is not your turn";
        }
        // check enough energy
        if (currentGame.getEnergyMap().get(username) < request.getCard().getCost()) {
            return String.format(
                    "Not enough energy [%.1f] to place card with cost [%.1f]",
                    currentGame.getEnergyMap().get(username),
                    request.getCard().getCost());
        }
        // check row col does not have enemy
        if (!currentGame.getBoardMap().get(username).get(request.getRow()).get(request.getCol()).isFriendlyCell(username)) {
            return "Card must be placed in a friendly or empty Cell";
        }
        // check row is within limit
        if (request.getRow() < currentGame.getMinTerritoryRowNum()) {
            return "Card must be placed on your Territory";
        }
        //check not too many cards are in cell
        if (currentGame.getUseAdvancedConfigs() != null && currentGame.getUseAdvancedConfigs()) {
            if (currentGame.getBoardMap().get(username).get(request.getRow()).get(request.getCol()).getCards().size() >= currentGame.getAdvancedGameConfigs().getMaxCardsPerCell()) {
                return String.format("This cell is at maximum capacity ([%d] cards). Check Advanced Game Configurations.", currentGame.getAdvancedGameConfigs().getMaxCardsPerCell());
            }
        } else {
            if (currentGame.getBoardMap().get(username).get(request.getRow()).get(request.getCol()).getCards().size() >= AdvancedGameConfigurationDTO.DEFAULT_MAX_CARDS_PER_CELL) {
                return String.format("This cell is at maximum capacity ([%d] cards).", AdvancedGameConfigurationDTO.DEFAULT_MAX_CARDS_PER_CELL);
            }
        }

        return "";
    }

    public void validateAdvancedGameConfigurations(AdvancedGameConfigurationDTO advancedGameConfigurationDTO) {
        if (advancedGameConfigurationDTO.getMaxCardsPerCell() < 1) {
            throw new AdvGameConfigException(String.format("Error in Advanced Game Configurations :: getMaxCardsPerCell must be 1 or greater. Current value: %d", advancedGameConfigurationDTO.getMaxCardsPerCell()));
        }

        double ratesSum = advancedGameConfigurationDTO.getDropRates().values().stream().reduce(0.0, Double::sum);
        if (ratesSum < 1 || ratesSum > 1) {
            throw new AdvGameConfigException(String.format("Error in Advanced Game Configurations :: getDropRates must add up to 1.0. Current value: %f", ratesSum));
        }
    }

    private void drawCard(String username, Game game, int cardIndex) {
        if (game.getUseAdvancedConfigs() == null) {
            game.setUseAdvancedConfigs(false);
        }

        Card newCard = Card.generateCard(username, game.getUseAdvancedConfigs() ? game.getAdvancedGameConfigs().getDropRates() : AdvancedGameConfigurationDTO.DEFAULT_DROP_RATES());
        LOGGER.info("Generated new card at index={}: {}", cardIndex, newCard);
        game.getCardsMap().get(username).set(cardIndex, newCard);
    }
}
