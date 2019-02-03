package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.model.Card;
import yay.linda.genericbackend.model.GameDTO;
import yay.linda.genericbackend.model.PutCardDTO;
import yay.linda.genericbackend.model.PutCardResponseDTO;
import yay.linda.genericbackend.service.GameService;

import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin
public class GameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @GetMapping("/{username}")
    public ResponseEntity<List<GameDTO>> getGamesByUsername(
            @PathVariable("username") String username) {
        LOGGER.info("GET GAMES request: username={}", username);
        return ResponseEntity.ok(gameService.getGameDTOsByUsername(username));
    }

    @GetMapping("/{gameId}/{username}")
    public ResponseEntity<GameDTO> getGameById(
            @PathVariable("gameId") String gameId,
            @PathVariable("username") String username) {
        LOGGER.info("GET GAME BY ID request: gameId={}, username={}", gameId, username);
        return ResponseEntity.ok(gameService.getGameDTOByIdAndUsername(gameId, username));
    }

    @GetMapping("/start/{username}")
    public ResponseEntity<GameDTO> startGame(
            @PathVariable("username") String username) {
        LOGGER.info("START GAME request: username={}", username);
        return ResponseEntity.ok(gameService.startGame(username));
    }

    @PutMapping("/putCard/{gameId}/{username}")
    public ResponseEntity<PutCardResponseDTO> putCard(
            @PathVariable("gameId") String gameId,
            @PathVariable("username") String username,
            @RequestBody PutCardDTO putCardDTO) {
        LOGGER.info("PUT CARD request: gameId={}, username={}, putCardDTO={}", gameId, username, putCardDTO);
        return ResponseEntity.ok(gameService.putCard(gameId, username, putCardDTO));
    }

    @GetMapping("/endTurn/{gameId}/{username}")
    public ResponseEntity<GameDTO> endTurn(
            @PathVariable("gameId") String gameId,
            @PathVariable("username") String username,
            @RequestParam(value = "discard", defaultValue = "false") Boolean discardHand) {
        LOGGER.info("END TURN request: gameId={}, username={}, discard={}", gameId, username, discardHand);
        return ResponseEntity.ok(gameService.endTurn(gameId, username, discardHand));
    }

    @GetMapping("/card/{gameId}/{username}/{usedCardIndex}")
    public ResponseEntity<Card> drawCard(
            @PathVariable("gameId") String gameId,
            @PathVariable("username") String username,
            @PathVariable("usedCardIndex") int usedCardIndex) {
        LOGGER.info("DRAW CARD request: username={}", username);
        return ResponseEntity.ok(gameService.drawCard(gameId, username, usedCardIndex));
    }
}
