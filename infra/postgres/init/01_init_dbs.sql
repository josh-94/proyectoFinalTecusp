-- Script de inicialización: crea las bases de datos lógicas por servicio.
-- Solo se usa si se prefiere una única instancia de Postgres (alternativa al Compose con 4 contenedores).
-- En el docker-compose.yml actual cada servicio tiene su propio contenedor, por lo que este
-- script queda como referencia para entornos de CI o despliegues unificados.

CREATE DATABASE identity_db;
CREATE DATABASE inventory_db;
CREATE DATABASE orders_db;
CREATE DATABASE returns_db;
