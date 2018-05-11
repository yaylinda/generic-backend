package yay.linda.genericbackend.dto;

public class LoginResponse {

    private LoginResponseStatus status;
    private String message;
    private String sessionToken;
    private String username;

    public LoginResponse() {
    }

    public LoginResponse(LoginResponseStatus status, String message, String sessionToken, String username) {
        this.status = status;
        this.message = message;
        this.sessionToken = sessionToken;
        this.username = username;
    }

    public LoginResponseStatus getStatus() {
        return status;
    }

    public LoginResponse setStatus(LoginResponseStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LoginResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public LoginResponse setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public LoginResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
