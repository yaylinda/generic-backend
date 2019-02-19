package yay.linda.genericbackend.api;

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
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.InviteToGameDTO;
import yay.linda.genericbackend.model.PutCardRequest;
import yay.linda.genericbackend.model.PutCardResponse;
import yay.linda.genericbackend.service.GameService;

import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin
public class GameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @GetMapping("")
    public ResponseEntity<List<GameDTO>> getGames(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET GAMES: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.getGames(sessionToken));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(
            @RequestHeader("Session-Token") String sessionToken,
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

    @GetMapping("/join")
    public ResponseEntity<GameDTO> joinGame(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestParam("gameId") String gameId) {
        LOGGER.info("JOIN GAME: sessionToken={}, gameId={}", sessionToken, gameId);
        return ResponseEntity.ok(gameService.joinGame(sessionToken, gameId));
    }

    @PostMapping("/new")
    public ResponseEntity<GameDTO> createGame(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("CREATE GAME: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.createGame(sessionToken));
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
        return ResponseEntity.ok(gameService.endTurn(sessionToken, gameId, discardHand));
    }
}
