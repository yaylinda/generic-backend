package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.model.GameDTO;
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
        LOGGER.info("GET GAMES request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.getGames(sessionToken));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(
            @RequestHeader("Session-Token") String sessionToken,
            @PathVariable("gameId") String gameId) {
        LOGGER.info("GET GAME BY ID request: sessionToken={}, gameId={}", sessionToken, gameId);
        return ResponseEntity.ok(gameService.getGameById(sessionToken, gameId));
    }

    @GetMapping("/start")
    public ResponseEntity<GameDTO> startGame(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("START GAME request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(gameService.startGame(sessionToken));
    }

    @PutMapping("/putCard/{gameId}")
    public ResponseEntity<PutCardResponse> putCard(
            @RequestHeader("Session-Token") String sessionToken,
            @PathVariable("gameId") String gameId,
            @RequestBody PutCardRequest putCardRequest) {
        LOGGER.info("PUT CARD request: sessionToken={}, gameId={}, putCardRequest={}", sessionToken, gameId, putCardRequest);
        return ResponseEntity.ok(gameService.putCard(sessionToken, gameId, putCardRequest));
    }

    @GetMapping("/endTurn/{gameId}")
    public ResponseEntity<GameDTO> endTurn(
            @RequestHeader("Session-Token") String sessionToken,
            @PathVariable("gameId") String gameId,
            @RequestParam(value = "discard", defaultValue = "false") Boolean discardHand) {
        LOGGER.info("END TURN request: sessionToken={}, gameId={}, discard={}", sessionToken, gameId, discardHand);
        return ResponseEntity.ok(gameService.endTurn(sessionToken, gameId, discardHand));
    }
}
