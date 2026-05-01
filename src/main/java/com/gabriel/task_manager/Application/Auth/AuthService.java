package com.gabriel.task_manager.Application.Auth;

import com.gabriel.task_manager.Application.Users.User;
import com.gabriel.task_manager.Application.Users.UserMapper;
import com.gabriel.task_manager.Application.Users.UserRepository;
import com.gabriel.task_manager.Infra.Exception.BusinessRuleException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
/*    private final AuthenticationManager authenticationManager;
    private final TokenJWTService tokenJWTService;
    private final PasswordEncryptService passwordEncryptService;
    */

    public LoginResponse login(@Valid LoginRequest loginRequest) {
        String tokenJWT = "Token Mockado, email: " + loginRequest.email();
        return new LoginResponse(tokenJWT);
    }

    @Transactional
    public RegisterResponse register(@Valid RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new BusinessRuleException("Já existe um usuário cadastrado com este login.");
        }
        User user = UserMapper.toUser(registerRequest);
        userRepository.save(user);

        return UserMapper.toRequestResponse(user);
    }
}
