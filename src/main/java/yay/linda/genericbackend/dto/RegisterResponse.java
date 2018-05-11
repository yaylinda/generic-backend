package yay.linda.genericbackend.dto;

public class RegisterResponse {

    private RegisterResponseStatus status;
    private String token;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(RegisterResponseStatus status, String token, String message) {
        this.status = status;
        this.token = token;
        this.message = message;
    }

    public RegisterResponseStatus getStatus() {
        return status;
    }

    public RegisterResponse setStatus(RegisterResponseStatus status) {
        this.status = status;
        return this;
    }

    public String getToken() {
        return token;
    }

    public RegisterResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RegisterResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
