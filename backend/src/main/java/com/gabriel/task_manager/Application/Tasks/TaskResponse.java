package com.gabriel.task_manager.Application.Tasks;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Dados de retorno de uma tarefa")
public record TaskResponse(
        @Schema(description = "ID da tarefa", example = "1")
        Long id,

        @Schema(description = "Título da tarefa", example = "Revisar PR do módulo de pagamento")
        String title,

        @Schema(description = "Descrição detalhada", example = "Verificar a implementação do gateway Stripe")
        String description,

        @Schema(description = "Status atual da tarefa", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "ID do usuário responsável", example = "42")
        Long assigneeId,

        @Schema(description = "Data de criação")
        LocalDateTime createdAt,

        @Schema(description = "Data de vencimento")
        LocalDateTime dueDate
) {
}
