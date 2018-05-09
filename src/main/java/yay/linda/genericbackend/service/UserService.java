package yay.linda.genericbackend.service;

import org.springframework.stereotype.Service;
import yay.linda.genericbackend.domain.LoginRequest;
import yay.linda.genericbackend.domain.LoginResponse;
import yay.linda.genericbackend.domain.RegisterRequest;
import yay.linda.genericbackend.domain.RegisterResponse;

@Service
public class UserService {

    public RegisterResponse register(RegisterRequest registerRequest) {
        return new RegisterResponse();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        return new LoginResponse();
    }


}
