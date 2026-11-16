-- 1. Insert projects only if they do not already exist
INSERT INTO projects (name)
SELECT 'Spring Boot Onboarding'
    WHERE NOT EXISTS (SELECT 1 FROM projects WHERE name = 'Spring Boot Onboarding');

INSERT INTO projects (name)
SELECT 'Task Management API'
    WHERE NOT EXISTS (SELECT 1 FROM projects WHERE name = 'Task Management API');

-- 2. Insert 10 tasks only if the tasks table is currently empty
INSERT INTO tasks (title, description, status, priority, project_id, due_date)
SELECT * FROM (
                  VALUES
                      ('Set up PostgreSQL database', 'Configure local DB and application.yml', 'COMPLETED', 'HIGH',
                       (SELECT id FROM projects WHERE name = 'Spring Boot Onboarding' LIMIT 1), NOW() + INTERVAL '1 day'),
              ('Configure MapStruct', 'Replace manual DTO mapping methods', 'COMPLETED', 'MEDIUM',
            (SELECT id FROM projects WHERE name = 'Spring Boot Onboarding' LIMIT 1), NOW() + INTERVAL '2 days'),
              ('Implement Global Exception Handler', 'Add @RestControllerAdvice for clean errors', 'IN_PROGRESS', 'HIGH',
            (SELECT id FROM projects WHERE name = 'Spring Boot Onboarding' LIMIT 1), NOW() + INTERVAL '3 days'),
              ('Add Request Validation', 'Use @Valid and Jakarta annotations on DTOs', 'PENDING', 'MEDIUM',
            (SELECT id FROM projects WHERE name = 'Spring Boot Onboarding' LIMIT 1), NOW() + INTERVAL '5 days'),
              ('Write Unit Tests for TaskService', 'Cover service logic with JUnit 5 and Mockito', 'PENDING', 'LOW',
            (SELECT id FROM projects WHERE name = 'Spring Boot Onboarding' LIMIT 1), NOW() + INTERVAL '7 days'),
              ('Design REST Endpoints', 'Define GET, POST, PUT, DELETE routes', 'COMPLETED', 'HIGH',
            (SELECT id FROM projects WHERE name = 'Task Management API' LIMIT 1), NOW() + INTERVAL '1 day'),
              ('Add Pagination and Sorting', 'Use Pageable in TaskController', 'IN_PROGRESS', 'MEDIUM',
            (SELECT id FROM projects WHERE name = 'Task Management API' LIMIT 1), NOW() + INTERVAL '4 days'),
              ('Implement Optimistic Locking', 'Add @Version field to Task entity', 'PENDING', 'LOW',
            (SELECT id FROM projects WHERE name = 'Task Management API' LIMIT 1), NOW() + INTERVAL '6 days'),
              ('Configure SLF4J File Logging', 'Verify logs/springboot-onboarding.log output', 'COMPLETED', 'LOW',
            (SELECT id FROM projects WHERE name = 'Task Management API' LIMIT 1), NOW() + INTERVAL '2 days'),
              ('Dockerize Spring Boot App', 'Create Dockerfile and docker-compose.yml', 'PENDING', 'HIGH',
            (SELECT id FROM projects WHERE name = 'Task Management API' LIMIT 1), NOW() + INTERVAL '10 days')
    ) AS seed(title, description, status, priority, project_id, due_date)
WHERE NOT EXISTS (SELECT 1 FROM tasks);
