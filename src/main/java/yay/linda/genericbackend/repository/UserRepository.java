package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findBySessionToken(String sessionToken);
}
