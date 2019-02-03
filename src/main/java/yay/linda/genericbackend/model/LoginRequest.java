package yay.linda.genericbackend.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
