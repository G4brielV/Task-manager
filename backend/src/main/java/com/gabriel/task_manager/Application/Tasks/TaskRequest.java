package com.gabriel.task_manager.Application.Tasks;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Payload para criação ou edição de uma tarefa")
public record TaskRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
        @Schema(description = "Título da tarefa", example = "Revisar PR do módulo de pagamento")
        String title,

        @Schema(description = "Descrição detalhada da tarefa", example = "Verificar a implementação do gateway Stripe")
        String description,

        @NotNull(message = "A data de vencimento é obrigatória")
        @Schema(description = "Data de vencimento (formato: yyyy-MM-dd)", example = "2025-12-31")
        LocalDate dueDate
) {
}
