package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.dto.*;
import yay.linda.genericbackend.dto.ResponseStatus;
import yay.linda.genericbackend.service.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{token}")
    public ResponseEntity<?> getUserFromToken(@PathVariable("token") String token) {
        LOGGER.info("GET USER request: token={}", token);
        UserDTO userDTO = userService.getUserFromToken(token);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return new ResponseEntity(
                    new ErrorDTO(ResponseStatus.SESSION_TOKEN_NOT_FOUND, "Unable to find token=" + token),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("REGISTER request: {}", registerRequest);

        RegisterResponse registerResponse = userService.register(registerRequest);
        LOGGER.info("REGISTER response: {}", registerResponse);

        if (registerResponse.getStatus() == ResponseStatus.CREATED) {
            return ResponseEntity.ok(new UserDTO()
                    .setEmail(registerRequest.getEmail())
                    .setToken(registerResponse.getToken())
                    .setUsername(registerRequest.getUsername()));
        } else {
            return new ResponseEntity(
                    new ErrorDTO(registerResponse.getStatus(), registerResponse.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("LOGIN request: {}", loginRequest);

        LoginResponse loginResponse = userService.login(loginRequest);
        LOGGER.info("LOGIN response: {}", loginResponse);

        if (loginResponse.getStatus() == ResponseStatus.SUCCESS) {
            return ResponseEntity.ok(new UserDTO()
                    .setEmail(loginRequest.getEmail())
                    .setToken(loginResponse.getSessionToken())
                    .setUsername(loginResponse.getUsername()));
        } else {
            return new ResponseEntity(
                    new ErrorDTO(loginResponse.getStatus(), loginResponse.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout/{token}")
    public ResponseEntity<?> logout(@PathVariable("token") String token) {
        LOGGER.info("LOGOUT request: token={}", token);
        userService.logout(token);
        return ResponseEntity.ok().build();
    }

}
