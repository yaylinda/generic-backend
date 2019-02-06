package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.model.LoginRequest;
import yay.linda.genericbackend.model.RegisterRequest;
import yay.linda.genericbackend.model.UserDTO;
import yay.linda.genericbackend.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{sessionToken}")
    public ResponseEntity<UserDTO> getUserFromSessionToken(@PathVariable("sessionToken") String sessionToken) {
        LOGGER.info("GET USER from sessionToken request: sessionToken={}", sessionToken);
        return ResponseEntity.ok(userService.getUserFromSessionToken(sessionToken));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("REGISTER request: {}", registerRequest);
        return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("LOGIN request: {}", loginRequest);
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/logout/{sessionToken}")
    public ResponseEntity<?> logout(@PathVariable("sessionToken") String sessionToken) {
        LOGGER.info("LOGOUT request: sessionToken={}", sessionToken);
        userService.logout(sessionToken);
        return ResponseEntity.noContent().build();
    }

}
