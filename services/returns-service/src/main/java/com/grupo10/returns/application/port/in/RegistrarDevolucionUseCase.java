package com.grupo10.returns.application.port.in;

import com.grupo10.returns.domain.model.LineaDevolucion;

import java.util.List;

public interface RegistrarDevolucionUseCase {

    record RegistrarDevolucionCommand(
            String pedidoId,
            String solicitadoPor,
            List<LineaDevolucion> lineas
    ) {}

    record DevolucionRegistrada(String devolucionId, String numeroDevolucion) {}

    DevolucionRegistrada registrar(RegistrarDevolucionCommand command);
}
