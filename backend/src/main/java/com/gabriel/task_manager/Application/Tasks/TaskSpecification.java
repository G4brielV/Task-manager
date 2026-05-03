package com.gabriel.task_manager.Application.Tasks;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    public static Specification<Task> byFilters(Long userId, TaskStatus status, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Sempre filtra pelo dono da tarefa
            predicates.add(cb.equal(root.get("assignee").get("id"), userId));

            // Filtro por status (opcional)
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Busca por texto em título ou descrição (opcional)
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch  = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleMatch, descMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
