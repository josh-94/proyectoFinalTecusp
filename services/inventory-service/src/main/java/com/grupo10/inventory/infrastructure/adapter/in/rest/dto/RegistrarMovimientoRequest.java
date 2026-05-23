package com.grupo10.inventory.infrastructure.adapter.in.rest.dto;

import com.grupo10.inventory.domain.model.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarMovimientoRequest(
        @NotBlank String loteId,
        @NotNull TipoMovimiento tipo,
        @Min(1) int cantidad,
        String referenciaExterna
) {}
