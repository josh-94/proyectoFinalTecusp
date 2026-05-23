package com.grupo10.inventory.domain.event;

import com.grupo10.inventory.domain.model.TipoMovimiento;

import java.time.Instant;
import java.util.UUID;

public record StockMovimientoRegistradoEvent(
        String eventId,
        String loteId,
        String productoId,
        TipoMovimiento tipo,
        int cantidad,
        String referenciaExterna,
        Instant occurredAt
) {
    public static StockMovimientoRegistradoEvent of(
            String loteId, String productoId, TipoMovimiento tipo,
            int cantidad, String referenciaExterna) {
        return new StockMovimientoRegistradoEvent(
                UUID.randomUUID().toString(),
                loteId,
                productoId,
                tipo,
                cantidad,
                referenciaExterna,
                Instant.now()
        );
    }
}
