package yay.linda.genericbackend.dto;

public class RegisterResponse {

    private RegisterResponseStatus status;
    private String message;
    private RegisterRequest registerRequest;

    public RegisterResponse() {
    }

    public RegisterResponse(RegisterResponseStatus status, String message, RegisterRequest registerRequest) {
        this.status = status;
        this.message = message;
        this.registerRequest = registerRequest;
    }

    public RegisterResponseStatus getStatus() {
        return status;
    }

    public RegisterResponse setStatus(RegisterResponseStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RegisterResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public RegisterRequest getRegisterRequest() {
        return registerRequest;
    }

    public RegisterResponse setRegisterRequest(RegisterRequest registerRequest) {
        registerRequest.setPassword("**********");
        this.registerRequest = registerRequest;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", registerRequest=" + registerRequest +
                '}';
    }
}
