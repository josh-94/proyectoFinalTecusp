-- V1__init.sql: Esquema inicial del servicio de pedidos

CREATE TABLE pedidos (
    id               VARCHAR(36)  PRIMARY KEY,
    numero_pedido    VARCHAR(20)  NOT NULL UNIQUE,
    solicitado_por   VARCHAR(36)  NOT NULL,
    hospital_destino VARCHAR(255) NOT NULL,
    estado           VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE_STOCK',
    motivo_rechazo   TEXT,
    creado_en        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    actualizado_en   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE lineas_pedido (
    id          BIGSERIAL    PRIMARY KEY,
    pedido_id   VARCHAR(36)  NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    lote_id     VARCHAR(36)  NOT NULL,
    cantidad    INT          NOT NULL CHECK (cantidad > 0),
    descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE processed_events (
    event_id     VARCHAR(36)  PRIMARY KEY,
    topic        VARCHAR(255) NOT NULL,
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pedidos_solicitado_por  ON pedidos(solicitado_por);
CREATE INDEX idx_pedidos_estado          ON pedidos(estado);
CREATE INDEX idx_lineas_pedido_pedido_id ON lineas_pedido(pedido_id);
