package com.gabriel.task_manager.Infra.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Configuration
public class SchedulingConfig implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfig.class);


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.setErrorHandler(t ->
                log.error("[Scheduler] Erro não tratado em tarefa agendada: {}", t.getMessage(), t)
        );
        scheduler.initialize();
        taskRegistrar.setTaskScheduler(scheduler);

        log.info("[SchedulingConfig] TaskScheduler configurado com pool de 2 threads.");
    }

    @Bean(name = "taskExecutor")
    public Executor asyncExecutor() {
        return Executors.newCachedThreadPool();
    }
}
