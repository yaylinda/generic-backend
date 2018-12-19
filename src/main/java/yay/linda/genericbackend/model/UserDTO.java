package yay.linda.genericbackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String email;
    private String token;
    private String username;
}
