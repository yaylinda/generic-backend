package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.model.FriendRequest;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
}
