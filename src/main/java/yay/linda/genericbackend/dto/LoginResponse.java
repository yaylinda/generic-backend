package yay.linda.genericbackend.dto;

public class LoginResponse {

    private LoginResponseStatus status;
    private String message;
    private String sessionToken;
    private LoginRequest loginRequest;

    public LoginResponse() {
    }

    public LoginResponse(LoginResponseStatus status, String message, String sessionToken, LoginRequest loginRequest) {
        this.status = status;
        this.message = message;
        this.sessionToken = sessionToken;
        this.loginRequest = loginRequest;
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

    public LoginRequest getLoginRequest() {
        return loginRequest;
    }

    public LoginResponse setLoginRequest(LoginRequest loginRequest) {
        loginRequest.setPassword("**********");
        this.loginRequest = loginRequest;
        return this;
    }
}
