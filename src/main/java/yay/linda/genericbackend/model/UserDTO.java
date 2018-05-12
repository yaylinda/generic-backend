package yay.linda.genericbackend.model;

public class UserDTO {

    private String email;
    private String token;
    private String username;

    public UserDTO() {
    }

    public UserDTO(String email, String token, String username) {
        this.email = email;
        this.token = token;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public UserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getToken() {
        return token;
    }

    public UserDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserDTO setUsername(String username) {
        this.username = username;
        return this;
    }
}
