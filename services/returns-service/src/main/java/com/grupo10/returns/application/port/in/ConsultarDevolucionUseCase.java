package com.grupo10.returns.application.port.in;

import com.grupo10.returns.domain.model.Devolucion;

import java.util.List;

public interface ConsultarDevolucionUseCase {
    Devolucion consultarPorId(String devolucionId);
    List<Devolucion> consultarTodas();
    List<Devolucion> consultarPorPedido(String pedidoId);
}
