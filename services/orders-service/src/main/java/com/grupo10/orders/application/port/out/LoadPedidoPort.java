package com.grupo10.orders.application.port.out;

import com.grupo10.orders.domain.model.Pedido;

import java.util.List;
import java.util.Optional;

public interface LoadPedidoPort {
    Optional<Pedido> findById(String pedidoId);
    List<Pedido> findAll();
    List<Pedido> findBySolicitadoPor(String solicitadoPor);
}
