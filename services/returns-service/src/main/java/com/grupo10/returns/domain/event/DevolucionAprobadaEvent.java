package com.grupo10.returns.domain.event;

import com.grupo10.returns.domain.model.LineaDevolucion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DevolucionAprobadaEvent(
        String eventId,
        String eventType,
        String occurredAt,
        String producer,
        int version,
        DevolucionData data
) {
    public record DevolucionData(
            String devolucionId,
            String pedidoId,
            List<LineaItem> lineas
    ) {}

    public record LineaItem(String loteId, int cantidad) {}

    public static DevolucionAprobadaEvent of(String devolucionId, String pedidoId,
                                              List<LineaDevolucion> lineas) {
        var items = lineas.stream()
                .map(l -> new LineaItem(l.loteId(), l.cantidadDevuelta()))
                .toList();
        return new DevolucionAprobadaEvent(
                UUID.randomUUID().toString(),
                "DevolucionAprobada",
                Instant.now().toString(),
                "returns-service",
                1,
                new DevolucionData(devolucionId, pedidoId, items)
        );
    }
}
