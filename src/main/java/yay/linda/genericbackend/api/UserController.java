package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.dto.*;
import yay.linda.genericbackend.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{token}")
    public ResponseEntity<UserDTO> getUserFromToken(@PathVariable("token") String token) {
        LOGGER.info("GET USER request: {}", token);
        UserDTO userDTO = userService.getUserFromToken(token);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("REGISTER request: {}", registerRequest);

        RegisterResponse registerResponse = userService.register(registerRequest);
        LOGGER.info("REGISTER response: {}", registerResponse);

        if (registerResponse.getStatus() == RegisterResponseStatus.CREATED) {
            return ResponseEntity.ok(new UserDTO()
                    .setEmail(registerRequest.getEmail())
                    .setToken(registerResponse.getToken())
                    .setUsername(registerRequest.getUsername()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("LOGIN request: {}", loginRequest);

        LoginResponse loginResponse = userService.login(loginRequest);
        LOGGER.info("LOGIN response: {}", loginResponse);

        if (loginResponse.getStatus() == LoginResponseStatus.SUCCESS) {
            return ResponseEntity.ok(new UserDTO()
                    .setEmail(loginRequest.getEmail())
                    .setToken(loginResponse.getSessionToken())
                    .setUsername(loginResponse.getUsername()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        return ResponseEntity.ok(userService.logout(logoutRequest.getSessionToken()));
    }

}
