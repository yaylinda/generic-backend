package yay.linda.genericbackend.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Session {

    @Id
    private String id;
    private String sessionToken;
    private String username;
    private Date createdDate;

    public Session() {
        this.createdDate = new Date();
    }

    public String getId() {
        return id;
    }

    public Session setId(String id) {
        this.id = id;
        return this;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Session setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Session setUsername(String username) {
        this.username = username;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Session setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }
}
