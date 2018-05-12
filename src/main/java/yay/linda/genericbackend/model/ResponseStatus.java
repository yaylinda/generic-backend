package yay.linda.genericbackend.model;

public enum ResponseStatus {
    SUCCESS,
    USERNAME_NOT_FOUND,
    EMAIL_NOT_FOUND,
    WRONG_PASSWORD,
    SESSION_TOKEN_NOT_FOUND,
    CREATED,
    USERNAME_TAKEN,
    EMAIL_TAKEN;
}
