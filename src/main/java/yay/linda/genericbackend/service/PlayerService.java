package yay.linda.genericbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.model.FriendRequest;
import yay.linda.genericbackend.model.FriendRequestDTO;
import yay.linda.genericbackend.model.FriendRequestStatus;
import yay.linda.genericbackend.model.PlayerDTO;
import yay.linda.genericbackend.model.RequestFriendDTO;
import yay.linda.genericbackend.model.RespondFriendDTO;
import yay.linda.genericbackend.model.User;
import yay.linda.genericbackend.model.UserActivity;
import yay.linda.genericbackend.repository.FriendRequestRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<PlayerDTO> getAllPlayers(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
//        userService.updateActivity(username, UserActivity.GET_PLAYERS_LIST);

        HashSet<String> friends = getFriends(sessionToken).stream()
                .map(PlayerDTO::getUsername)
                .collect(Collectors.toCollection(HashSet::new));

        List<PlayerDTO> otherPlayers = userRepository.findAllByOrderByLastActiveDateDesc().stream()
                .filter(u -> !u.getUsername().equals(username))
                .filter(p -> !friends.contains(p.getUsername()))
                .map(u -> {
                    List<FriendRequest> requests0 = friendRequestRepository
                            .findAllByRequesterAndRequesteeAndStatus(username, u.getUsername(), FriendRequestStatus.REQUESTED.name());
                    List<FriendRequest> requests1 = friendRequestRepository
                            .findAllByRequesterAndRequesteeAndStatus(u.getUsername(), username, FriendRequestStatus.REQUESTED.name());
                    return PlayerDTO.fromUser(u, requests0.isEmpty() && requests1.isEmpty());
                })
                .collect(Collectors.toList());

        LOGGER.info("Other Players: {}", otherPlayers);

        return otherPlayers;
    }

    public List<PlayerDTO> searchPlayersByUsername(String sessionToken, String query) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.SEARCH_FOR_FRIENDS);

        List<User> matching = userRepository.findByUsernameLike(query);
        LOGGER.info("Found {} players with username like '{}'", matching.size(), query);

        HashSet<String> friends = getFriends(sessionToken).stream()
                .map(PlayerDTO::getUsername)
                .collect(Collectors.toCollection(HashSet::new));

        return matching.stream()
                .filter(p -> !p.getUsername().equals(username))
                .filter(p -> !friends.contains(p.getUsername()))
                .map(p -> {
                    List<FriendRequest> requests0 = friendRequestRepository
                            .findAllByRequesterAndRequesteeAndStatus(username, p.getUsername(), FriendRequestStatus.REQUESTED.name());
                    List<FriendRequest> requests1 = friendRequestRepository
                            .findAllByRequesterAndRequesteeAndStatus(p.getUsername(), username, FriendRequestStatus.REQUESTED.name());
                    return PlayerDTO.fromUser(p, requests0.isEmpty() && requests1.isEmpty());
                })
                .collect(Collectors.toList());
    }

    public PlayerDTO getOnePlayer(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.GET_PROFILE_INFO);

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            return PlayerDTO.fromUser(optionalUser.get(), false);
        } else {
            throw new NotFoundException(String.format("Unknown username, '%s'", username));
        }
    }


    public List<PlayerDTO> getFriends(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
//        userService.updateActivity(username, UserActivity.GET_FRIENDS_LIST);

        List<PlayerDTO> friends = new ArrayList<>();

        friends.addAll(friendRequestRepository
                .findAllByRequesterAndStatus(username, FriendRequestStatus.ACCEPTED.name()).stream()
                .map(fr -> userRepository.findByUsername(fr.getRequestee()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(u -> PlayerDTO.fromUser(u, false))
                .collect(Collectors.toList()));

        friends.addAll(friendRequestRepository
                .findAllByRequesteeAndStatus(username, FriendRequestStatus.ACCEPTED.name()).stream()
                .map(fr -> userRepository.findByUsername(fr.getRequester()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(u -> PlayerDTO.fromUser(u, false))
                .collect(Collectors.toList()));

        LOGGER.info("Friends of {}: {}", username, friends);
        return friends;
    }


    public List<FriendRequestDTO> getFriendRequests(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
//        userService.updateActivity(username, UserActivity.GET_FRIEND_REQUEST_LIST);

        List<FriendRequestDTO> friendRequests = new ArrayList<>();

        friendRequests.addAll(friendRequestRepository.findAllByRequester(username).stream()
                .map(FriendRequestDTO::fromFriendRequest)
                .collect(Collectors.toList()));

        friendRequests.addAll(friendRequestRepository.findAllByRequestee(username).stream()
                .map(FriendRequestDTO::fromFriendRequest)
                .collect(Collectors.toList()));

        LOGGER.info("FriendRequests for {}: {}", username, friendRequests);
        return friendRequests;
    }

    public void requestFriend(String sessionToken, RequestFriendDTO requestFriendDTO) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.REQUEST_FRIEND);

        FriendRequest friendRequest = FriendRequest.builder()
                .requester(username)
                .requestee(requestFriendDTO.getRequestee())
                .requestDate(Date.from(Instant.now()))
                .status(FriendRequestStatus.REQUESTED.name())
                .build();

        LOGGER.info("Saving FriendRequest (request): {}", friendRequest);
        friendRequestRepository.save(friendRequest);

        this.messagingTemplate.convertAndSend("/topic/friendRequestReceived/" + requestFriendDTO.getRequestee(), username);
    }

    public void respondFriend(String sessionToken, RespondFriendDTO respondFriendDTO) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);
        LOGGER.info("Obtained username={} from sessionToken", username);
        userService.updateActivity(username, UserActivity.RESPOND_TO_FRIEND_REQUEST);

        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(respondFriendDTO.getRequestId());

        if (!optionalFriendRequest.isPresent()) {
            throw new NotFoundException(String.format("Request with id=%s is not found.", respondFriendDTO.getRequestId()));
        }

        FriendRequest friendRequest = optionalFriendRequest.get();
        friendRequest.setResponseDate(Date.from(Instant.now()));
        friendRequest.setStatus(respondFriendDTO.getIsAccept() ? FriendRequestStatus.ACCEPTED.name() : FriendRequestStatus.DECLINED.name());

        LOGGER.info("Saving FriendRequest (response): {}", friendRequest);
        friendRequestRepository.save(friendRequest);

        this.messagingTemplate.convertAndSend("/topic/friendRequestResponse/" + friendRequest.getRequester(), username);
    }
}
