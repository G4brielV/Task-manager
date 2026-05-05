package com.gabriel.task_manager.Application.Tasks;

import com.gabriel.task_manager.Application.Users.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class TaskMapper {

    public Task toTask(TaskRequest dto, User assignee) {
        TaskStatus initialStatus = TaskStatus.TO_DO;

        // If the due date is already in the past, the task starts as OVERDUE
        if (dto.dueDate().isBefore(LocalDate.now())) {
            initialStatus = TaskStatus.OVERDUE;
        }

        return Task.builder()
                .title(dto.title())
                .description(dto.description())
                .status(initialStatus)
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
                .createdDate(task.getCreatedDate())
                .dueDate(task.getDueDate())
                .build();
    }
}
