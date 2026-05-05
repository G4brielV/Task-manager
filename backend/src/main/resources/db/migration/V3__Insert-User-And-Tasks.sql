-- Insere o usuário e armazena o ID gerado temporariamente
WITH inserted_user AS (
    INSERT INTO users (name, email, password)
    VALUES (
        'Admin',
        'admin@ex.com',
        '$2a$10$Sh7HKYhLw5sexDBCobqYNOyjLoI0kOqwBBnZASUh1JCGSEkK3Aw/u' -- 123456
    )
    RETURNING id
)
-- Insere as 10 tasks utilizando o ID do usuário recém-criado
INSERT INTO tasks (title, description, status, assignee_id, created_date, due_date)
VALUES
    -- 📌 TO_DO (A Fazer) - Prazos no futuro
    ('Planejar Sprint', 'Definir as metas da próxima semana', 'TO_DO', (SELECT id FROM inserted_user), CURRENT_DATE, CURRENT_DATE + INTERVAL '5 days'),
    ('Estudar SQL', 'Revisar comandos de JOIN e CTEs', 'TO_DO', (SELECT id FROM inserted_user), CURRENT_DATE, CURRENT_DATE + INTERVAL '10 days'),

    -- 📌 IN_PROGRESS (Em Andamento) - Prazos no futuro ou hoje
    ('Desenvolver API', 'Criar endpoints de CRUD de usuários', 'IN_PROGRESS', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '3 days'),
    ('Revisar PRs', 'Fazer code review das entregas da equipe', 'IN_PROGRESS', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '1 day'),
    ('Atualizar documentação', 'Escrever README do projeto', 'IN_PROGRESS', (SELECT id FROM inserted_user), CURRENT_DATE, CURRENT_DATE + INTERVAL '2 days'),

    -- 📌 OVERDUE (Atrasado) - Prazos (due_date) que já passaram
    ('Corrigir Bug Crítico', 'Erro de login em produção', 'OVERDUE', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '5 days'),
    ('Enviar Relatório', 'Relatório mensal de acessos', 'OVERDUE', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE - INTERVAL '2 days'),
    ('Renovar Licença', 'Pagar a licença da IDE', 'OVERDUE', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '1 day'),

    -- 📌 COMPLETED (Concluído) - Prazos geralmente no passado
    ('Configurar Banco', 'Rodar scripts DDL iniciais', 'COMPLETED', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '10 days'),
    ('Reunião de Kick-off', 'Apresentação inicial do projeto', 'COMPLETED', (SELECT id FROM inserted_user), CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '18 days');