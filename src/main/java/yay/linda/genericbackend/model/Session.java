package yay.linda.genericbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
public class Session {

    @Id
    private String id;
    private String sessionToken;
    private String username;
    private Date createdDate;
    private Boolean isActive;

    public Session(String username) {
        this.sessionToken = UUID.randomUUID().toString();
        this.username = username;
        this.createdDate = Date.from(Instant.now());
        this.isActive = true;
    }
}
