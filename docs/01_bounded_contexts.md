# Bounded Contexts — Sistema de Gestión de Inventario Médico y Devoluciones

> Documento de trabajo. Sirve para alimentar el documento de arquitectura (secciones 2.1, 3.1, 3.2, 3.3) y para guiar la implementación.

## 1. Visión general del dominio

El sistema atiende tres procesos críticos del flujo hospitalario de insumos:

1. **Gestión de inventario médico**: catálogo de productos (medicamentos, instrumental, insumos descartables), control de stock por lote y fecha de vencimiento, alertas de stock mínimo y caducidad.
2. **Gestión de pedidos**: solicitudes de áreas hospitalarias (UCI, quirófano, farmacia) o de clientes externos al almacén central. Reserva de stock, despacho, confirmación de entrega.
3. **Gestión de devoluciones**: retornos parciales o totales de instrumental médico (estéril o no estéril). Cada ítem devuelto pasa por inspección y puede reincorporarse al inventario, descartarse o enviarse a reproceso.

## 2. Bounded Contexts identificados

| # | Contexto | Responsabilidad nuclear | Datos que posee | Lenguaje ubicuo |
|---|----------|-------------------------|-----------------|-----------------|
| 1 | **Identity & Access** | Autenticación, emisión de JWT, gestión de usuarios y roles (RBAC). | Usuarios, roles, permisos, refresh tokens. | Usuario, Rol, Permiso, Token |
| 2 | **Inventory** | Catálogo de productos médicos, stock por lote, ubicaciones, alertas de caducidad. | Producto, Lote, Stock, Movimiento de stock, Alerta. | Producto, Lote, SKU, Stock, Movimiento, Caducidad |
| 3 | **Orders** | Ciclo de vida del pedido: creación, reserva, despacho, confirmación. | Pedido, LíneaDePedido, EstadoPedido, ReservaStock. | Pedido, Línea, Estado, Reserva, Despacho |
| 4 | **Returns** | Solicitudes de devolución, inspección, decisión final (reingresar / descartar / reprocesar). | Devolución, ÍtemDevuelto, ResultadoInspección, EstadoDevolución. | Devolución, Inspección, Reingreso, Descarte |

> Para un trabajo individual con alcance funcional mínimo, **estos cuatro servicios son suficientes**. Notificaciones y reportes se pueden cubrir desde el gateway o dejarse como roadmap futuro en el doc.

## 3. Roles RBAC sugeridos

| Rol | Permisos clave |
|-----|----------------|
| `ADMIN` | Gestión total: usuarios, productos, configuración. |
| `WAREHOUSE_OPERATOR` | Crear lotes, registrar movimientos de stock, procesar despachos. |
| `HOSPITAL_STAFF` | Crear pedidos, ver estado de sus pedidos, iniciar devoluciones. |
| `INSPECTOR` | Realizar inspección de devoluciones y registrar resultados. |
| `AUDITOR` | Solo lectura sobre movimientos, pedidos y devoluciones. |

## 4. Mapa de contextos (Context Map estilo DDD)

```
[Identity] ---<token JWT>---> (todos los demás vía gateway)

[Orders]  --REST sync-->  [Inventory]    (validar disponibilidad al crear pedido)
[Orders]  --Kafka asyn-->  [Inventory]   (PedidoCreado → reservar stock)
[Inventory] --Kafka asyn--> [Orders]     (StockReservado / StockInsuficiente)

[Returns] --REST sync-->  [Orders]       (validar que el pedido original existe)
[Returns] --Kafka asyn--> [Inventory]    (DevolucionAprobada → reingresar lote)
```

- **REST síncrono** se usa solo cuando el cliente necesita la respuesta inmediata (ej: ¿puedo crear este pedido?).
- **Kafka asíncrono** se usa para coreografiar el resto del flujo y desacoplar servicios.

## 5. Aplicación de Clean Architecture + Hexagonal por servicio

Cada microservicio replicará la siguiente estructura interna:

```
src/main/java/com/grupo10/<servicio>/
├── domain/                  # Núcleo: entidades, value objects, eventos de dominio, reglas de negocio puras
│   ├── model/
│   ├── event/
│   └── exception/
├── application/             # Casos de uso, puertos (interfaces de entrada/salida)
│   ├── usecase/
│   ├── port/
│   │   ├── in/              # Puertos de entrada (lo que la app ofrece)
│   │   └── out/             # Puertos de salida (lo que la app necesita)
│   └── service/
├── infrastructure/          # Adaptadores: BD, Kafka, REST clients, configuración
│   ├── adapter/
│   │   ├── in/
│   │   │   ├── rest/        # Controllers REST
│   │   │   └── messaging/   # Consumers Kafka
│   │   └── out/
│   │       ├── persistence/ # Repos JPA, mappers
│   │       └── messaging/   # Producers Kafka
│   └── config/
└── <Servicio>Application.java
```

Regla de oro: las flechas de dependencia apuntan **siempre hacia adentro** (infrastructure → application → domain). El dominio no conoce ni a Spring, ni a JPA, ni a Kafka.

## 6. Por qué este recorte de servicios

- **Cohesión alta**: cada servicio tiene una sola razón de cambio.
- **Acoplamiento bajo**: los servicios solo se conocen por contratos (REST + esquemas de eventos), nunca por BD compartida.
- **Escalabilidad independiente**: el servicio de Inventory probablemente recibe muchas más lecturas que Returns; cada uno escala por separado.
- **Equipos independientes (a futuro)**: aunque sea trabajo individual, la arquitectura se justifica pensando en evolución.
