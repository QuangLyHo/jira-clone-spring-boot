-- Team/workspace scoping. Existing projects/tasks are test data with no
-- team to belong to, so this clears them rather than backfilling a
-- placeholder team.
DELETE FROM task_assignees;
DELETE FROM tasks;
DELETE FROM projects;

CREATE TABLE teams (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE team_members (
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (team_id, user_id),
    KEY fk_team_members_user (user_id),
    CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

ALTER TABLE projects
    ADD COLUMN team_id BIGINT NOT NULL,
    ADD KEY fk_projects_team (team_id),
    ADD CONSTRAINT fk_projects_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE;
