package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.model.*;
import yay.linda.genericbackend.model.ResponseStatus;
import yay.linda.genericbackend.service.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{token}")
    public ResponseEntity<UserDTO> getUserFromToken(@PathVariable("token") String token) {
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

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("REGISTER request: {}", registerRequest);

        RegisterResponse registerResponse = userService.register(registerRequest);
        LOGGER.info("REGISTER response: {}", registerResponse);

        if (registerResponse.getStatus() == ResponseStatus.CREATED) {
            return ResponseEntity.ok(UserDTO.builder()
                    .email(registerRequest.getEmail())
                    .token(registerResponse.getToken())
                    .username(registerRequest.getUsername())
                    .build());
        } else {
            return new ResponseEntity(
                    new ErrorDTO(registerResponse.getStatus(), registerResponse.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("LOGIN request: {}", loginRequest);

        LoginResponse loginResponse = userService.login(loginRequest);
        LOGGER.info("LOGIN response: {}", loginResponse);

        if (loginResponse.getStatus() == ResponseStatus.SUCCESS) {
            return ResponseEntity.ok(UserDTO.builder()
                    .email(loginRequest.getEmail())
                    .token(loginResponse.getSessionToken())
                    .username(loginResponse.getUsername())
                    .build());
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
