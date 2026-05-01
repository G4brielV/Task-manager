package com.gabriel.task_manager.Application.Tasks;

import com.gabriel.task_manager.Application.Users.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskMapper {

    public Task toTask(TaskRequest dto, User assignee) {
        return Task.builder()
                .title(dto.title())
                .description(dto.description())
                .status(dto.status())
                .assignee(assignee)
                .createdAt(dto.dueDate())
                .build();
    }

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDueDate())
                .build();
    }
}
