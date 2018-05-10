package yay.linda.genericbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yay.linda.genericbackend.dto.*;
import yay.linda.genericbackend.service.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("REGISTER request: {}", registerRequest);
        RegisterResponse registerResponse = userService.register(registerRequest);
        LOGGER.info("REGISTER response: {}", registerResponse);
        if (registerResponse.getStatus() == RegisterResponseStatus.CREATED) {
            return ResponseEntity.ok(registerResponse);
        } else {
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("LOGIN request: {}", loginRequest);
        LoginResponse loginResponse = userService.login(loginRequest);
        LOGGER.info("LOGIN response: {}", loginResponse);
        if (loginResponse.getStatus() == LoginResponseStatus.SUCCESS) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.badRequest().body(loginResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        return ResponseEntity.ok(userService.logout(logoutRequest.getSessionToken()));
    }

}
