package com.gabriel.task_manager.Infra.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabriel.task_manager.Application.Users.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    @Value("{$api.security.token.secret}")
    private String secret;

    public String gerarToken(User user){
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("TaskManager")
                .withSubject(user.getId().toString())
                .withClaim("userName", user.getName())
                .withIssuedAt(Instant.now())
                .withExpiresAt(dataExpiracao())
                .sign(algorithm);
    }


    private Instant dataExpiracao() {
        return LocalDateTime
                .now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }

    public Optional<JWTUserData> verifyToken(String TokenJWT){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer("TaskManager")
                    .build()
                    .verify(TokenJWT);

            return Optional.of(JWTUserData
                    .builder()
                    .id(Long.valueOf(jwt.getSubject()))
                    .name(jwt.getClaim("userName").asString())
                    .build());

        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }
}

