package yay.linda.genericbackend.dto;

public class LogoutRequest {

    private String sessionToken;

    public LogoutRequest() {
    }

    public LogoutRequest(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public LogoutRequest setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "sessionToken='" + sessionToken + '\'' +
                '}';
    }
}
