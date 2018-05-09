package yay.linda.genericbackend.domain;

import org.springframework.data.annotation.Id;

public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String sessionToken;


}
