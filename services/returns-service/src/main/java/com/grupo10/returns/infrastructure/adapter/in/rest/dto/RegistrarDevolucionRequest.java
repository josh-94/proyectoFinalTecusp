package com.grupo10.returns.infrastructure.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record RegistrarDevolucionRequest(
        @NotBlank String pedidoId,
        @NotNull @NotEmpty @Valid List<LineaDevolucionRequest> lineas
) {
    public record LineaDevolucionRequest(
            @NotBlank String loteId,
            @Positive int cantidadDevuelta,
            @NotBlank String motivoDevolucion
    ) {}
}
