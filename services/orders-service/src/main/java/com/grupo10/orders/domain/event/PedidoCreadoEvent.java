package com.grupo10.orders.domain.event;

import com.grupo10.orders.domain.model.LineaDePedido;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PedidoCreadoEvent(
        String eventId,
        String eventType,
        String occurredAt,
        String producer,
        int version,
        PedidoData data
) {
    public record PedidoData(
            String pedidoId,
            String solicitanteId,
            String area,
            List<ItemPedido> items
    ) {}

    public record ItemPedido(String loteId, int cantidad) {}

    public static PedidoCreadoEvent of(String pedidoId, String solicitanteId,
                                       String hospitalDestino, List<LineaDePedido> lineas) {
        var items = lineas.stream()
                .map(l -> new ItemPedido(l.loteId(), l.cantidad()))
                .toList();
        return new PedidoCreadoEvent(
                UUID.randomUUID().toString(),
                "PedidoCreado",
                Instant.now().toString(),
                "orders-service",
                1,
                new PedidoData(pedidoId, solicitanteId, hospitalDestino, items)
        );
    }
}
