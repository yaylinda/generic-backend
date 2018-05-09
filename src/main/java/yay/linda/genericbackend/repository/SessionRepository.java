package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.domain.Session;

public interface SessionRepository extends MongoRepository<Session, String> {
}
