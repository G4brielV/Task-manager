package com.gabriel.task_manager.Application.Auth;

import com.gabriel.task_manager.Application.Users.User;
import com.gabriel.task_manager.Application.Users.UserMapper;
import com.gabriel.task_manager.Application.Users.UserRepository;
import com.gabriel.task_manager.Infra.Exception.BusinessRuleException;
import com.gabriel.task_manager.Infra.Security.MyPasswordEncoder;
import com.gabriel.task_manager.Infra.Security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final MyPasswordEncoder passwordEncoder;


    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        Authentication autenticacao = authenticationManager.authenticate(token);
        User user = (User) autenticacao.getPrincipal();
        String tokenJWT = tokenService.gerarToken(user);
        return new LoginResponse(tokenJWT);
    }

    @Transactional
    public RegisterResponse register(@RequestBody @Valid RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new BusinessRuleException("Já existe um usuário cadastrado com este login.");
        }
        User user = UserMapper.toUser(registerRequest);
        String senhaCripto = passwordEncoder.encryptPassword(user.getPassword());
        user.atualizarSenha(senhaCripto);
        userRepository.save(user);

        return UserMapper.toRequestResponse(user);
    }
}
