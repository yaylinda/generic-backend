package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.service.GameService;

@RestController
@RequestMapping("/game")
@CrossOrigin
public class GameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getGamesByUsername(@PathVariable("username") String username) {
        LOGGER.info("GET GAMES request: username={}", username);
        return ResponseEntity.ok(gameService.getGameDTOsByUsername(username));
    }

    @GetMapping("/{gameId}/{username}")
    public ResponseEntity<?> getGameById(
            @PathVariable("gameId") String gameId,
            @PathVariable("username") String username) {
        LOGGER.info("GET GAME BY ID request: gameId={}, username={}\", gameId, username");
        return ResponseEntity.ok(gameService.getGameDTOByIdAndUsername(gameId, username));
    }

    @GetMapping("/start/{username}")
    public ResponseEntity<?> startGame(@PathVariable("username") String username) {
        LOGGER.info("START GAME request: username={}", username);
        return ResponseEntity.ok(gameService.startGame(username));
    }

    @GetMapping("/endTurn/{gameId}/{username}")
    public ResponseEntity<?> endTurn(@PathVariable("gameId") String gameId, @PathVariable("username") String username) {
        LOGGER.info("END TURN request: gameId={}, username={}", gameId, username);
        return ResponseEntity.ok(gameService.endTurn(gameId, username));
    }
}