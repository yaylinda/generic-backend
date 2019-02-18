package yay.linda.genericbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class FriendRequest {
    @Id
    private String id;
    private String requester;
    private String requestee;
    private String requestDate;
    private String responseDate;
    private FriendRequestStatus status;
}
