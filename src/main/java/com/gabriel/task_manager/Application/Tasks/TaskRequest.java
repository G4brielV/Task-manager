package com.gabriel.task_manager.Application.Tasks;

import java.time.LocalDateTime;

public record TaskRequest(
        String title,
        String description,
        TaskStatus status,
        Long assigneeId,
        LocalDateTime dueDate
) {
}
