package com.gabriel.task_manager.Application.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.task_manager.Application.Tasks.Task;
import com.gabriel.task_manager.Application.Tasks.TaskRepository;
import com.gabriel.task_manager.Application.Tasks.TaskStatus;
import com.gabriel.task_manager.Application.Tasks.TaskStatusRequest;
import com.gabriel.task_manager.Application.Users.User;
import com.gabriel.task_manager.Application.Users.UserRepository;
import com.gabriel.task_manager.Infra.Security.TokenService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc // Chamadas HTTP
@ActiveProfiles("test") // Ler o application-test.yaml
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class TaskControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Para converter objetos Java em JSON
    private final TokenService tokenServicee;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    // Variáveis para guardar os dados criados no banco e usar nos testes
    private User user;
    private User hacker;
    private String tokenUser;
    private String tokenHacker;

    @BeforeEach
    void setUpGlobal() {
        // Limpa as tabelas transacionais
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // Criando os Usuários
        user = criarSalvarUsuario("user", "user@email.com");
        hacker = criarSalvarUsuario("hacker", "hacker@email.com");

        // Tokens JWT do user e hacker
        tokenUser = tokenServicee.gerarToken(user);
        tokenHacker = tokenServicee.gerarToken(hacker);
    }

    private User criarSalvarUsuario(String name, String email) {
        User u = User.builder()
                .name(name).email(email).password("senha123").build();
        return userRepository.save(u);
    }

    private Task criarSalvarTask(String title, String description, TaskStatus status, User user, LocalDate dueDate) {
        Task t = Task.builder()
                .title(title).description(description).status(status).assignee(user).dueDate(dueDate).build();
        return taskRepository.save(t);
    }

    @Nested
    @DisplayName("PATCH /tasks/{id}/status")
    class PatchStatsTask {

        private Task task1;

        @BeforeEach
        void setUp() {
            task1 = criarSalvarTask("Titulo", "Descricao", TaskStatus.OVERDUE, user,
                    LocalDate.of(2021, 5, 10));
        }

        @Test
        @DisplayName("Deve aprovar mudança de status para COMPLETED")
        void concluirTask_ComSucesso_EVerificarBanco() throws Exception {

            TaskStatusRequest request = new TaskStatusRequest(TaskStatus.COMPLETED);
            String jsonBody = objectMapper.writeValueAsString(request);

            // O Dono faz a requisição PATCH para aprovar a adoção 1
            mockMvc.perform(patch("/tasks/" + task1.getId() + "/status")
                            .header("Authorization", "Bearer " + tokenUser)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    // Valida o retorno HTTP
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            /*Validar o Banco de Dados*/
            Task taskdb = taskRepository.findById(task1.getId()).get();
            assertThat(taskdb.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        }

        @Test
        @DisplayName("Deve rejeitar mudança de status para TO_DO quando data de vencimento está no passado")
        void changeToTO_DO_Task_ComBusinessException() throws Exception {

            TaskStatusRequest request = new TaskStatusRequest(TaskStatus.TO_DO);
            String jsonBody = objectMapper.writeValueAsString(request);

            // O Dono faz a requisição PATCH para aprovar a adoção 1
            mockMvc.perform(patch("/tasks/" + task1.getId() + "/status")
                            .header("Authorization", "Bearer " + tokenUser)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    // Valida o retorno HTTP
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Tarefas com data de vencimento no passado não podem ser movidas para TO_DO ou IN_PROGRESS. Atualize a data de vencimento para uma data futura primeiro."));
        }
    }
}
