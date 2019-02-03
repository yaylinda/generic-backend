package yay.linda.genericbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private Date createdDate;
    private Date lastLogin;

    public User(RegisterRequest registerRequest) {
        this.username = registerRequest.getUsername();
        this.password = registerRequest.getPassword();
        this.email = registerRequest.getEmail();
        this.createdDate = new Date();
        this.lastLogin = new Date();
    }
}
