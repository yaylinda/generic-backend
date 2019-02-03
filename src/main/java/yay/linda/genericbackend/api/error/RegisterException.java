package yay.linda.genericbackend.api.error;

public class RegisterException extends RuntimeException {
    public RegisterException(String message) {
        super(message);
    }
}
