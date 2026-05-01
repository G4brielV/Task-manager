CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL,
    assignee_id  BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,

    CONSTRAINT fk_task_user FOREIGN KEY (assignee_id ) REFERENCES users(id)
);