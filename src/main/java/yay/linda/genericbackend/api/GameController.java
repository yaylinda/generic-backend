package yay.linda.genericbackend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yay.linda.genericbackend.api.error.ErrorDTO;
import yay.linda.genericbackend.model.CreateJoinGameResponseDTO;
import yay.linda.genericbackend.model.GameConfiguration;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.GameStatus;
import yay.linda.genericbackend.model.InviteToGameDTO;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.service.GameAIPlayer;
import yay.linda.genericbackend.service.GameService;

import java.util.List;
import java.util.Objects;

import static yay.linda.genericbackend.api.error.ErrorMessages.NOT_FOUND;
import static yay.linda.genericbackend.api.error.ErrorMessages.UNEXPECTED_ERROR;

@Api(tags = "Games Controller")
@RestController
@RequestMapping("/games")
@CrossOrigin
public class GameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private GameAIPlayer gameAIPlayer;

    @ApiOperation(value = "Retrieve all Simple War games for a player, given a valid Session-Token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved games"),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorDTO.class),
            @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = ErrorDTO.class)
    })
    @GetMapping("")
    public ResponseEntity<List<GameDTO>> getGames(
            @ApiParam(value = "Session-Token", required = true)
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET GAMES: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.getGames(sessionToken));
    }

    @ApiOperation(value = "Retrieve a Simple War game by gameId for a player, given a valid Session-Token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved game by id"),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorDTO.class),
            @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = ErrorDTO.class)
    })
    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(
            @ApiParam(value = "Session-Token", required = true)
            @RequestHeader("Session-Token") String sessionToken,
            @ApiParam(value = "gameId", required = true)
            @PathVariable("gameId") String gameId) {
        LOGGER.info("GET GAME BY ID: sessionToken={}, gameId={}", sessionToken, gameId);
        return ResponseEntity.ok(gameService.getGameById(sessionToken, gameId));
    }

    @GetMapping("/joinable")
    public ResponseEntity<List<GameDTO>> getJoinableGames(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET JOINABLE GAMES: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.getJoinableGames(sessionToken));
    }

    @GetMapping("/join/{gameId}")
    public ResponseEntity<GameDTO> joinGame(
            @RequestHeader("Session-Token") String sessionToken,
            @PathVariable("gameId") String gameId) {
        LOGGER.info("JOIN GAME: sessionToken={}, gameId={}", sessionToken, gameId);
        return ResponseEntity.ok(gameService.joinGame(sessionToken, gameId));
    }

    @GetMapping("/createOrJoin")
    public ResponseEntity<CreateJoinGameResponseDTO> createOrJoinGame(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("CREATE OR GAME: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.createOrJoinGame(sessionToken));
    }

    @PostMapping("/new/validate")
    public ResponseEntity<String> validateAdvancedgameConfiguration(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestBody GameConfiguration gameConfiguration) {
        LOGGER.info("VALIDATE ADV GAME CONFIG: sessionToken={}, gameConfiguration={}", sessionToken, gameConfiguration);
        gameService.validateAdvancedGameConfigurations(gameConfiguration);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/new")
    public ResponseEntity<GameDTO> createGame(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestParam(value = "ai", defaultValue = "false") Boolean isAi) {
        LOGGER.info("CREATE GAME: sessionToken={}, isAi={}", sessionToken, isAi);
        return ResponseEntity.ok(gameService.createGame(sessionToken, isAi));
    }

    @PostMapping("/invite")
    public ResponseEntity<GameDTO> inviteToGame(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestBody InviteToGameDTO inviteToGameDTO) {
        LOGGER.info("CREATE GAME: sessionToken={}, inviteToGameDTO={}", sessionToken, inviteToGameDTO);
        return ResponseEntity.ok(gameService.inviteToGame(sessionToken, inviteToGameDTO));
    }

    @PutMapping("/putCard/{gameId}")
    public ResponseEntity<PutCardResponse> putCard(
            @RequestHeader("Session-Token") String sessionToken,
            @PathVariable("gameId") String gameId,
            @RequestBody PutCardRequest putCardRequest) {
        LOGGER.info("PUT CARD: sessionToken={}, gameId={}, putCardRequest={}", sessionToken, gameId, putCardRequest);
        return ResponseEntity.ok(gameService.putCard(sessionToken, gameId, putCardRequest));
    }

    @GetMapping("/endTurn/{gameId}")
    public ResponseEntity<GameDTO> endTurn(
            @RequestHeader("Session-Token") String sessionToken,
            @PathVariable("gameId") String gameId,
            @RequestParam(value = "discard", defaultValue = "false") Boolean discardHand) {
        LOGGER.info("END TURN: sessionToken={}, gameId={}, discard={}", sessionToken, gameId, discardHand);
        GameDTO gameDTO = gameService.endTurn(sessionToken, gameId, discardHand);

        if (!Objects.isNull(gameDTO.getIsAi()) && gameDTO.getIsAi() && gameDTO.getStatus() != GameStatus.COMPLETED) {
            LOGGER.info("gameId={} isAi... invoking SimpleWar AI...");
            gameDTO = gameAIPlayer.nextMove(gameId, gameDTO.getUsername(), sessionToken);
        }

        return ResponseEntity.ok(gameDTO);
    }

    @GetMapping("/default-configs")
    public ResponseEntity<GameConfiguration> getDefaultgameConfiguration() {
        LOGGER.info("GET default game configs");
        return ResponseEntity.ok(GameConfiguration.DEFAULT());
    }
}
