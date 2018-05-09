package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
}
