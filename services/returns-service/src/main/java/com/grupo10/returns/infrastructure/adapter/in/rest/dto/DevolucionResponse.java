package com.grupo10.returns.infrastructure.adapter.in.rest.dto;

import java.time.Instant;
import java.util.List;

public record DevolucionResponse(
        String id,
        String numeroDevolucion,
        String pedidoId,
        String solicitadoPor,
        String estado,
        String observaciones,
        String motivoRechazo,
        List<LineaResponse> lineas,
        Instant creadoEn,
        Instant actualizadoEn
) {
    public record LineaResponse(String loteId, int cantidadDevuelta, String motivoDevolucion) {}
}
