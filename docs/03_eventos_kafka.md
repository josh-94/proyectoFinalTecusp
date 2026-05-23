# Mapa de Eventos Kafka

Este documento describe los topics, productores y consumidores del sistema. Sirve como contrato de comunicación asíncrona.

## Convenciones

- **Topic naming**: `<dominio>.<entidad>.<accion-en-pasado>` en kebab-case minúsculas. Ej: `orders.pedido.creado`.
- **Formato de payload**: JSON con envelope `{ eventId, eventType, occurredAt, producer, version, data }`.
- **Idempotencia**: cada consumidor guarda `eventId` procesados para evitar duplicados.
- **Retención**: 7 días en entornos de desarrollo, 30 días en producción.
- **Particiones**: por defecto 3 particiones por topic; clave de partición = `aggregateId` para preservar orden por agregado.

## Topics

| # | Topic | Productor | Consumidor(es) | Propósito |
|---|-------|-----------|----------------|-----------|
| 1 | `orders.pedido.creado` | orders-service | inventory-service | Disparar reserva de stock |
| 2 | `inventory.stock.reservado` | inventory-service | orders-service | Confirmar reserva exitosa |
| 3 | `inventory.stock.insuficiente` | inventory-service | orders-service | Rechazar pedido por falta de stock |
| 4 | `orders.pedido.confirmado` | orders-service | inventory-service, returns-service | Notificar pedido listo para despacho |
| 5 | `orders.pedido.despachado` | orders-service | returns-service | Habilitar futuras devoluciones |
| 6 | `orders.pedido.cancelado` | orders-service | inventory-service | Liberar stock reservado |
| 7 | `returns.devolucion.solicitada` | returns-service | inventory-service (info), orders-service | Iniciar proceso de inspección |
| 8 | `returns.devolucion.aprobada` | returns-service | inventory-service | Reingresar lote al stock |
| 9 | `returns.devolucion.rechazada` | returns-service | inventory-service | Registrar baja por descarte |
| 10 | `inventory.alerta.caducidad` | inventory-service | (futuro: notifications-service) | Alertar productos próximos a vencer |
| 11 | `inventory.alerta.stock-minimo` | inventory-service | (futuro: notifications-service) | Alertar stock por debajo del umbral |

## Diagrama de flujo: creación de un pedido (Saga)

```
Cliente Flutter
    │
    ▼
[Gateway] ──► POST /orders ──► orders-service
                                    │
                                    ├── guarda Pedido(estado=PENDIENTE_STOCK)
                                    │
                                    └── publica  orders.pedido.creado  ──┐
                                                                          │
                                          ┌───────────────────────────────┘
                                          ▼
                                  inventory-service
                                          │
                                          ├── verifica stock por lote+FEFO
                                          │
                                          ├── ¿hay stock?
                                          │    │
                                          │    ├── SÍ → reserva y publica inventory.stock.reservado
                                          │    │
                                          │    └── NO → publica inventory.stock.insuficiente
                                          │
                                          ▼
                                  orders-service consume
                                          │
                                          ├── estado=CONFIRMADO → publica orders.pedido.confirmado
                                          │
                                          └── estado=RECHAZADO  → notifica al cliente
```

## Diagrama de flujo: devolución parcial

```
Personal hospitalario abre devolución en Flutter
    │
    ▼
[Gateway] ──► POST /returns ──► returns-service
                                     │
                                     ├── valida pedido vía GET orders-service (REST sync)
                                     │
                                     ├── crea Devolucion(estado=EN_INSPECCION)
                                     │
                                     └── publica returns.devolucion.solicitada

Inspector marca ítem por ítem en Flutter
    │
    ▼
returns-service registra ResultadoInspeccion
    │
    └── al finalizar inspección:
            │
            ├── si ítem OK → publica returns.devolucion.aprobada
            │                  │
            │                  ▼
            │             inventory-service reingresa lote al stock
            │
            └── si ítem descartado → publica returns.devolucion.rechazada
                                       │
                                       ▼
                                  inventory-service registra baja
```

## Esquema de evento (ejemplo)

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "orders.pedido.creado",
  "occurredAt": "2026-05-15T14:32:11Z",
  "producer": "orders-service",
  "version": 1,
  "data": {
    "pedidoId": "PED-2026-000123",
    "solicitanteId": "USR-456",
    "area": "UCI-2",
    "items": [
      { "productoId": "PRD-A1", "cantidad": 5 },
      { "productoId": "PRD-B7", "cantidad": 2 }
    ]
  }
}
```
