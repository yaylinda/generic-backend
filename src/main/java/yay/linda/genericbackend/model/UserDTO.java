package yay.linda.genericbackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private String sessionToken;
    private Boolean isGuest;
}
