package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.api.error.RegisterException;
import yay.linda.genericbackend.api.error.UsernamePasswordMismatchException;
import yay.linda.genericbackend.model.*;
import yay.linda.genericbackend.repository.SessionRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public UserDTO getUserFromSessionToken(String sessionToken) {
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
            throw NotFoundException.sessionTokenNotFound(sessionToken);
        }
    }

    public SessionTokenDTO register(RegisterRequest registerRequest) {

        if (usernameExists(registerRequest.getUsername())) {
            throw new RegisterException(String.format("Username='%s' is unavailable.", registerRequest.getUsername()));
        }

        if (emailExists(registerRequest.getEmail())) {
            throw new RegisterException(String.format("Email='%s' is already associated with another account.", registerRequest.getEmail()));
        }

        String sessionToken = UUID.randomUUID().toString();
        persistUser(registerRequest);
        persistSession(registerRequest.getUsername(), sessionToken);

        return new SessionTokenDTO(sessionToken);
    }

    public SessionTokenDTO login(LoginRequest loginRequest) {

        if (!usernameExists(loginRequest.getUsername())) {
            throw NotFoundException.usernameNotFound(loginRequest.getUsername());
        }

        if (!verifyPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            throw new UsernamePasswordMismatchException(loginRequest.getUsername());
        }

        String sessionToken = UUID.randomUUID().toString();
        persistSession(loginRequest.getUsername(), sessionToken);

        return new SessionTokenDTO(sessionToken);
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

    private void persistSession(String username, String sessionToken) {
        User user = userRepository.findByUsername(username).get()
                .setSessionToken(sessionToken)
                .setLastLogin(new Date());

        userRepository.save(user);

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

    private void sendConfirmationEmail(RegisterRequest registerRequest) {
        // TODO
    }
}
