

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_user_status
                           CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE UNIQUE INDEX uq_users_email
    ON users (LOWER(email));



CREATE TABLE user_identities (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 provider VARCHAR(30) NOT NULL,
                                 issuer VARCHAR(500) NOT NULL,
                                 subject VARCHAR(255) NOT NULL,
                                 password_hash VARCHAR(255),
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT uq_identity_issuer_subject
                                     UNIQUE (issuer, subject),

                                 CONSTRAINT chk_identity_provider
                                     CHECK (provider IN ('LOCAL', 'KEYCLOAK')),

                                 CONSTRAINT chk_identity_password
                                     CHECK (
                                         (provider = 'LOCAL' AND password_hash IS NOT NULL)
                                             OR
                                         (provider = 'KEYCLOAK' AND password_hash IS NULL)
                                         )
);

CREATE INDEX idx_user_identities_user_id
    ON user_identities(user_id);




ALTER TABLE projects
    ADD COLUMN id_uuid UUID DEFAULT gen_random_uuid();

ALTER TABLE projects
    ALTER COLUMN id_uuid SET NOT NULL;




ALTER TABLE tasks
    ADD COLUMN id_uuid UUID DEFAULT gen_random_uuid();

ALTER TABLE tasks
    ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE tasks
    ADD COLUMN project_id_uuid UUID;

UPDATE tasks t
SET project_id_uuid = p.id_uuid
    FROM projects p
WHERE t.project_id = p.id;

ALTER TABLE tasks
    ALTER COLUMN project_id_uuid SET NOT NULL;




ALTER TABLE tasks
DROP CONSTRAINT fk_tasks_project;

ALTER TABLE tasks
DROP CONSTRAINT tasks_pkey;

ALTER TABLE projects
DROP CONSTRAINT projects_pkey;




ALTER TABLE tasks
DROP COLUMN id;

ALTER TABLE tasks
DROP COLUMN project_id;

ALTER TABLE projects
DROP COLUMN id;




ALTER TABLE projects
    RENAME COLUMN id_uuid TO id;

ALTER TABLE tasks
    RENAME COLUMN id_uuid TO id;

ALTER TABLE tasks
    RENAME COLUMN project_id_uuid TO project_id;




ALTER TABLE projects
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE tasks
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);

ALTER TABLE tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id)
            REFERENCES projects(id)
            ON DELETE CASCADE;

CREATE INDEX idx_tasks_project_id
    ON tasks(project_id);



ALTER TABLE projects
    ADD COLUMN created_by UUID REFERENCES users(id);




CREATE TABLE project_members (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                 project_id UUID NOT NULL
                                     REFERENCES projects(id) ON DELETE CASCADE,

                                 user_id UUID NOT NULL
                                     REFERENCES users(id) ON DELETE RESTRICT,

                                 role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',

                                 joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT uq_project_member
                                     UNIQUE (project_id, user_id),

                                 CONSTRAINT chk_project_member_role
                                     CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'))
);

CREATE INDEX idx_project_members_user_id
    ON project_members(user_id);


-- Only one OWNER per project
CREATE UNIQUE INDEX uq_project_single_owner
    ON project_members(project_id)
    WHERE role = 'OWNER';




ALTER TABLE tasks
    ADD COLUMN created_by UUID,
    ADD COLUMN assigned_to UUID;


-- Both creator and assignee must belong to the task's project.

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_creator_membership
        FOREIGN KEY (project_id, created_by)
            REFERENCES project_members(project_id, user_id);

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_assignee_membership
        FOREIGN KEY (project_id, assigned_to)
            REFERENCES project_members(project_id, user_id);

CREATE INDEX idx_tasks_assigned_to
    ON tasks(assigned_to);
