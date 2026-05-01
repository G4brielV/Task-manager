package com.gabriel.task_manager.Application.Users;

import com.gabriel.task_manager.Application.Auth.RegisterRequest;
import com.gabriel.task_manager.Application.Auth.RegisterResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public User toUser(RegisterRequest dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .senha(dto.senha())
                .build();
    }

    public RegisterResponse toRequestResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}