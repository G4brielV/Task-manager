package com.gabriel.task_manager.Application.Tasks;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para alteração do status de uma tarefa")
public record TaskStatusRequest(

        @NotNull(message = "O status é obrigatório")
        @Schema(description = "Novo status", example = "IN_PROGRESS")
        TaskStatus status
) {
}
