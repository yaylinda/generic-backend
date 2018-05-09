package yay.linda.genericbackend.domain;

public class LoginResponse {

    private LoginResponseStatus status;
    private String message;
    private LoginRequest loginRequest;

    public LoginResponse() {
    }

    public LoginResponse(LoginResponseStatus status, String message, LoginRequest loginRequest) {
        this.status = status;
        this.message = message;
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

    public LoginRequest getLoginRequest() {
        return loginRequest;
    }

    public LoginResponse setLoginRequest(LoginRequest loginRequest) {
        this.loginRequest = loginRequest;
        return this;
    }
}
