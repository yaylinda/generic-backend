package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.api.error.SessionExpiredException;
import yay.linda.genericbackend.model.Session;
import yay.linda.genericbackend.repository.SessionRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.Optional;

@Service
public class SessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    public String getUsernameFromSessionToken(String sessionToken) {
        LOGGER.info("Looking up sessionToken={}", sessionToken);
        Optional<Session> optionalSession = sessionRepository.findBySessionToken(sessionToken);
        if (optionalSession.isPresent()) {

            Session session = optionalSession.get();
            LOGGER.info("Found session: {}", session);

            if (!session.getIsActive()) {
                throw new SessionExpiredException(sessionToken, session.getUsername());
            }

            return session.getUsername();
        } else {
            throw NotFoundException.sessionTokenNotFound(sessionToken);
        }
    }

    public Session createSession(String username) {
        Session session = new Session(username);
        LOGGER.info("Creating new Session: {}", session);
        sessionRepository.save(session);
        return session;
    }

    public void deleteSession(String sessionToken) {
        LOGGER.info("Deleting active session for sessionToken={}", sessionToken);
        sessionRepository.deleteBySessionToken(sessionToken);
    }

}
