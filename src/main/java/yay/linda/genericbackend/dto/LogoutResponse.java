package yay.linda.genericbackend.dto;

public class LogoutResponse {

    private LogoutResponseStatus status;
    private String message;
    private LogoutRequest logoutRequest;

    public LogoutResponse() {
    }

    public LogoutResponse(LogoutResponseStatus status, String message, LogoutRequest logoutRequest) {
        this.status = status;
        this.message = message;
        this.logoutRequest = logoutRequest;
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

    public LogoutRequest getLogoutRequest() {
        return logoutRequest;
    }

    public LogoutResponse setLogoutRequest(LogoutRequest logoutRequest) {
        this.logoutRequest = logoutRequest;
        return this;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", logoutRequest=" + logoutRequest +
                '}';
    }
}
