package com.grupo10.inventory.application.port.in;

import com.grupo10.inventory.domain.model.TipoMovimiento;

public interface RegistrarMovimientoUseCase {

    MovimientoResult registrar(RegistrarMovimientoCommand command);

    record RegistrarMovimientoCommand(
            String loteId,
            TipoMovimiento tipo,
            int cantidad,
            String referenciaExterna,
            String creadoPor
    ) {}

    record MovimientoResult(
            String movimientoId,
            String loteId,
            int cantidadResultante
    ) {}
}
