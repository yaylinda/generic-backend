package yay.linda.genericbackend.model;

import lombok.Data;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;

@Data
public class FriendRequestDTO {
    private String id;
    private String requester;
    private String requestee;
    private String requestDate;
    private String responseDate;
    private String status;

    private FriendRequestDTO() {

    }

    public static FriendRequestDTO fromFriendRequest(FriendRequest friendRequest) {
        FriendRequestDTO friendRequestDTO = new FriendRequestDTO();
        friendRequestDTO.id = friendRequest.getId();
        friendRequestDTO.requester = friendRequest.getRequester();
        friendRequestDTO.requestee = friendRequest.getRequestee();
        friendRequestDTO.requestDate = SIMPLE_DATE_FORMAT.format(friendRequest.getRequestDate());
        friendRequestDTO.responseDate = friendRequest.getResponseDate() != null ? SIMPLE_DATE_FORMAT.format(friendRequest.getResponseDate()) : null;
        friendRequestDTO.status = friendRequest.getStatus();
        return friendRequestDTO;
    }
}
