package com.grupo10.orders.infrastructure.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CrearPedidoRequest(
        @NotBlank String hospitalDestino,
        @NotNull @NotEmpty @Valid List<LineaDePedidoRequest> lineas
) {
    public record LineaDePedidoRequest(
            @NotBlank String loteId,
            @Positive int cantidad,
            @NotBlank String descripcion
    ) {}
}
