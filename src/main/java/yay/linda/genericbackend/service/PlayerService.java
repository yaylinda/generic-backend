package yay.linda.genericbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yay.linda.genericbackend.model.PlayerActivityDTO;
import yay.linda.genericbackend.model.PlayerDTO;
import yay.linda.genericbackend.model.RequestFriendDTO;
import yay.linda.genericbackend.model.RespondFriendDTO;
import yay.linda.genericbackend.model.User;
import yay.linda.genericbackend.model.UserDTO;
import yay.linda.genericbackend.repository.FriendRequestRepository;
import yay.linda.genericbackend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public List<PlayerDTO> getAllPlayers(String sessionToken) {
        String username = sessionService.getUsernameFromSessionToken(sessionToken);

        List<User> users = userRepository.findAllOrderByLastActiveDateDesc();
        users = users.stream().filter(u -> !u.getUsername().equals(username)).collect(Collectors.toList());

        return users.stream().map(u -> {
            return new PlayerDTO(u);
        }).collect(Collectors.toList());
    }

    public List<PlayerDTO> getFriends(String sessionToken) {
        return null;
    }


    public List<PlayerActivityDTO> getActivity(String sessionToken) {
        return null;
    }

    public void requestFriend(String sessionToken, RequestFriendDTO requestFriendDTO) {

    }

    public void respondFriend(String sessionToken, RespondFriendDTO respondFriendDTO) {

    }
}
