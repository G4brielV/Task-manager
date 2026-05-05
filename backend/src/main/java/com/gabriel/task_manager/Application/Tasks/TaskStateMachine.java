package com.gabriel.task_manager.Application.Tasks;

import com.gabriel.task_manager.Infra.Exception.BusinessRuleException;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * TO_DO        → IN_PROGRESS, COMPLETED
 * IN_PROGRESS  → TO_DO, COMPLETED
 * OVERDUE      → COMPLETED
 *                      → TO_DO, IN_PROGRESS  (only if dueDate ≥ hoje)
 * COMPLETED    → TO_DO, IN_PROGRESS
 *
 * OVERDUE não é selecionado pelo ususário e sim pelo sistema
 */
@UtilityClass
public class TaskStateMachine {

    private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED = Map.of(
            TaskStatus.TO_DO,       Set.of(TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED),
            TaskStatus.IN_PROGRESS, Set.of(TaskStatus.TO_DO, TaskStatus.COMPLETED),
            TaskStatus.COMPLETED,   Set.of(TaskStatus.TO_DO, TaskStatus.IN_PROGRESS),
            TaskStatus.OVERDUE,     Set.of(TaskStatus.COMPLETED)
    );

    public void validateTransition(TaskStatus currentStatus, TaskStatus newStatus, LocalDate dueDate) {
        // OVERDUE não é selecionado pelo usuario e sim pelo sistema
        if (newStatus == TaskStatus.OVERDUE) {
            throw new BusinessRuleException(
                    "O status OVERDUE não pode ser definido manualmente. " +
                    "Ele é atribuído automaticamente pelo sistema quando a data de vencimento é ultrapassada."
            );
        }

        if (currentStatus == newStatus) {
            return;
        }

        Set<TaskStatus> allowed = ALLOWED.get(currentStatus);
        if (allowed != null && allowed.contains(newStatus)) {
            return;
        }

        if (currentStatus == TaskStatus.OVERDUE
                && (newStatus == TaskStatus.TO_DO || newStatus == TaskStatus.IN_PROGRESS)) {
            if (dueDate != null && !dueDate.isBefore(LocalDate.now())) {
                return; // due date was moved to future, allow
            }
            throw new BusinessRuleException(
                    "Tarefas atrasadas só podem voltar para TO_DO ou IN_PROGRESS " +
                    "se a data de vencimento for atualizada para uma data futura."
            );
        }

        throw new BusinessRuleException(
                String.format("Transição de status inválida: %s → %s", currentStatus, newStatus)
        );
    }
}
