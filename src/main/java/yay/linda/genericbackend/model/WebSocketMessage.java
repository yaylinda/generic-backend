package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketMessage {
    private String gameId;
    private String username; // the user that did the action to be notified of (not the user to notify)
    private String response; // used in FriendRequest notifications
}
