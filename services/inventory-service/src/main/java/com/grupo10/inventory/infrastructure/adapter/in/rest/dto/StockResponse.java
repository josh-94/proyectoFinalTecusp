package com.grupo10.inventory.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;

public record StockResponse(
        String id,
        String productoId,
        String numeroLote,
        LocalDate fechaVencimiento,
        int cantidadDisponible,
        boolean vencido
) {}
