package yay.linda.genericbackend.api;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yay.linda.genericbackend.model.FriendRequestDTO;
import yay.linda.genericbackend.model.PlayerDTO;
import yay.linda.genericbackend.model.RequestFriendDTO;
import yay.linda.genericbackend.model.RespondFriendDTO;
import yay.linda.genericbackend.model.UserDTO;
import yay.linda.genericbackend.service.PlayerService;

import java.util.List;

@Api(tags = "Players Controller")
@RestController
@RequestMapping("/players")
@CrossOrigin
public class PlayerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PlayerService playerService;

    @GetMapping("/all")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET PLAYERS from sessionToken request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(playerService.getAllPlayers(sessionToken));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerDTO>> searchPlayersByUsername(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestParam(value = "query") String query) {
        LOGGER.info("SEARCH PLAYERS sessionToken={}, query={}", sessionToken, query);
        return ResponseEntity.ok(playerService.searchPlayersByUsername(sessionToken, query));
    }

    @GetMapping("/one")
    public ResponseEntity<PlayerDTO> getOnePlayer(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET PLAYER from sessionToken request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(playerService.getOnePlayer(sessionToken));
    }

    @GetMapping("/friends")
    public ResponseEntity<List<PlayerDTO>> getFriends(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET FRIENDS from sessionToken request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(playerService.getFriends(sessionToken));
    }

    @GetMapping("/friends/requests")
    public ResponseEntity<List<FriendRequestDTO>> getRequests(
            @RequestHeader("Session-Token") String sessionToken) {
        LOGGER.info("GET friend requests from sessionToken request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(playerService.getFriendRequests(sessionToken));
    }

    @PostMapping("/friends/requests")
    public ResponseEntity<UserDTO> requestFriend(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestBody RequestFriendDTO requestFriendDTO) {
        LOGGER.info("PUT request friend: sessionToken={}, requestFriendDTO={}", sessionToken, requestFriendDTO);
        playerService.requestFriend(sessionToken, requestFriendDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/friends/responses")
    public ResponseEntity<UserDTO> respondFriend(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestBody RespondFriendDTO respondFriendDTO) {
        LOGGER.info("PUT respond friend: sessionToken={}, respondFriendDTO={}", sessionToken, respondFriendDTO);
        playerService.respondFriend(sessionToken, respondFriendDTO);
        return ResponseEntity.ok().build();
    }
}
