package com.gabriel.task_manager.Application.Tasks;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Job Async que roda as 00:00(UTC-3)
 * Qualquer task com status TO_DO ou IN_PROGRESS que due_date é no passado
 * vai ser atualizado para OVERDUE
 */
@Component
@RequiredArgsConstructor
public class OverdueTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueTaskScheduler.class);
    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final TaskRepository taskRepository;

    //@Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    @Scheduled(fixedDelay = 6000)
    @Async
    @Transactional
    public void markOverdueTasks() {
        ZonedDateTime now = ZonedDateTime.now(SAO_PAULO);
        LocalDate today = now.toLocalDate();

        log.info("[OverdueTaskScheduler] Iniciando verificação de tarefas atrasadas. " +
                 "Data referência: {} | Timezone: {} | Thread: {}",
                 today, SAO_PAULO, Thread.currentThread().getName());

        try {
            int updated = taskRepository.markOverdueTasks(today);
            log.info("[OverdueTaskScheduler] Verificação concluída. {} tarefa(s) marcada(s) como OVERDUE.", updated);
        } catch (Exception e) {
            log.error("[OverdueTaskScheduler] Erro ao marcar tarefas atrasadas: {}", e.getMessage(), e);
        }
    }
}
