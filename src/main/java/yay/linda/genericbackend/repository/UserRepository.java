package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByOrderByLastActiveDateDesc();

    List<User> findByUsernameLike(String query);
}
