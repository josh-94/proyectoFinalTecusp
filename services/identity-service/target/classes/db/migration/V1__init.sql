-- V1__init.sql: Esquema inicial del servicio de identidad

CREATE TABLE usuarios (
    id              VARCHAR(36)  PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    nombre          VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Tabla de roles por usuario (relación 1-N)
CREATE TABLE usuario_roles (
    usuario_id  VARCHAR(36) NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol         VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, rol)
);

-- Refresh tokens almacenados como hash SHA-256 (64 chars hex)
CREATE TABLE refresh_tokens (
    id          VARCHAR(36) PRIMARY KEY,
    usuario_id  VARCHAR(36) NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    revocado    BOOLEAN     NOT NULL DEFAULT FALSE,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Índices de acceso frecuente
CREATE INDEX idx_refresh_tokens_usuario_id ON refresh_tokens(usuario_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- El usuario admin inicial se crea mediante AdminDataInitializer al arrancar la app.
-- Ver app.admin.* en application.yml para las credenciales por defecto.
