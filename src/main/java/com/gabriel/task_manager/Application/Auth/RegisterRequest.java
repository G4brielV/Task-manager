package com.gabriel.task_manager.Application.Auth;

public record RegisterRequest(
        String name,
        String email,
        String senha
) {
}
