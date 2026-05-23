# Architectural Decision Records (ADR)
## Sistema Distribuido de Gestión de Inventario Médico y Devoluciones
**Grupo 10 — Trabajo Individual — Tipo A**
**Fecha:** Mayo 2026

> Formato basado en Michael Nygard (Context · Decision · Consequences). Cada ADR es inmutable: si una decisión cambia, se crea un ADR nuevo que la supersede.

---

## ADR-001: Adoptar arquitectura de microservicios sobre monolito

**Estado:** Aceptado

**Contexto.** El sistema debe gestionar tres dominios con ciclos de cambio y patrones de carga distintos (inventario, pedidos, devoluciones). Un monolito acoplaría las tres áreas en un mismo despliegue, una misma base de datos y un mismo ciclo de release.

**Decisión.** Se adopta una arquitectura de microservicios con cuatro servicios independientes: `identity-service`, `inventory-service`, `orders-service`, `returns-service`. Cada uno se despliega por separado, tiene su propia base de datos y expone su propia API.

**Consecuencias.**
- Positivas: escalado independiente, despliegues aislados, resiliencia ante fallos parciales, equipos independientes a futuro.
- Negativas: mayor complejidad operativa (orquestación, observabilidad distribuida), transacciones distribuidas, costo de infraestructura mayor que un monolito equivalente.

---

## ADR-002: Aplicar Clean Architecture + Arquitectura Hexagonal por servicio

**Estado:** Aceptado

**Contexto.** Se busca que la lógica de negocio sea independiente del framework (Spring Boot), de la base de datos (PostgreSQL) y del transporte (REST/Kafka), para facilitar pruebas y futuras migraciones tecnológicas.

**Decisión.** Cada microservicio se estructura en tres capas (domain · application · infrastructure) con puertos y adaptadores. El dominio no importa nada de Spring ni de JPA. Los casos de uso definen puertos de entrada y salida; los adaptadores los implementan.

**Consecuencias.**
- Positivas: testabilidad del dominio sin levantar el contexto Spring; reemplazo de adaptadores sin tocar reglas de negocio; código auto-documentado por capas.
- Negativas: curva de aprendizaje; más archivos y boilerplate; tentación de saltarse capas si la disciplina cede.

---

## ADR-003: Base de datos PostgreSQL por servicio (Database per Service)

**Estado:** Aceptado

**Contexto.** Compartir una sola base de datos entre microservicios produce acoplamiento estructural: un cambio de esquema obliga a coordinar despliegues, y los servicios pierden autonomía.

**Decisión.** Cada microservicio tiene su propia instancia lógica de PostgreSQL. Ningún servicio accede a tablas de otro servicio directamente. La sincronización entre datos se hace exclusivamente vía API (REST) o vía eventos (Kafka).

**Consecuencias.**
- Positivas: encapsulamiento total del esquema; despliegues independientes; libertad para evolucionar el modelo.
- Negativas: no hay transacciones ACID cross-service (se mitiga con patrón Saga); duplicación parcial de datos (ej: `inventory-service` guarda un snapshot del producto para auditoría).

---

## ADR-004: Comunicación mixta — REST síncrono + Kafka asíncrono

**Estado:** Aceptado

**Contexto.** Algunas operaciones requieren respuesta inmediata (validar stock antes de crear un pedido) y otras pueden coreografiarse en background (reservar stock, notificar a inspección).

**Decisión.**
- **REST** se usa para operaciones síncronas iniciadas por el cliente o entre servicios cuando se necesita confirmación inmediata. Documentación con OpenAPI/Swagger.
- **Kafka** se usa para eventos de dominio publicados después de que algo ocurrió. Los consumidores reaccionan asíncronamente.

**Consecuencias.**
- Positivas: cada operación usa el estilo que mejor le sirve; los servicios pueden estar caídos sin perder eventos (Kafka los retiene).
- Negativas: dos modos mentales que mantener; necesidad de garantizar idempotencia en consumidores; complejidad de testing.

---

## ADR-005: Autenticación con JWT + Autorización con RBAC

**Estado:** Aceptado

**Contexto.** El sistema tiene roles diferenciados (admin, operador de almacén, personal hospitalario, inspector, auditor) y necesita un mecanismo de autenticación que no requiera sesión en servidor para escalar horizontalmente.

**Decisión.** El `identity-service` emite tokens JWT firmados (RS256) con los claims `sub`, `roles`, `exp`, `iss`. El API Gateway valida la firma y propaga el token a los servicios downstream. Cada servicio decide la autorización por rol usando Spring Security con anotaciones `@PreAuthorize`.

**Consecuencias.**
- Positivas: stateless, escalable, estándar industrial.
- Negativas: revocación inmediata difícil (se mitiga con expiraciones cortas y refresh tokens); tamaño del header crece con la cantidad de claims.

---

## ADR-006: API Gateway como único punto de entrada

**Estado:** Aceptado

