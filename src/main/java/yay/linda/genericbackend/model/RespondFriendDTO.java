package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class RespondFriendDTO {
    private String requestId;
    private Boolean isAccept;
}
