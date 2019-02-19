package yay.linda.genericbackend.model;

import lombok.Data;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;

@Data
public class PlayerActivityDTO {
    private String id;
    private String requester;
    private String requestee;
    private String requestDate;
    private String responseDate;
    private String status;

    private PlayerActivityDTO() {

    }

    public static PlayerActivityDTO fromFriendRequest(FriendRequest friendRequest) {
        PlayerActivityDTO playerActivityDTO = new PlayerActivityDTO();
        playerActivityDTO.id = friendRequest.getId();
        playerActivityDTO.requester = friendRequest.getRequester();
        playerActivityDTO.requestee = friendRequest.getRequestee();
        playerActivityDTO.requestDate = SIMPLE_DATE_FORMAT.format(friendRequest.getRequestDate());
        playerActivityDTO.responseDate = friendRequest.getResponseDate() != null ? SIMPLE_DATE_FORMAT.format(friendRequest.getResponseDate()) : null;
        playerActivityDTO.status = friendRequest.getStatus();
        return playerActivityDTO;
    }
}
