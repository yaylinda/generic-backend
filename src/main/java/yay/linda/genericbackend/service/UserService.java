package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.api.error.RegisterException;
import yay.linda.genericbackend.api.error.UsernamePasswordMismatchException;
import yay.linda.genericbackend.model.*;
import yay.linda.genericbackend.repository.UserRepository;

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

        return new UserDTO(optionalUser.get());
    }

    public SessionTokenDTO register(RegisterRequest registerRequest) {

        if (usernameExists(registerRequest.getUsername())) {
            throw RegisterException.usernameTaken(registerRequest.getUsername());
        }

        if (emailExists(registerRequest.getEmail())) {
            throw RegisterException.emailTaken(registerRequest.getEmail());
        }

        createUser(registerRequest);

        Session session = sessionService.createSession(registerRequest.getUsername());

        return new SessionTokenDTO(session.getSessionToken());
    }

    public SessionTokenDTO login(LoginRequest loginRequest) {

        if (!usernameExists(loginRequest.getUsername())) {
            throw NotFoundException.usernameNotFound(loginRequest.getUsername());
        }

        if (!verifyPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            throw new UsernamePasswordMismatchException(loginRequest.getUsername());
        }

        Session session = sessionService.createSession(loginRequest.getUsername());

        return new SessionTokenDTO(session.getSessionToken());
    }

    public void logout(String sessionToken) {
        sessionService.deleteSession(sessionToken);
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
