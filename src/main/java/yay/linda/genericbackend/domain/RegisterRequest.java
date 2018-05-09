package yay.linda.genericbackend.domain;

public class RegisterRequest {

    private String username;
    private String password;
    private String passwordConf;
    private String email;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String passwordConf, String email) {
        this.username = username;
        this.password = password;
        this.passwordConf = passwordConf;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public RegisterRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPasswordConf() {
        return passwordConf;
    }

    public RegisterRequest setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegisterRequest setEmail(String email) {
        this.email = email;
        return this;
    }
}
