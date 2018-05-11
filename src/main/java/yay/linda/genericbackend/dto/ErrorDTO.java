package yay.linda.genericbackend.dto;

public class ErrorDTO {

    private ResponseStatus status;
    private String message;

    public ErrorDTO(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public ErrorDTO setStatus(ResponseStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorDTO setMessage(String message) {
        this.message = message;
        return this;
    }
}
