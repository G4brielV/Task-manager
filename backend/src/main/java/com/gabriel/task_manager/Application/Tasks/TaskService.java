package com.gabriel.task_manager.Application.Tasks;

import com.gabriel.task_manager.Application.Users.User;
import com.gabriel.task_manager.Application.Users.UserRepository;
import com.gabriel.task_manager.Infra.Exception.ForbiddenException;
import com.gabriel.task_manager.Infra.Exception.ResourceNotFoundException;
import com.gabriel.task_manager.Infra.Security.JWTUserData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<TaskResponse> listTasks(JWTUserData jwtUserData, TaskStatus status, String search, Pageable pageable) {
        return taskRepository
                .findAll(TaskSpecification.byFilters(jwtUserData.id(), status, search), pageable)
                .map(TaskMapper::toTaskResponse);
    }

    @Transactional
    public TaskResponse createTask(JWTUserData jwtUserData, TaskRequest request) {
        User assignee = userRepository.findById(jwtUserData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Task task = TaskMapper.toTask(request, assignee);
        taskRepository.save(task);
        return TaskMapper.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(JWTUserData jwtUserData, Long taskId, TaskRequest request) {
        Task task = findTaskOwned(taskId, jwtUserData.id());

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        applyOverdueLogic(task);
        return TaskMapper.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateStatus(JWTUserData jwtUserData, Long taskId, TaskStatusRequest request) {
        Task task = findTaskOwned(taskId, jwtUserData.id());

        // Validate transition via state machine (throws BusinessRuleException on invalid)
        TaskStateMachine.validateTransition(task.getStatus(), request.status(), task.getDueDate());

        task.setStatus(request.status());

        return TaskMapper.toTaskResponse(task);
    }

    @Transactional
    public void deleteTask(JWTUserData jwtUserData, Long taskId) {
        Task task = findTaskOwned(taskId, jwtUserData.id());
        taskRepository.delete(task);
    }

    public TaskResponse getTask(JWTUserData jwtUserData, Long taskId) {
        Task task = findTaskOwned(taskId, jwtUserData.id());
        return TaskMapper.toTaskResponse(task);
    }


    private Task findTaskOwned(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        if (!task.getAssignee().getId().equals(userId)) {
            throw new ForbiddenException("Você não tem permissão para acessar esta tarefa");
        }
        return task;
    }

    /**
     * - dueDate < now AND status ≠ COMPLETED → OVERDUE
     * - era OVERDUE AND dueDate ≥ today → TO_DO
     */
    private void applyOverdueLogic(Task task) {
        boolean isPast = task.getDueDate().isBefore(LocalDate.now());

        if (isPast && task.getStatus() != TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.OVERDUE);
        } else if (!isPast && task.getStatus() == TaskStatus.OVERDUE) {
            task.setStatus(TaskStatus.TO_DO);
        }
    }
}
