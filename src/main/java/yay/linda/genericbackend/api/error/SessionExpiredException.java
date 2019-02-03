package yay.linda.genericbackend.api.error;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String sessionToken, String username) {
        super(String.format("Session ('%s') has expired for '%s'", sessionToken, username));
    }
}
