package com.gabriel.task_manager.Application.Auth;

public record LoginRequest(
        String email,
        String senha
) {
}
