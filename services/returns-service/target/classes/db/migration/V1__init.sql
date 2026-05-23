-- V1__init.sql: Esquema inicial del servicio de devoluciones

CREATE TABLE devoluciones (
    id                 VARCHAR(36)  PRIMARY KEY,
    numero_devolucion  VARCHAR(20)  NOT NULL UNIQUE,
    pedido_id          VARCHAR(36)  NOT NULL,
    solicitado_por     VARCHAR(36)  NOT NULL,
    estado             VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
    observaciones      TEXT,
    motivo_rechazo     TEXT,
    creado_en          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    actualizado_en     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE lineas_devolucion (
    id                BIGSERIAL    PRIMARY KEY,
    devolucion_id     VARCHAR(36)  NOT NULL REFERENCES devoluciones(id) ON DELETE CASCADE,
    lote_id           VARCHAR(36)  NOT NULL,
    cantidad_devuelta INT          NOT NULL CHECK (cantidad_devuelta > 0),
    motivo_devolucion TEXT         NOT NULL
);

CREATE TABLE processed_events (
    event_id     VARCHAR(36)  PRIMARY KEY,
    topic        VARCHAR(255) NOT NULL,
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_devoluciones_pedido_id        ON devoluciones(pedido_id);
CREATE INDEX idx_devoluciones_estado           ON devoluciones(estado);
CREATE INDEX idx_lineas_devolucion_devolucion  ON lineas_devolucion(devolucion_id);
