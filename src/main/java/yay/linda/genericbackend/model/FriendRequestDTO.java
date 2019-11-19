package yay.linda.genericbackend.model;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;

import static yay.linda.genericbackend.model.Constants.SIMPLE_DATE_FORMAT;

@Data
public class FriendRequestDTO implements Comparable<FriendRequestDTO> {
    private String id;
    private String requester;
    private String requestee;
    private String requestDate;
    private String responseDate;
    private String status;
    private String currentTimestamp;

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
        friendRequestDTO.currentTimestamp = SIMPLE_DATE_FORMAT.format(Date.from(Instant.now()));
        return friendRequestDTO;
    }

    @Override
    public int compareTo(FriendRequestDTO o) {
        String thisLastModDate = StringUtils.isEmpty(this.responseDate) ? this.requestDate : this.responseDate;
        String otherLastModDate = StringUtils.isEmpty(o.getResponseDate()) ? o.getRequestDate() : o.getResponseDate();

        return thisLastModDate.compareTo(otherLastModDate) * -1;
    }
}
