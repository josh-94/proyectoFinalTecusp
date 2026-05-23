package com.grupo10.orders.application.port.in;

import com.grupo10.orders.domain.model.Pedido;

import java.util.List;

public interface ConsultarPedidoUseCase {
    Pedido consultarPorId(String pedidoId);
    List<Pedido> consultarTodos();
    List<Pedido> consultarPorSolicitante(String solicitadoPor);
}
