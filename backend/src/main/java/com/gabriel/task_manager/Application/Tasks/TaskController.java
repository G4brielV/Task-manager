package com.gabriel.task_manager.Application.Tasks;

import com.gabriel.task_manager.Infra.Security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "2. Tasks", description = "Gerenciamento das Tasks")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Listar tarefas do usuário autenticado",
            description = "Retorna uma página de tarefas pertencentes ao usuário logado. " +
                          "Permite filtrar por status e buscar por texto no título ou descrição.",
            parameters = {
                    @Parameter(name = "status",  description = "Filtro por status (TO_DO, IN_PROGRESS, OVERDUE, COMPLETED)"),
                    @Parameter(name = "search",  description = "Busca por texto no título ou descrição"),
                    @Parameter(name = "page",    description = "Número da página (padrão 0)"),
                    @Parameter(name = "size",    description = "Tamanho da página (padrão 10)"),
                    @Parameter(name = "sort",    description = "Ordenação (ex: createdDate,desc)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista paginada de tarefas"),
                    @ApiResponse(responseCode = "401", description = "Não autenticado",               content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 10, sort = "created_date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TaskResponse> page = taskService.listTasks(jwtUserData, status, search, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Buscar tarefa por ID",
            description = "Retorna os dados de uma tarefa específica, desde que pertença ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
                    @ApiResponse(responseCode = "403", description = "Tarefa não pertence ao usuário", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",           content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PathVariable Long id
    ) {
        TaskResponse response = taskService.getTask(jwtUserData, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Criar uma nova tarefa",
            description = "Cria uma tarefa associada ao usuário autenticado. O status inicial é TO_DO, " +
                          "a menos que a data de vencimento já esteja no passado (nesse caso, será OVERDUE).",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Tarefa criada com sucesso",
                            headers = @Header(name = "Location", description = "URI da tarefa criada",
                                    schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @RequestBody @Valid TaskRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        TaskResponse response = taskService.createTask(jwtUserData, request);
        URI location = uriBuilder.path("/tasks/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Editar uma tarefa",
            description = "Atualiza título, descrição e data de vencimento de uma tarefa do usuário autenticado. " +
                          "Se a nova data de vencimento estiver no passado e o status não for COMPLETED, " +
                          "o status será automaticamente definido como OVERDUE.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Tarefa não pertence ao usuário", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",           content = @Content),
                    @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos",      content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PathVariable Long id,
            @RequestBody @Valid TaskRequest request
    ) {
        TaskResponse response = taskService.updateTask(jwtUserData, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Alterar o status de uma tarefa",
            description = "Permite mudar apenas o campo status de uma tarefa pertencente ao usuário autenticado. " +
                          "Transições inválidas retornarão HTTP 400. O status OVERDUE não pode ser definido manualmente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Transição de status inválida",    content = @Content),
                    @ApiResponse(responseCode = "403", description = "Tarefa não pertence ao usuário",  content = @Content),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",           content = @Content),
                    @ApiResponse(responseCode = "422", description = "Status inválido",                 content = @Content)
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PathVariable Long id,
            @RequestBody @Valid TaskStatusRequest request
    ) {
        TaskResponse response = taskService.updateStatus(jwtUserData, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir uma tarefa",
            description = "Remove permanentemente uma tarefa pertencente ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Tarefa não pertence ao usuário", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",           content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PathVariable Long id
    ) {
        taskService.deleteTask(jwtUserData, id);
        return ResponseEntity.noContent().build();
    }
}
