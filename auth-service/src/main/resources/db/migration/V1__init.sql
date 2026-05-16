CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE SCHEMA IF NOT EXISTS auth_data;

-- =========================
-- USERS
-- =========================

CREATE TABLE auth_data.users_auth (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    account_expires_at TIMESTAMP,
    password_changed_at TIMESTAMP
);

-- =========================
-- ROLES
-- =========================

CREATE TABLE auth_data.user_roles (
    id           BIGSERIAL    PRIMARY KEY,
    role_name    VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    description  TEXT
);

-- =========================
-- USERS <-> ROLES
-- =========================

CREATE TABLE auth_data.users_roles (
    user_id UUID NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_users_roles_user
        FOREIGN KEY (user_id)
        REFERENCES auth_data.users_auth(uuid)
        ON DELETE CASCADE,

    CONSTRAINT fk_users_roles_role
        FOREIGN KEY (role_id)
        REFERENCES auth_data.user_roles(id)
        ON DELETE CASCADE
);

-- =========================
-- INDEXES
-- =========================

CREATE INDEX idx_users_username
    ON auth_data.users_auth(username);

CREATE INDEX idx_users_email
    ON auth_data.users_auth(email);

CREATE INDEX idx_roles_name
    ON auth_data.user_roles(role_name);

-- =========================
-- DEFAULT ROLES
-- =========================

INSERT INTO auth_data.user_roles(role_name, display_name)
VALUES
    ('ROLE_USER',  'Пользователь'),
    ('ROLE_ADMIN', 'Администратор');