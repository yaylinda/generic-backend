package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yay.linda.genericbackend.api.error.AdvGameConfigException;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.CreateJoinGameResponseDTO;
import yay.linda.genericbackend.model.Game;
import yay.linda.genericbackend.model.GameConfiguration;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.GameStatus;
import yay.linda.genericbackend.model.InviteToGameDTO;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.model.PutCardStatus;
import yay.linda.genericbackend.model.UserActivity;
import yay.linda.genericbackend.repository.GameRepository;

import java.time.Instant;
import java.util.Collections;
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
//        userService.updateActivity(username, UserActivity.GET_GAMES_LIST);

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

        Collections.sort(gameDTOs);

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
            gameDTO = createGame(sessionToken, false);
            response.setCreateOrJoin("CREATE");
        } else {
            LOGGER.info("Found available game {} to join, gameId={}", username, waitingGames.get(0).getId());
            gameDTO = joinGame(sessionToken, waitingGames.get(0).getId());
            response.setCreateOrJoin("JOIN");
        }

        response.setGame(gameDTO);

        return response;
    }

    /**
     * Join a game by gameId
     *
     * @param sessionToken
     * @param gameId
     * @return
     */
    public GameDTO joinGame(String sessionToken, String gameId) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.JOIN_GAME);
        userService.incrementNumGames(username);

        Game gameToJoin = getGameById(gameId);

        gameToJoin.addPlayer2ToGame(username);

        gameRepository.save(gameToJoin);
        LOGGER.info("Found game with id={} for {} to join", gameId, username);
        this.messagingTemplate.convertAndSend("/topic/player2Joined/" + gameToJoin.getPlayer1(), gameToJoin.getId());

        return new GameDTO(gameToJoin, false);
    }

    /**
     * Create a new DEFAULT game
     *
     * @param sessionToken
     * @param isAi
     * @return
     */
    public GameDTO createGame(String sessionToken, Boolean isAi) {

        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.CREATE_GAME);
        userService.incrementNumGames(username);

        Game newGame = new Game(GameConfiguration.DEFAULT(), isAi);

        newGame.createGameForPlayer1(username);

        gameRepository.save(newGame);
        LOGGER.info("Created new game for {} with gameId={}", username, newGame.getId());
        this.messagingTemplate.convertAndSend("/topic/gameCreated", username);

        return new GameDTO(newGame, true);
    }

    /**
     * Create a new game and invite a user to it
     *
     * @param sessionToken
     * @param inviteToGameDTO
     * @return
     */
    public GameDTO inviteToGame(String sessionToken, InviteToGameDTO inviteToGameDTO) {

        if (inviteToGameDTO.getIsAdvanced()) {
            this.validateAdvancedGameConfigurations(inviteToGameDTO.getGameConfiguration());
        }

        // set missing fields of GameConfig
        inviteToGameDTO.getGameConfiguration().setIsAdvanced(inviteToGameDTO.getIsAdvanced());
        inviteToGameDTO.getGameConfiguration().setMinTerritoryRow(
                inviteToGameDTO.getGameConfiguration().getNumRows() - inviteToGameDTO.getGameConfiguration().getNumTerritoryRows());

        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.INVITE_TO_GAME);
        userService.incrementNumGames(username);
        userService.incrementNumGames(inviteToGameDTO.getPlayer2());

        Game newGame = new Game(inviteToGameDTO.getIsAdvanced() ? inviteToGameDTO.getGameConfiguration() : GameConfiguration.DEFAULT(), false);

        newGame.createGameForPlayer1(username);
        newGame.addPlayer2ToGame(inviteToGameDTO.getPlayer2());

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

        return putCardHelper(game, username, opponentName, isPlayer1, putCardRequest);
    }

    public PutCardResponse putCardHelper(Game game, String username, String opponentName, Boolean isPlayer1, PutCardRequest putCardRequest) {

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
                int opponentRow = (game.getGameConfig().getNumRows() - 1) - putCardRequest.getRow();
                game.putCardOnBoard(opponentName, opponentRow, putCardRequest.getCol(), putCardRequest.getCard());
            }

            drawCard(username, game, putCardRequest.getCardIndex());

            gameRepository.save(game);
            this.messagingTemplate.convertAndSend("/topic/opponentPutCard/" + opponentName, game.getId());
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

        return endTurnHelper(game, username, discardHand);
    }

    public GameDTO endTurnHelper(Game game, String username, Boolean discardHand) {

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
            IntStream.range(0, game.getGameConfig().getNumCardsInHand()).boxed()
                    .forEach(i -> drawCard(username, game, i));
        }

        game.updatePreviousBoard(username); // sets current board state to previous board state
        game.updateTransitionalBoard(username); // moves all "my" troops forward
        game.updateCurrentBoard(username, opponentName); // performs clash on cells with multiple cards

        game.incrementNumTurns(username);
        game.incrementEnergyForEndTurn(username);

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            game.updateOpponentBoard(username, opponentName);
            this.messagingTemplate.convertAndSend("/topic/opponentEndedTurn/" + opponentName, game.getId());
        }

        if (game.getPointsMap().get(username) >= game.getGameConfig().getPointsToWin()) {
            game.setStatus(GameStatus.COMPLETED);
            game.setCompletedDate(Date.from(Instant.now()));
            game.setWinner(username);
            userService.incrementNumWins(username);
        }

        gameRepository.save(game);
        return new GameDTO(game, isPlayer1);
    }

    public void validateAdvancedGameConfigurations(GameConfiguration gameConfiguration) {

        LOGGER.info("Validating Advanced Game Configurations Input");

        if (gameConfiguration.getMaxCardsPerCell() < 1) {
            throw new AdvGameConfigException(String.format("Error in Advanced Game Configurations :: getMaxCardsPerCell must be 1 or greater. Current value: %d", gameConfiguration.getMaxCardsPerCell()));
        }

        double ratesSum = gameConfiguration.getDropRates().values().stream().reduce(0.0, Double::sum);
        if (ratesSum < 1 || ratesSum > 1) {
            throw new AdvGameConfigException(String.format("Error in Advanced Game Configurations :: getDropRates must add up to 1.0. Current value: %f", ratesSum));
        }

        // TODO - validate other game configs

        /*
        maxCardsPerCell > 1
        pointsToWin > 1
        maxEnergy > 1, and maxEnergy >= startingEnergy
        numRows > 1
        numCols > 1
        numCardsInHand > 1
        numTerritoryRows > 1 and numTerritoryRows <= numTerritoryRows
        energyGrowthRate > 0
        startingEnergy > 0
        */
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

    public Game getGameById(String gameId) {
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
        if (request.getRow() < currentGame.getGameConfig().getMinTerritoryRow()) {
            return "Card must be placed on your Territory";
        }
        //check not too many cards are in cell
        if (currentGame.getBoardMap().get(username).get(request.getRow()).get(request.getCol()).getCards().size() >= currentGame.getGameConfig().getMaxCardsPerCell()) {
            return String.format("This cell is at maximum capacity ([%d] cards).", currentGame.getGameConfig().getMaxCardsPerCell());
        }

        return "";
    }

    private void drawCard(String username, Game game, int cardIndex) {
        Card newCard = Card.generateCard(username, game.getGameConfig().getDropRates());
        LOGGER.info("Generated new card at index={}: {}", cardIndex, newCard);
        game.getCardsMap().get(username).set(cardIndex, newCard);
    }
}
