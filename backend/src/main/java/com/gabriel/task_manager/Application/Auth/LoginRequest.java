package com.gabriel.task_manager.Application.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Schema(description = "Email cadastrado", example = "admin@ex.com")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Schema(description = "Senha cadastrada", example = "123456")
        String password
) {
}
