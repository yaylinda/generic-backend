package yay.linda.genericbackend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@Builder
public class FriendRequest {
    @Id
    private String id;
    private String requester;
    private String requestee;
    private Date requestDate;
    private Date responseDate;
    private String status;
}
