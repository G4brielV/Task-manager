CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL,
    assignee_id  BIGINT,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,

    CONSTRAINT fk_task_user FOREIGN KEY (assignee_id ) REFERENCES users(id)
);