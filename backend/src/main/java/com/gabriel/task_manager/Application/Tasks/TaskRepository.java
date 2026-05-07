package com.gabriel.task_manager.Application.Tasks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(nativeQuery = true,
            value = "SELECT * FROM tasks t WHERE t.assignee_id = :userId " +
                    "AND (CAST(:status AS text) IS NULL OR t.status = CAST(:status AS text)) " +
                    "AND (CAST(:search AS text) IS NULL OR TRIM(CAST(:search AS text)) = '' OR t.search_vector @@ websearch_to_tsquery('portuguese', CAST(:search AS text)))",
            countQuery = "SELECT count(*) FROM tasks t WHERE t.assignee_id = :userId " +
                    "AND (CAST(:status AS text) IS NULL OR t.status = CAST(:status AS text)) " +
                    "AND (CAST(:search AS text) IS NULL OR TRIM(CAST(:search AS text)) = '' OR t.search_vector @@ websearch_to_tsquery('portuguese', CAST(:search AS text)))")
    Page<Task> findTasksByFilters(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("search") String search,
            Pageable pageable
    );

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
