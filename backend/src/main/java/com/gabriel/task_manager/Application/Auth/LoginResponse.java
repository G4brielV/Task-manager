package com.gabriel.task_manager.Application.Auth;


import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "Token JWT")
        String token
) {
}
