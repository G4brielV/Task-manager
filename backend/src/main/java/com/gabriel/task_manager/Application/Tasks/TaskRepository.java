package com.gabriel.task_manager.Application.Tasks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndAssigneeId(Long id, Long assigneeId);

    /**
     * Marca taks incompletas com OVEREDUE
     * em casos que due_date ja passou
     *
     * @param today data tual(LocalDate.now())
     * @return quantidade de linhas atualizadas
     */
    @Modifying
    @Query("UPDATE Task t SET t.status = 'OVERDUE' " +
           "WHERE t.status IN ('TO_DO', 'IN_PROGRESS') " +
           "AND t.dueDate < :today")
    int markOverdueTasks(@Param("today") LocalDate today);
}
