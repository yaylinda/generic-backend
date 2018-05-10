package yay.linda.genericbackend.dto;

public class LogoutResponse {

    private LogoutResponseStatus status;
    private String message;
    private String sessionToken;

    public LogoutResponse() {
    }

    public LogoutResponse(LogoutResponseStatus status, String message, String sessionToken) {
        this.status = status;
        this.message = message;
        this.sessionToken = sessionToken;
    }

    public LogoutResponseStatus getStatus() {
        return status;
    }

    public LogoutResponse setStatus(LogoutResponseStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LogoutResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public LogoutResponse setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                '}';
    }
}
