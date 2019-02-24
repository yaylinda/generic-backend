package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.api.error.RegisterException;
import yay.linda.genericbackend.api.error.UsernamePasswordMismatchException;
import yay.linda.genericbackend.model.LoginRequest;
import yay.linda.genericbackend.model.RegisterRequest;
import yay.linda.genericbackend.model.Session;
import yay.linda.genericbackend.model.User;
import yay.linda.genericbackend.model.UserActivity;
import yay.linda.genericbackend.model.UserDTO;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    public UserDTO getUserFromSessionToken(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw NotFoundException.usernameNotFound(username);
        }

        return UserDTO.builder()
                .username(optionalUser.get().getUsername())
                .sessionToken(sessionToken)
                .build();
    }

    public UserDTO register(RegisterRequest registerRequest) {

        if (usernameExists(registerRequest.getUsername())) {
            throw RegisterException.usernameTaken(registerRequest.getUsername());
        }

        if (emailExists(registerRequest.getEmail())) {
            throw RegisterException.emailTaken(registerRequest.getEmail());
        }

        createUser(registerRequest);

        Session session = sessionService.createSession(registerRequest.getUsername());

        return UserDTO.builder()
                .username(registerRequest.getUsername())
                .sessionToken(session.getSessionToken())
                .build();
    }

    public UserDTO login(LoginRequest loginRequest) {

        if (!usernameExists(loginRequest.getUsername())) {
            throw NotFoundException.usernameNotFound(loginRequest.getUsername());
        }

        if (!verifyPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            throw new UsernamePasswordMismatchException(loginRequest.getUsername());
        }

        Session session = sessionService.createSession(loginRequest.getUsername());

        return UserDTO.builder()
                .username(loginRequest.getUsername())
                .sessionToken(session.getSessionToken())
                .build();
    }

    public void logout(String sessionToken) {
        sessionService.deleteSession(sessionToken);
    }

    public void updateActivity(String username, UserActivity userActivity) {
        LOGGER.info("Updating lastActiveDate for {}, lastActivity={}", username, userActivity);
        userRepository.findByUsername(username).ifPresent(u -> {
            u.setLastActiveDate(new Date());
            u.setLastActivity(userActivity.name());
            userRepository.save(u);
        });
    }

    public void incrementNumGames(String username) {
        LOGGER.info("Incrementing numGames for {}", username);
        userRepository.findByUsername(username).ifPresent(u -> {
            u.setNumGames(u.getNumGames() + 1);
            userRepository.save(u);
        });
    }

    public void incrementNumWins(String username) {
        LOGGER.info("Incrementing numWins for {}", username);
        userRepository.findByUsername(username).ifPresent(u -> {
            u.setNumWins(u.getNumWins() + 1);
            userRepository.save(u);
        });
    }

    /*-------------------------------------------------------------------------
        PRIVATE HELPER METHODS
     -------------------------------------------------------------------------*/

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

    private void createUser(RegisterRequest registerRequest) {
        LOGGER.info("Creating new User: {}", registerRequest);
        User user = new User(registerRequest);
        userRepository.save(user);
    }

    private void sendConfirmationEmail(RegisterRequest registerRequest) {
        // TODO
    }
}
