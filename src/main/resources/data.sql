
-- =====================================================
-- 1. CREATE DUMMY USER
-- =====================================================

INSERT INTO users (
    id,
    email,
    first_name,
    last_name,
    status
)
VALUES (
           '11111111-1111-4111-8111-111111111111',
           'ramesh@example.com',
           'Ramesh',
           'Tiwari',
           'ACTIVE'
       );


-- =====================================================
-- 2. CREATE PROJECT
-- =====================================================

INSERT INTO projects (
    id,
    name,
    created_by
)
VALUES (
           '22222222-2222-4222-8222-222222222222',
           'Spring Boot Task Management',
           '11111111-1111-4111-8111-111111111111'
       );


-- =====================================================
-- 3. ADD PROJECT OWNER
-- =====================================================

INSERT INTO project_members (
    project_id,
    user_id,
    role
)
VALUES (
           '22222222-2222-4222-8222-222222222222',
           '11111111-1111-4111-8111-111111111111',
           'OWNER'
       );


-- =====================================================
-- 4. CREATE FIVE DUMMY TASKS
-- =====================================================

INSERT INTO tasks (
    title,
    description,
    status,
    priority,
    project_id,
    created_by,
    assigned_to,
    due_date
)
VALUES
    (
        'Configure Spring Security',
        'Set up SecurityFilterChain and configure public and protected endpoints.',
        'COMPLETED',
        'HIGH',
        '22222222-2222-4222-8222-222222222222',
        '11111111-1111-4111-8111-111111111111',
        '11111111-1111-4111-8111-111111111111',
        CURRENT_TIMESTAMP + INTERVAL '1 day'
    ),
    (
        'Implement User Registration',
        'Create signup API with email validation and secure password hashing.',
        'IN_PROGRESS',
        'HIGH',
        '22222222-2222-4222-8222-222222222222',
        '11111111-1111-4111-8111-111111111111',
        '11111111-1111-4111-8111-111111111111',
        CURRENT_TIMESTAMP + INTERVAL '3 days'
    ),
    (
        'Implement JWT Authentication',
        'Generate short-lived JWT access tokens and validate incoming bearer tokens.',
        'PENDING',
        'HIGH',
        '22222222-2222-4222-8222-222222222222',
        '11111111-1111-4111-8111-111111111111',
        NULL,
        CURRENT_TIMESTAMP + INTERVAL '5 days'
    ),
    (
        'Create Refresh Token API',
        'Implement refresh token storage, rotation, expiration and revocation.',
        'PENDING',
        'MEDIUM',
        '22222222-2222-4222-8222-222222222222',
        '11111111-1111-4111-8111-111111111111',
        NULL,
        CURRENT_TIMESTAMP + INTERVAL '7 days'
    ),
    (
        'Write Integration Tests',
        'Test registration, login, authentication and protected REST endpoints.',
        'PENDING',
        'LOW',
        '22222222-2222-4222-8222-222222222222',
        '11111111-1111-4111-8111-111111111111',
        NULL,
        CURRENT_TIMESTAMP + INTERVAL '10 days'
    );
