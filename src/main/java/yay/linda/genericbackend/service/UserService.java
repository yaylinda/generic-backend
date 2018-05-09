package yay.linda.genericbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.domain.Session;
import yay.linda.genericbackend.domain.User;
import yay.linda.genericbackend.dto.LoginRequest;
import yay.linda.genericbackend.dto.LoginResponse;
import yay.linda.genericbackend.dto.RegisterRequest;
import yay.linda.genericbackend.dto.RegisterResponse;
import yay.linda.genericbackend.repository.SessionRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.UUID;

import static yay.linda.genericbackend.dto.LoginResponseStatus.*;
import static yay.linda.genericbackend.dto.RegisterResponseStatus.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

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
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean verifyPassword(String username, String password) {
        User user = userRepository.findByUsername(username).get();
        return (user.getPassword().equals(password));
    }

    private void persistUser(RegisterRequest registerRequest) {
        User user = new User(registerRequest);
        userRepository.save(user);
    }

    private void persistSession(LoginRequest loginRequest, String sessionToken) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        user.setSessionToken(sessionToken);
        userRepository.save(user);

        Session session = new Session()
                .setUsername(loginRequest.getUsername())
                .setSessionToken(sessionToken);
        sessionRepository.save(session);
    }

    private void sendConfirmationEmail(RegisterRequest registerRequest) {
        // TODO
    }


}
