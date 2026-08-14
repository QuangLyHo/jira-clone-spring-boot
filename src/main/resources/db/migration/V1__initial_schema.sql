CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(255) DEFAULT NULL,
    last_name VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (email)
) ENGINE=InnoDB;

CREATE TABLE projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    status ENUM('todo', 'in_progress', 'done') DEFAULT 'todo',
    project_id BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_junction_projects (project_id),
    CONSTRAINT fk_junction_projects FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE task_assignees (
    task_id BIGINT NOT NULL,
    assignee_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, assignee_id),
    KEY fk_junction_user (assignee_id),
    CONSTRAINT fk_junction_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_junction_user FOREIGN KEY (assignee_id) REFERENCES users (id)
) ENGINE=InnoDB;



