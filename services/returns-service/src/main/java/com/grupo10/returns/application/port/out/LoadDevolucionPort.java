package com.grupo10.returns.application.port.out;

import com.grupo10.returns.domain.model.Devolucion;

import java.util.List;
import java.util.Optional;

public interface LoadDevolucionPort {
    Optional<Devolucion> findById(String devolucionId);
    List<Devolucion> findAll();
    List<Devolucion> findByPedidoId(String pedidoId);
}
