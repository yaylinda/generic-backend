package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private Date createdDate;
    private Date lastActiveDate;
    private String lastActivity;
    private Integer numWins;
    private Integer numGames;

    public User(RegisterRequest registerRequest) {
        this.username = registerRequest.getUsername();
        this.password = registerRequest.getPassword();
        this.email = registerRequest.getEmail();
        this.createdDate = new Date();
        this.lastActiveDate = new Date();
        this.numWins = 0;
        this.numGames = 0;
    }
}
