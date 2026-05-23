package com.grupo10.orders.infrastructure.adapter.in.rest.dto;

import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        String id,
        String numeroPedido,
        String solicitadoPor,
        String hospitalDestino,
        String estado,
        String motivoRechazo,
        List<LineaResponse> lineas,
        Instant creadoEn,
        Instant actualizadoEn
) {
    public record LineaResponse(String loteId, int cantidad, String descripcion) {}
}
