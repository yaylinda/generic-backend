package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String username;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
    }
}
