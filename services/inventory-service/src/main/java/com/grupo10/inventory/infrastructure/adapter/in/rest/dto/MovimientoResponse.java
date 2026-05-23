package com.grupo10.inventory.infrastructure.adapter.in.rest.dto;

public record MovimientoResponse(
        String movimientoId,
        String loteId,
        int cantidadResultante
) {}
