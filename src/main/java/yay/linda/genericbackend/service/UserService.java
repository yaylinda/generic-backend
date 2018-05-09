package yay.linda.genericbackend.service;

import org.springframework.stereotype.Service;
import yay.linda.genericbackend.dto.LoginRequest;
import yay.linda.genericbackend.dto.LoginResponse;
import yay.linda.genericbackend.dto.RegisterRequest;
import yay.linda.genericbackend.dto.RegisterResponse;

import java.util.UUID;

import static yay.linda.genericbackend.dto.LoginResponseStatus.SUCCESS;
import static yay.linda.genericbackend.dto.LoginResponseStatus.USERNAME_NOT_FOUND;
import static yay.linda.genericbackend.dto.LoginResponseStatus.WRONG_PASSWORD;
import static yay.linda.genericbackend.dto.RegisterResponseStatus.CREATED;
import static yay.linda.genericbackend.dto.RegisterResponseStatus.EMAIL_TAKEN;
import static yay.linda.genericbackend.dto.RegisterResponseStatus.USERNAME_TAKEN;

@Service
public class UserService {

    public RegisterResponse register(RegisterRequest registerRequest) {

        if (usernameExists(registerRequest.getUsername())) {
            return new RegisterResponse()
                    .setStatus(USERNAME_TAKEN)
                    .setMessage("username taken")
                    .setRegisterRequest(registerRequest);
        }

        if (emailExists(registerRequest.getEmail())) {
            return new RegisterResponse()
                    .setStatus(EMAIL_TAKEN)
                    .setMessage("email taken")
                    .setRegisterRequest(registerRequest);
        }

        sendConfirmationEmail(registerRequest);
        persistUser(registerRequest);

        return new RegisterResponse()
                .setStatus(CREATED)
                .setMessage("registration successful!")
                .setRegisterRequest(registerRequest);
    }

    public LoginResponse login(LoginRequest loginRequest) {

        if (!usernameExists(loginRequest.getUsername())) {
            return new LoginResponse()
                    .setStatus(USERNAME_NOT_FOUND)
                    .setMessage("username not found")
                    .setSessionToken(null)
                    .setLoginRequest(loginRequest);
        }

        if (!verifyPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            return new LoginResponse()
                    .setStatus(WRONG_PASSWORD)
                    .setMessage("wrong password")
                    .setSessionToken(null)
                    .setLoginRequest(loginRequest);
        }

        String sessionToken = UUID.randomUUID().toString();
        persistSession(loginRequest, sessionToken);

        return new LoginResponse()
                .setStatus(SUCCESS)
                .setMessage("login successful!")
                .setSessionToken(sessionToken)
                .setLoginRequest(loginRequest);
    }

    private boolean usernameExists(String username) {
        return true; // TODO
    }

    private boolean emailExists(String email) {
        return true; // TODO
    }

    private boolean verifyPassword(String username, String password) {
        return true; // TODO
    }

    private boolean persistUser(RegisterRequest registerRequest) {
        return false; // TODO
    }

    private boolean persistSession(LoginRequest loginRequest, String sessionToken) {
        return false; // TODO
    }

    private void sendConfirmationEmail(RegisterRequest registerRequest) {
        // TODO
    }


}
