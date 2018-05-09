package yay.linda.genericbackend.domain;

import org.springframework.data.annotation.Id;
import yay.linda.genericbackend.dto.RegisterRequest;

import java.util.Date;

public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String sessionToken;
    private Date createdDate;

    public User() {
    }

    public User(RegisterRequest registerRequest) {
        this.username = registerRequest.getUsername();
        this.password = registerRequest.getPassword();
        this.email = registerRequest.getEmail();
        this.sessionToken = null;
        this.createdDate = new Date();
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public User setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public User setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }
}