**Contexto.** Exponer cuatro servicios directamente al cliente Flutter complica CORS, autenticación, rate limiting y versionado.

**Decisión.** Se implementa un API Gateway (Spring Cloud Gateway) que: enruta a los servicios internos, valida JWT, aplica rate limiting, expone una sola URL pública.

**Consecuencias.**
- Positivas: un único punto de seguridad; los servicios internos pueden vivir en una red privada.
- Negativas: single point of failure (se mitiga con réplicas); latencia adicional de un hop.

---

## ADR-007: Flutter como cliente multiplataforma único

**Estado:** Aceptado

**Contexto.** Se debe entregar al menos una aplicación cliente. Mantener apps nativas iOS + Android + Web duplicaría el esfuerzo para un trabajo individual.

**Decisión.** Se desarrolla una sola aplicación Flutter que compila a Android, iOS y Web desde una misma base de código. Se usa Riverpod o Bloc para gestión de estado y `dio` para HTTP.

**Consecuencias.**
- Positivas: una sola base de código para todas las plataformas; ecosistema maduro; hot reload acelera el desarrollo.
- Negativas: rendimiento ligeramente inferior a nativo; algunas integraciones del sistema operativo requieren plugins externos.

---

## ADR-008: Contenedorización con Docker y despliegue local con Docker Compose

**Estado:** Aceptado

**Contexto.** El entorno local debe levantar PostgreSQL (×4), Kafka, Zookeeper, los servicios y el gateway de forma reproducible.

**Decisión.** Cada servicio se empaqueta en una imagen Docker. Se provee un `docker-compose.yml` para desarrollo local. Kubernetes queda como roadmap futuro pero se diseña pensando en ello (12-Factor App).

**Consecuencias.**
- Positivas: "funciona en mi máquina" deja de ser problema; setup en minutos.
- Negativas: requiere recursos de máquina (RAM); aprender Compose si no se conoce.

---

## ADR-009: Patrón Saga (coreografía) para transacciones distribuidas

**Estado:** Aceptado

**Contexto.** Crear un pedido involucra dos servicios: `orders-service` (crear el pedido) y `inventory-service` (reservar stock). No hay transacción ACID que abarque ambos.

**Decisión.** Se implementa el patrón Saga por coreografía vía Kafka:
1. `orders-service` crea el pedido en estado `PENDIENTE_STOCK` y publica `PedidoCreado`.
2. `inventory-service` consume, reserva stock y publica `StockReservado` o `StockInsuficiente`.
3. `orders-service` consume y transiciona el pedido a `CONFIRMADO` o `RECHAZADO`.

**Consecuencias.**
- Positivas: consistencia eventual sin coordinador central; cada servicio es autónomo.
- Negativas: complejidad de debug; necesidad de eventos de compensación; modelo mental nuevo para el equipo.

---

## ADR-010: Gestión de migraciones de BD con Flyway

**Estado:** Aceptado

**Contexto.** Cada servicio tiene su propio esquema; los cambios deben versionarse y aplicarse en orden.

**Decisión.** Cada servicio incluye scripts de migración versionados en `src/main/resources/db/migration` (`V1__init.sql`, `V2__add_lot_table.sql`, …) ejecutados por Flyway al arrancar.

**Consecuencias.**
- Positivas: esquema reproducible; historial visible en git; rollback documentado.
- Negativas: rigor en nomenclatura; cada cambio implica un script nuevo (no se editan los anteriores).

---

## ADR-011: Observabilidad mínima — logs estructurados + actuator + correlación de trazas

**Estado:** Aceptado

**Contexto.** En sistemas distribuidos, una sola petición puede atravesar varios servicios. Sin trazabilidad, debuggear es imposible.

**Decisión.**
- Logs en formato JSON (`logback-spring.xml`) con campo `traceId` propagado por el gateway.
- `spring-boot-starter-actuator` expone `/health`, `/info`, `/metrics`.
- (Opcional roadmap) Prometheus + Grafana + Loki.

**Consecuencias.**
- Positivas: troubleshooting realista; preparado para integrar herramientas APM más adelante.
- Negativas: leve overhead en el log; disciplina para no loguear PII.

---

## Resumen tabular

| ADR | Decisión | Justificación principal |
|-----|----------|--------------------------|
| 001 | Microservicios | Ciclos de cambio y carga diferentes por dominio |
| 002 | Clean + Hexagonal | Independencia del framework, testabilidad |
| 003 | BD por servicio | Encapsulamiento, autonomía |
| 004 | REST + Kafka | Cada operación con el estilo adecuado |
| 005 | JWT + RBAC | Stateless y estándar |
| 006 | API Gateway | Punto único de seguridad y enrutado |
| 007 | Flutter | Multiplataforma con un solo código |
| 008 | Docker + Compose | Entorno reproducible |
| 009 | Saga coreografía | Consistencia eventual sin coordinador |
| 010 | Flyway | Migraciones versionadas |
| 011 | Observabilidad básica | Trazabilidad mínima viable |
