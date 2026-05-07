package com.gabriel.task_manager.Application.Task;

import com.gabriel.task_manager.Application.Tasks.*;
import com.gabriel.task_manager.Application.Users.User;
import com.gabriel.task_manager.Application.Users.UserRepository;
import com.gabriel.task_manager.Infra.Exception.ForbiddenException;
import com.gabriel.task_manager.Infra.Security.JWTUserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve ver uma task com sucesso (autorizado)")
    void solicitarTaskPorId_DeveRetornarResponse_QuandoSucesso() {

        /*Preparação*/
        Long userId = 10L;
        Long taskId = 1L;

        JWTUserData jwtUserData = new JWTUserData(userId, "Lira");

        User user = User.builder().id(userId).name("Lira").build();
        Task task = Task.builder()
                .id(taskId)
                .assignee(user)
                .status(TaskStatus.TO_DO)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.ofNullable(task));

        /*Ação e verificação*/
        TaskResponse response = taskService.getTask(jwtUserData, taskId);

        assertNotNull(response);
        assertEquals(taskId, response.id());
        assertEquals(TaskStatus.TO_DO, response.status());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando tentar ver task de outro usuario")
    void solicitarTaskPorId_DeveFalhar_QuandoTaskNaoForDoUsuario() {

        /*Preparação*/
        Long userId = 10L;
        Long hackerID = 11L;
        Long taskId = 1L;


        JWTUserData jwtUserData = new JWTUserData(hackerID, "Hacker");

        User user = User.builder().id(userId).name("Lira").build();
        Task task = Task.builder()
                .id(taskId)
                .assignee(user)
                .status(TaskStatus.TO_DO)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.ofNullable(task));

        /*Ação e verificação*/
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            taskService.getTask(jwtUserData, taskId);
        });

        // Validamos que ele foi barrado com a mensagem exata
        assertEquals("Você não tem permissão para acessar esta tarefa", exception.getMessage());
    }

}
