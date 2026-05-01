package com.gabriel.task_manager.Application.Tasks;

import com.gabriel.task_manager.Application.Users.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskMapper {

    public Task toTask(TaskRequest dto, User assignee) {
        return Task.builder()
                .title(dto.title())
                .description(dto.description())
                .status(TaskStatus.TO_DO)
                .assignee(assignee)
                .dueDate(dto.dueDate())
                .build();
    }

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDueDate())
                .build();
    }
}
