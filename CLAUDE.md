# Proyecto: Sistema Distribuido de Gestión de Inventario Médico y Devoluciones

> Curso: Arquitectura de Software · Grupo 10 · Trabajo Individual · Tipo A
> Este archivo es leído automáticamente por Claude Code al abrir cualquier sesión en el repo.

## Objetivo

Plataforma distribuida para gestionar inventario médico hospitalario, pedidos y devoluciones parciales de instrumental. Microservicios desacoplados, comunicación REST + eventos Kafka, autenticación JWT con RBAC.

## Stack

- **Backend**: Java 21 + Spring Boot 3.x (Web, Data JPA, Security, Validation, Actuator), Spring Cloud Gateway, Flyway.
- **Mensajería**: Apache Kafka (Confluent images en Compose).
- **Base de datos**: PostgreSQL 16, una instancia lógica por microservicio.
- **Cliente**: Flutter 3.x (Android + Web inicial), `dio` para HTTP, Riverpod para estado.
- **Build**: Maven (multi-módulo) en backend, `flutter pub` en frontend.
- **Contenedores**: Docker + Docker Compose para desarrollo local.
- **Testing**: JUnit 5, Mockito, Testcontainers (PostgreSQL + Kafka), REST Assured. Para Flutter: `flutter_test` y `mocktail`.

## Arquitectura

- **Estilo**: Microservicios + Event-Driven + Clean Architecture + Hexagonal.
- **Servicios**: `identity-service`, `inventory-service`, `orders-service`, `returns-service`, `api-gateway`.
- **Comunicación**:
  - REST síncrono con OpenAPI (springdoc-openapi) cuando se necesita respuesta inmediata.
  - Kafka asíncrono para eventos de dominio (ver `docs/eventos.md`).
- Ver `docs/ADRs/` para todas las decisiones arquitectónicas.

## Estructura del repo

```
.
├── services/
│   ├── identity-service/
│   ├── inventory-service/
│   ├── orders-service/
│   ├── returns-service/
│   └── api-gateway/
├── mobile-app/                 # Flutter
├── infra/
│   ├── docker-compose.yml
│   ├── kafka/
│   └── postgres/init/
├── docs/
│   ├── ADRs/                   # ADR-001.md, ADR-002.md, ...
│   ├── arquitectura.md         # Documento principal (export a docx)
│   ├── eventos.md
│   └── diagramas/              # *.puml y exportados *.png
└── CLAUDE.md
```

## Convenciones de código (backend)

- Estructura interna por servicio (hexagonal): `domain/`, `application/` (con `port/in`, `port/out`, `usecase`), `infrastructure/` (con `adapter/in/rest`, `adapter/in/messaging`, `adapter/out/persistence`, `adapter/out/messaging`).
- El paquete `domain` **no** debe importar nada de Spring, JPA ni Kafka.
- DTOs de REST viven en `adapter/in/rest/dto`, distintos de las entidades JPA en `adapter/out/persistence/entity`.
- Mappers explícitos (sin reflexión cuando se puede), idealmente MapStruct.
- Anotaciones de seguridad con `@PreAuthorize("hasRole('...')")` en los handlers.
- Nombres de tests: `MétodoBajoPrueba_should_Comportamiento_when_Condición`.

## Convenciones de eventos Kafka

- Topic: `<dominio>.<entidad>.<accion-pasado>` (ej: `orders.pedido.creado`).
- Envelope JSON: `{ eventId, eventType, occurredAt, producer, version, data }`.
- Cada consumidor persiste `eventId` procesados (tabla `processed_events`) para idempotencia.
- Particiones: clave = `aggregateId` para preservar orden por agregado.

## Comandos comunes

```bash
# Levantar todo el entorno local
docker compose -f infra/docker-compose.yml up -d

# Compilar todos los servicios
mvn -T 1C clean install

# Levantar un servicio concreto en modo dev
cd services/inventory-service && mvn spring-boot:run

# Tests
mvn test

# Generar OpenAPI doc en build time (springdoc)
# Disponible en runtime: http://localhost:8081/swagger-ui.html

# App Flutter
cd mobile-app && flutter run -d chrome
```

## Reglas para Claude

1. **Antes de crear archivos nuevos**, revisa la estructura propuesta en este archivo y respétala.
2. **Nunca pongas lógica de Spring/JPA/Kafka en `domain/`**. Si dudas, propón refactor.
3. **Cuando agregues un endpoint REST nuevo**, actualiza también el OpenAPI y, si aplica, el contrato de eventos.
4. **Cuando agregues un evento Kafka nuevo**, documenta el topic, productor, consumidor y payload en `docs/eventos.md`.
5. **Tests primero o en paralelo**, no después. Cada caso de uso debe tener test unitario; cada controller, test de slice (`@WebMvcTest`).
6. **Commits**: mensajes en imperativo, formato Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
7. **Si te piden tocar un ADR aceptado**, no lo edites: crea un ADR nuevo que lo supersede.

## Servicios y puertos locales (desarrollo)

| Servicio | Puerto | Swagger |
|----------|--------|---------|
| api-gateway | 8080 | http://localhost:8080/swagger-ui.html |
| identity-service | 8081 | http://localhost:8081/swagger-ui.html |
| inventory-service | 8082 | http://localhost:8082/swagger-ui.html |
| orders-service | 8083 | http://localhost:8083/swagger-ui.html |
| returns-service | 8084 | http://localhost:8084/swagger-ui.html |
| postgres (per service) | 5432-5435 | — |
| kafka | 9092 | — |
| kafka-ui | 8090 | http://localhost:8090 |

## Roles RBAC

`ADMIN`, `WAREHOUSE_OPERATOR`, `HOSPITAL_STAFF`, `INSPECTOR`, `AUDITOR`.
