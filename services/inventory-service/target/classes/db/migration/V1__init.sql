-- V1__init.sql: Esquema inicial del servicio de inventario médico

CREATE TABLE productos (
    id              VARCHAR(36)  PRIMARY KEY,
    sku             VARCHAR(50)  NOT NULL UNIQUE,
    nombre          VARCHAR(255) NOT NULL,
    descripcion     TEXT,
    unidad_medida   VARCHAR(20),
    stock_minimo    INT          NOT NULL DEFAULT 0,
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE lotes (
    id                   VARCHAR(36) PRIMARY KEY,
    producto_id          VARCHAR(36) NOT NULL REFERENCES productos(id),
    numero_lote          VARCHAR(50) NOT NULL,
    fecha_vencimiento    DATE        NOT NULL,
    cantidad_disponible  INT         NOT NULL DEFAULT 0,
    creado_en            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    actualizado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT lotes_cantidad_positiva CHECK (cantidad_disponible >= 0)
);

CREATE TABLE movimientos_stock (
    id                 VARCHAR(36)  PRIMARY KEY,
    lote_id            VARCHAR(36)  NOT NULL REFERENCES lotes(id),
    tipo               VARCHAR(20)  NOT NULL,
    cantidad           INT          NOT NULL,
    referencia_externa VARCHAR(100),
    creado_por         VARCHAR(100) NOT NULL,
    creado_en          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Tabla de idempotencia para consumidores Kafka (ADR-009)
CREATE TABLE processed_events (
    event_id      VARCHAR(36) PRIMARY KEY,
    processed_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Índices de consulta frecuente
CREATE INDEX idx_lotes_producto_id        ON lotes(producto_id);
CREATE INDEX idx_lotes_fecha_vencimiento  ON lotes(fecha_vencimiento);
CREATE INDEX idx_movimientos_lote_id      ON movimientos_stock(lote_id);
CREATE INDEX idx_movimientos_creado_en    ON movimientos_stock(creado_en);
