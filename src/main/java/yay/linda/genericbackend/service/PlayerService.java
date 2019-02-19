package yay.linda.genericbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.api.error.NotFoundException;
import yay.linda.genericbackend.model.FriendRequest;
import yay.linda.genericbackend.model.FriendRequestStatus;
import yay.linda.genericbackend.model.PlayerActivityDTO;
import yay.linda.genericbackend.model.PlayerDTO;
import yay.linda.genericbackend.model.RequestFriendDTO;
import yay.linda.genericbackend.model.RespondFriendDTO;
import yay.linda.genericbackend.model.User;
import yay.linda.genericbackend.repository.FriendRequestRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<PlayerDTO> getAllPlayers(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        HashSet<String> friends = getFriends(sessionToken).stream()
                .map(PlayerDTO::getUsername)
                .collect(Collectors.toCollection(HashSet::new));

        return userRepository.findAllByOrderByLastActiveDateDesc().stream()
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
    }

    public List<PlayerDTO> getFriends(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

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

        return friends;
    }


    public List<PlayerActivityDTO> getActivity(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        List<PlayerActivityDTO> activities = new ArrayList<>();

        activities.addAll(friendRequestRepository.findAllByRequester(username).stream()
                .map(PlayerActivityDTO::fromFriendRequest)
                .collect(Collectors.toList()));

        activities.addAll(friendRequestRepository.findAllByRequestee(username).stream()
                .map(PlayerActivityDTO::fromFriendRequest)
                .collect(Collectors.toList()));

        return activities;
    }

    public void requestFriend(String sessionToken, RequestFriendDTO requestFriendDTO) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        FriendRequest friendRequest = FriendRequest.builder()
                .requester(username)
                .requestee(requestFriendDTO.getRequestee())
                .requestDate(new Date())
                .status(FriendRequestStatus.REQUESTED.name())
                .build();

        friendRequestRepository.save(friendRequest);

        this.messagingTemplate.convertAndSend("/topic/friendRequestReceived/" + requestFriendDTO.getRequestee(), username);
    }

    public void respondFriend(String sessionToken, RespondFriendDTO respondFriendDTO) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(respondFriendDTO.getRequestId());

        if (!optionalFriendRequest.isPresent()) {
            throw new NotFoundException(String.format("Request with id=%s is not found.", respondFriendDTO.getRequestId()));
        }

        FriendRequest friendRequest = optionalFriendRequest.get();
        friendRequest.setResponseDate(new Date());
        friendRequest.setStatus(respondFriendDTO.getIsAccept() ? FriendRequestStatus.ACCEPTED.name() : FriendRequestStatus.DECLINED.name());

        friendRequestRepository.save(friendRequest);

        this.messagingTemplate.convertAndSend("/topic/friendRequestResponse/" + friendRequest.getRequestee(), username);
    }
}
