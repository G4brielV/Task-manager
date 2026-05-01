package com.gabriel.task_manager.Application.Auth;

import lombok.Builder;

@Builder
public record RegisterResponse(
        Long id,
        String name,
        String email
) {
}
