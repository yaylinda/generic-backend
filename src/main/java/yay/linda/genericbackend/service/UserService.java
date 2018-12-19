package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yay.linda.genericbackend.model.*;
import yay.linda.genericbackend.repository.SessionRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static yay.linda.genericbackend.model.ResponseStatus.*;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public UserDTO getUserFromToken(String sessionToken) {
        Optional<User> optionalUser = userRepository.findBySessionToken(sessionToken);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            LOGGER.info("Found! {}", user);
            return UserDTO.builder()
                    .email(user.getEmail())
                    .token(user.getSessionToken())
                    .username(user.getUsername())
                    .build();
        } else {
            LOGGER.warn("No user found matching sessionToken={}", sessionToken);
            return null;
        }
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        if (usernameExists(registerRequest.getUsername())) {
            return new RegisterResponse()
                    .setStatus(USERNAME_TAKEN)
                    .setMessage("username taken");
        }

        if (emailExists(registerRequest.getEmail())) {
            return new RegisterResponse()
                    .setStatus(EMAIL_TAKEN)
                    .setMessage("email taken");
        }

        String sessionToken = UUID.randomUUID().toString();
        persistUser(registerRequest);
        persistSession(registerRequest.getUsername(), sessionToken, "username");

        return new RegisterResponse()
                .setStatus(CREATED)
                .setMessage("registration successful!")
                .setToken(sessionToken);
    }

    public LoginResponse login(LoginRequest loginRequest) {

        String username;

        // check if username or email is found
        if (!StringUtils.isEmpty(loginRequest.getUsername())) {
            username = loginRequest.getUsername();
            if (!usernameExists(loginRequest.getUsername())) {
                return new LoginResponse()
                        .setStatus(USERNAME_NOT_FOUND)
                        .setMessage("username not found")
                        .setSessionToken(null);
            }
        } else {
            username = getUsernameFromEmail(loginRequest.getEmail());
            if (!usernameExists(loginRequest.getEmail())) {
                return new LoginResponse()
                        .setStatus(EMAIL_NOT_FOUND)
                        .setMessage("email not found")
                        .setSessionToken(null);
            }
        }

        if (!verifyPassword(username, loginRequest.getPassword())) {
            return new LoginResponse()
                    .setStatus(WRONG_PASSWORD)
                    .setMessage("wrong password")
                    .setSessionToken(null);
        }

        String sessionToken = UUID.randomUUID().toString();
        persistSession(loginRequest.getEmail(), sessionToken, "email");

        return new LoginResponse()
                .setStatus(SUCCESS)
                .setMessage("login successful!")
                .setSessionToken(sessionToken)
                .setUsername(username);
    }

    public void logout(String sessionToken) {
        if (sessionExists(sessionToken)) {
            deleteSession(sessionToken);
        }
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

    private boolean sessionExists(String sessionToken) {
        return sessionRepository.findBySessionToken(sessionToken).isPresent();
    }

    private void persistUser(RegisterRequest registerRequest) {
        User user = new User(registerRequest);
        userRepository.save(user);
    }

    private void persistSession(String username, String sessionToken, String mode) {
        if (mode.equals("username")) {
            User user = userRepository.findByUsername(username).get()
                    .setSessionToken(sessionToken)
                    .setLastLogin(new Date());
            userRepository.save(user);
        } else {
            User user = userRepository.findByEmail(username).get()
                    .setSessionToken(sessionToken)
                    .setLastLogin(new Date());
            userRepository.save(user);
        }

        Session session = new Session()
                .setUsername(username)
                .setSessionToken(sessionToken);
        sessionRepository.save(session);
    }

    private void deleteSession(String sessionToken) {
        sessionRepository.deleteBySessionToken(sessionToken);
        User user = userRepository.findBySessionToken(sessionToken).get()
                .setSessionToken(null);
        userRepository.save(user);
    }

    private String getUsernameFromEmail(String email) {
        return userRepository.findByEmail(email).get().getUsername();
    }

    private void sendConfirmationEmail(RegisterRequest registerRequest) {
        // TODO
    }
}
