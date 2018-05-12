package yay.linda.genericbackend.model;

public class RegisterResponse {

    private ResponseStatus status;
    private String token;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(ResponseStatus status, String token, String message) {
        this.status = status;
        this.token = token;
        this.message = message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public RegisterResponse setStatus(ResponseStatus status) {
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
