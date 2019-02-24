package yay.linda.genericbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yay.linda.genericbackend.model.FriendRequest;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {

    Optional<FriendRequest> findById(String id);

    List<FriendRequest> findAllByRequester(String requester);

    List<FriendRequest> findAllByRequestee(String requestee);

    List<FriendRequest> findAllByRequesterAndStatus(String requester, String status);

    List<FriendRequest> findAllByRequesteeAndStatus(String requestee, String status);

    List<FriendRequest> findAllByRequesterAndRequesteeAndStatus(String requester, String requestee, String status);
}
